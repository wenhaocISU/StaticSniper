package analysisTools;

import java.util.HashMap;
import java.util.Map;

import main.Paths;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.Stmt;
import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticField;
import staticFamily.StaticMethod;
import staticFamily.StaticStmt;



public class Sooot {
	
	private static StaticApp testApp;

	public static void generateAppData(StaticApp staticApp) {
		
		testApp = staticApp;

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.initAnalysis", new SceneTransformer() {
			@Override
			protected void internalTransform(String phaseName, Map<String, String> options) {
				for (SootClass sootC : Scene.v().getApplicationClasses()) {
					StaticClass c = testApp.findClassByName(sootC.getName());
					boolean needAddC = false;
					if (c == null) {
						needAddC = true;
						c = new StaticClass(sootC.getName());
					}
					c = extractClassInfo(sootC, c);
					for (SootField sootF : sootC.getFields()) {
						StaticField f = c.findFieldByFullSignature(sootF.getSignature());
						boolean needAddF = false;
						if (f == null) {
							needAddF = true;
							f = new StaticField(sootF.getSignature());
						}
						f = extractFieldInfo(sootF, f);
						f.setDeclaringClass(c.getName());
						if (needAddF) {
							c.addField(f);
						}
					}
					for (SootMethod sootM : sootC.getMethods()) {
						StaticMethod m = c.findMethodByFullSignature(sootM.getSignature());
						boolean needAddM = false;
						if (m == null) {
							needAddM = true;
							m = new StaticMethod(sootM.getSignature());
						}
						m = extractMethodInfo(sootM, m);
						m.setDeclaringClass(c.getName());
						if (needAddM) {
							c.addMethod(m);
						}
					}
					if (needAddC) {
						testApp.addClass(c);
					}
				}
			}}));
		
		PackManager.v().getPack("jtp").add(new Transform("jtp.myTransform", new BodyTransformer() {
			@Override
			protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
				StaticMethod m = testApp.findMethodByFullSignature(b.getMethod().getSignature());
				StaticClass c = m.getDeclaringClass(testApp);
				m.setHasBody(true);
				m.setJimpleCode(b.toString());
				for (Local l : b.getLocals()) {
					m.addLocalVariable(l.getName(), l.getType().toString());
				}
				for (Local l : b.getParameterLocals()) {
					m.addParamVariable(l.getName(), l.getType().toString());
				}
				for (Unit u : b.getUnits()) {
					final StaticStmt s = new StaticStmt(u.toString());
					Stmt stmt = (Stmt) u;
					u.apply(new JimpleStmtSolver(s));
					if (stmt.containsFieldRef()) {
						s.setContainsFieldRef(true);
						s.setTargetSignature(stmt.getFieldRef().getField().getSignature());
						SootField refTargetF = stmt.getFieldRef().getField();
						SootClass refTargetC = refTargetF.getDeclaringClass();
						String stmtTargetSig = s.getTargetSignature();
						String stmtTargetC = stmtTargetSig.substring(1, stmtTargetSig.indexOf(": "));
						String stmtTargetSubsig = stmtTargetSig.substring(stmtTargetSig.indexOf(": ")+2,
													stmtTargetSig.length()-1);
						if (refTargetC.isApplicationClass()
							&& refTargetF.getSubSignature().equals(stmtTargetSubsig)) {
							if (refTargetC.getName().equals(stmtTargetC)) {
								StaticField tgtF = testApp.findFieldByFullSignature(stmtTargetSig);
								tgtF.addInCallSource(m.getFullJimpleSignature());
								m.addFieldRef(tgtF.getFullJimpleSignature());
							} else {
								StaticField tgtF = new StaticField(stmtTargetSig);
								tgtF.setDeclaringClass(refTargetC.getName());
								tgtF.addInCallSource(m.getFullJimpleSignature());
								m.addFieldRef(tgtF.getFullJimpleSignature());
								StaticClass tgtC = testApp.findClassByName(stmtTargetC);
								tgtC.addField(tgtF);
							}
						}
					}
					else if (s.containsMethodCall()) {
						s.setContainsMethodCall(true);
						s.setTargetSignature(stmt.getInvokeExpr().getMethod().getSignature());
						SootMethod refTargetM = stmt.getInvokeExpr().getMethod();
						SootClass refTargetC = refTargetM.getDeclaringClass();
						String stmtTargetSig = s.getTargetSignature();
						String stmtTargetC = stmtTargetSig.substring(1, stmtTargetSig.indexOf(": "));
						String stmtTargetSubsig = stmtTargetSig.substring(stmtTargetSig.indexOf(": ")+2,
													stmtTargetSig.length()-1);
						if (refTargetC.isApplicationClass() &&
							refTargetM.getSubSignature().equals(stmtTargetSubsig)) {
							if (refTargetC.getName().equals(stmtTargetC)) {
								StaticMethod tgtM = testApp.findMethodByFullSignature(stmtTargetSig);
								tgtM.addInCallSource(m.getFullJimpleSignature());
								m.addOutCallTarget(tgtM.getFullJimpleSignature());
							} else {
								StaticMethod tgtM = new StaticMethod(stmtTargetSig);
								tgtM.setDeclaringClass(refTargetC.getName());
								tgtM.addInCallSource(m.getFullJimpleSignature());
								m.addOutCallTarget(tgtM.getFullJimpleSignature());
								StaticClass tgtC = testApp.findClassByName(stmtTargetC);
								tgtC.addMethod(tgtM);
							}
						}
					}
					m.addStmt(s);
				}
			}
		}));

		String[] args = {
		"-d", Paths.appDataDir + testApp.getTestApp() + "/soot/Jimples",
		"-f", "J",
		"-src-prec", "apk",
		"-ire", "-allow-phantom-refs", "-w",
		"-force-android-jar", Paths.androidJarPath,
		"-process-path", testApp.getTestApp().getAbsolutePath() };
		soot.Main.main(args);
	}

	private static StaticClass extractClassInfo(SootClass sootC, StaticClass c) {
		c.setIsAbstract(sootC.isAbstract());
		c.setIsInDEX(true);
		c.setIsInterface(sootC.isInterface());
		c.setModifiers(sootC.getModifiers());
		c.setSuperClass(sootC.getSuperclass().getName());
		if (sootC.getInterfaceCount()>0)
			for (SootClass intf : sootC.getInterfaces())
				c.addInterface(intf.getName());
		if (c.isInnerClass() && !sootC.getOuterClass().getName().equals(sootC.getName())
			&& sootC.getOuterClass().isApplicationClass()) {
			c.setIsInnerClass(true);
			c.setOuterClass(sootC.getOuterClass().getName());
			StaticClass outerC = testApp.findClassByName(c.getOuterClassName());
			boolean needAddOuterC = false;
			if (outerC == null) {
				needAddOuterC = true;
				outerC = new StaticClass(c.getOuterClassName());
			}
			outerC.addInnerClass(c.getName());
			if (needAddOuterC)
				testApp.addClass(outerC);
		}
		return c;
	}
	
	private static StaticField extractFieldInfo(SootField sootF, StaticField f) {
		f.setDeclaration(sootF.getDeclaration());
		f.setModifiers(sootF.getModifiers());
		f.setIsDeclaredHere(true);
		return f;
	}
	
	private static StaticMethod extractMethodInfo(SootMethod sootM, StaticMethod m) {
		m.setBytecodeSignature(sootM.getBytecodeSignature());
		m.setIsAbstract(sootM.isAbstract());
		m.setIsNative(sootM.isNative());
		m.setIsDeclaredHere(true);
		for (Type paramType : sootM.getParameterTypes())
			m.addParameterType(paramType.toString());
		return m;
	}

}

