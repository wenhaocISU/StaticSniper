package analysisTools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.Paths;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.util.Chain;
import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticField;
import staticFamily.StaticMethod;
import staticFamily.StaticStmt;


public class Soot {

	
	public static void generateAppData(final StaticApp testApp) {

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.initAnalysis", new SceneTransformer() {
			@Override
			protected void internalTransform(String arg0, Map<String, String> arg1) {
				CHATransformer.v().transform();
				Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
				for (SootClass sC : sootClasses) {
					String outerClassName = "";
					boolean isInnerClass = false;
					if (sC.isInnerClass() && !sC.getOuterClass().getName().equals(sC.getName())) {
						outerClassName = sC.getOuterClass().getName();
						isInnerClass = true;
					}
					String superClassName = "";
					if (sC.hasSuperclass())
						superClassName = sC.getSuperclass().getName();
					ArrayList<String> interfaceList = new ArrayList<String>();
					for (SootClass ic: sC.getInterfaces()) {
						interfaceList.add(ic.getName());
					}
					List<StaticField> fList = new ArrayList<StaticField>();
					for (SootField sF : sC.getFields()) {
						StaticField f = new StaticField(sF.getName(), 
							sF.getType().toString(), sF.getModifiers(), sF.getDeclaration());
						fList.add(f);
					}
					List<StaticMethod> mList = new ArrayList<StaticMethod>();
					for (SootMethod sM : sC.getMethods()) {
						String sM_jimple = "";
						ArrayList<String> parameters = new ArrayList<String>();
						for (Type pT : sM.getParameterTypes()) {
							parameters.add(pT.toString());
						}
						Map<String, String> paramVariables = new HashMap<String, String>();
						Map<String, String> localVariables = new HashMap<String, String>();
						List<StaticStmt>	statements = new ArrayList<StaticStmt>();
						if (!sM.isAbstract() && !sM.isNative())
							try {
								Body b = sM.retrieveActiveBody();
								sM_jimple = b.toString();
								for (Local l : b.getLocals()) {
									localVariables.put(l.getName(), l.getType().toString());
								}
								for (Local l : b.getParameterLocals()) {
									paramVariables.put(l.getName(), l.getType().toString());
								}
								for (Unit u :b.getUnits()) {
									Stmt stmt = (Stmt) u;
									boolean hasFieldRef = false, hasMethodCall = false;
									String targetClass = "", targetSubSig = "";
									if (stmt.containsFieldRef()) {
										SootField targetField = stmt.getFieldRef().getField();
										if (targetField.getDeclaringClass().isApplicationClass()) {
											hasFieldRef = true;
											targetClass = targetField.getDeclaringClass().getName();
											targetSubSig = targetField.getSubSignature();
										}
									} else if (stmt.containsInvokeExpr()) {
										SootMethodRef targetMethod = stmt.getInvokeExpr().getMethodRef();
										if (targetMethod.declaringClass().isApplicationClass()) {
											hasMethodCall = true;
											targetClass = targetMethod.declaringClass().getName();
											targetSubSig = targetMethod.getSubSignature().toString();
										}
									}
									StaticStmt s = new StaticStmt(stmt.toString(), 
											hasMethodCall, hasFieldRef,
											targetClass, targetSubSig);
									statements.add(s);
								}
							}	catch (Exception e) {}
						StaticMethod m = new StaticMethod(sM.getSignature(), sM.getSubSignature(), sM.getBytecodeSignature(), 
								sM.isAbstract(), sM.isNative(), sM.getModifiers(), parameters, localVariables, paramVariables, sM_jimple, statements);
						for (StaticStmt stmt : m.getStatements())
							stmt.setMethod(m);
						mList.add(m);
					}
					
					StaticClass c = new StaticClass(sC.getName(), interfaceList, fList, mList,
													isInnerClass, sC.isAbstract(), sC.isApplicationClass(),
													sC.isInterface(), sC.getModifiers(),
													superClassName, outerClassName);
					for (StaticMethod m : c.getMethodList())
						m.setDeclaringClass(c);
					for (StaticField f : c.getFieldList())
						f.setDeclaringClass(c);
					c.setStaticApp(testApp);
					testApp.addClass(c);
				}
				for (StaticClass c : testApp.getClassList()) {
					if (c.isInnerClass()) {
						if (c.getOuterClassName().equals(c.getName()))
							continue;
						StaticClass outerC = testApp.findClassByName(c.getOuterClassName());
						if (outerC != null)
							outerC.addInnerClass(c.getName());
					}
					for (StaticMethod m : c.getMethodList()) {
						for (StaticStmt stmt : m.getStatements()) {
							if (stmt.containsFieldRef()) {
								StaticField targetF = testApp.findFieldBySubSignature(stmt.getTargetClass(), stmt.getTargetSubSig());
								if (targetF != null) {
									m.addFieldRef(targetF.getFullSignature());
									targetF.addInCallSource(m.getJimpleFullSignature());
								}
							} else if (stmt.containsMethodCall()) {
								StaticMethod targetM = testApp.findMethodBySubSignature(stmt.getTargetClass(), stmt.getTargetSubSig());
								if (targetM != null) {
									m.addOutCallTarget(targetM.getJimpleFullSignature());
									targetM.addInCallSource(m.getJimpleFullSignature());
								}
							}
						}
					}
				}
			}}));
		
		String[] sootArgs = {
				"-d", testApp.outPath + "/soot/Jimples",
				"-f", "J",
				"-src-prec", "apk",
				"-ire", "-allow-phantom-refs", "-w",
				"-force-android-jar", Paths.androidJarPath,
				"-process-path", testApp.getTestApp().getAbsolutePath()
		};
		soot.Main.main(sootArgs);
	}
	
	public static int returnCounter = 1;
	
	public static void InstrumentEveryMethod(File file) throws Exception{
		
		File instrumentLog = new File(Paths.appDataDir + file.getName() + "/soot/Instrumentation/methodLevelLog.csv");
		instrumentLog.getParentFile().mkdirs();
		final PrintWriter out = new PrintWriter(new FileWriter(instrumentLog));
		PackManager.v().getPack("jtp").add(new Transform("jtp.myTransform", new BodyTransformer() {
			protected void internalTransform(final Body b, String phaseName,Map<String, String> options) {
				String className = b.getMethod().getDeclaringClass().getName();
				if (className.startsWith("android.support.v"))	return;
				if (b.getMethod().getName().contains("<clinit>") || b.getMethod().getName().contains("<init>"))	return;
				final Local l_outPrint = Jimple.v().newLocal("outPrint", RefType.v("java.io.PrintStream"));
				b.getLocals().add(l_outPrint);
				final SootMethod out_println = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				String methodSig = b.getMethod().getSignature().replace(",", " ");
				PatchingChain<Unit> units = b.getUnits();
				// first instrument the beginning
				Iterator<Unit> unitsIT = b.getUnits().snapshotIterator();
				for (int i = 0; i < b.getMethod().getParameterCount()+1; i++)
					unitsIT.next();
				Unit firstUnit = unitsIT.next();
				units.insertBefore(Jimple.v().newAssignStmt(l_outPrint,
						Jimple.v().newStaticFieldRef(Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())
						), firstUnit);
				units.insertBefore(Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr(l_outPrint, out_println.makeRef(), StringConstant.v("METHOD_STARTING," + methodSig))
						), firstUnit);
				b.validate();
				// then instrument the return
				unitsIT = b.getUnits().snapshotIterator();
				returnCounter = 1;
				while (unitsIT.hasNext()) {
					final Unit u = unitsIT.next();
					u.apply(new AbstractStmtSwitch() {
						public void caseReturnStmt(ReturnStmt rS) {
							PatchingChain<Unit> units = b.getUnits();
							String mSig = b.getMethod().getSignature().replace(",", " ");
							units.insertBefore(Jimple.v().newAssignStmt(l_outPrint,
									Jimple.v().newStaticFieldRef(Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())
									), u);
							units.insertBefore(Jimple.v().newInvokeStmt(
									Jimple.v().newVirtualInvokeExpr(l_outPrint, out_println.makeRef(), StringConstant.v("METHOD_RETURNING," + mSig + "," + returnCounter))
									), u);
							returnCounter++;
						}
						public void caseReturnVoidStmt(ReturnVoidStmt rS) {
							PatchingChain<Unit> units = b.getUnits();
							String mSig = b.getMethod().getSignature().replace(",", " ");
							units.insertBefore(Jimple.v().newAssignStmt(l_outPrint,
									Jimple.v().newStaticFieldRef(Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())
									), u);
							units.insertBefore(Jimple.v().newInvokeStmt(
									Jimple.v().newVirtualInvokeExpr(l_outPrint, out_println.makeRef(), StringConstant.v("METHOD_RETURNING," + mSig + "," + returnCounter))
									), u);
							returnCounter++;
						}
					});
				}
				b.validate();
				out.write(className + "," + b.getMethod().getName() + "\n");
			}
		}));

		String[] args = {};
		List<String> argsList = new ArrayList<String>(Arrays.asList(args));
		argsList.addAll(Arrays.asList(new String[] {
				"-d", Paths.appDataDir + file.getName() + "/soot/Instrumentation",
				"-f", "dex",
				"-src-prec", "apk",
				"-ire", "-allow-phantom-refs", "-w",
				"-force-android-jar", Paths.androidJarPath,
				"-process-path", file.getAbsolutePath()	}));
		args = argsList.toArray(new String[0]);
		File outFile = new File(Paths.appDataDir + file.getName() + "/soot/Instrumentation/" + file.getName());
		if (outFile.exists())	outFile.delete();
		Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
		soot.Main.main(args);
		soot.G.reset();
		out.close();
	}

}
