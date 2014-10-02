package analysisTools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import soot.Transform;
import soot.Type;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
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
			protected void internalTransform(String phaseName, Map<String, String> options) {
				Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
				for (SootClass sootC : sootClasses) {
					StaticClass c = testApp.findClassByName(sootC.getName());
					boolean needAddClass = false;
					if (c == null) {
						needAddClass = true;
						c = new StaticClass(sootC.getName());
					}
					// class attributes
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
					// fields
					for (SootField sootF : sootC.getFields()) {
						StaticField f = c.findFieldByFullSignature(sootF.getSignature());
						boolean needAddF = false;
						if (f == null) {
							needAddF = true;
							f = new StaticField(sootF.getSignature());
						}
						f.setDeclaration(sootF.getDeclaration());
						f.setDeclaringClass(c.getName());
						f.setModifiers(sootF.getModifiers());
						if (needAddF)
							c.addField(f);
					}
					// methods
					for (SootMethod sootM : sootC.getMethods()) {
						StaticMethod m = c.findMethodByFullSignature(sootM.getSignature());
						boolean needAddM = false;
						if (m == null) {
							needAddM = true;
							m = new StaticMethod(sootM.getSignature());
						}
						m.setBytecodeSignature(sootM.getBytecodeSignature());
						m.setDeclaringClass(c.getName());
						m.setIsAbstract(sootM.isAbstract());
						m.setIsNative(sootM.isNative());
						for (Type paramType : sootM.getParameterTypes())
							m.addParameterType(paramType.toString());
						if (!m.isAbstract() && !m.isNative())
						try {
							Body b = sootM.retrieveActiveBody();
							if (b != null) {
								m.setHasBody(true);
								m.setJimpleCode(b.toString());
								for (Unit u : b.getUnits()) {
									Stmt sootS = (Stmt) u;
									StaticStmt s = new StaticStmt(sootS.toString());
									if (sootS.containsFieldRef()) {
										s.setContainsFieldRef(true);
										SootField targetSF = sootS.getFieldRef().getField();
										s.setTargetSignature(targetSF.getSignature());
										SootClass targetSC = targetSF.getDeclaringClass();
										if (targetSC.isApplicationClass()) {
											m.addFieldRef(targetSF.getSignature());
											StaticClass targetC = testApp.findClassByName(targetSC.getName());
											if (targetSC.getName().equals(c.getName()))
												targetC = c;
											StaticField targetF = testApp.findFieldByFullSignature(targetSF.getSignature());
											boolean needAddTF = false, needAddTC = false;
											if (targetC == null) {
												needAddTC = true;
												targetC = new StaticClass(targetSC.getName());
											}
											if (targetF == null) {
												needAddTF = true;
												targetF = new StaticField(targetSF.getSignature());
											}
											targetF.addInCallSource(m.getFullJimpleSignature());
											if (needAddTF)
												targetC.addField(targetF);
											if (needAddTC)
												testApp.addClass(targetC);
										}
									} else if (sootS.containsInvokeExpr()) {
										s.setContainsMethodCall(true);
										SootMethod targetSM = sootS.getInvokeExpr().getMethod();
										s.setTargetSignature(targetSM.getSignature());
										SootClass targetSC = targetSM.getDeclaringClass();
										if (targetSC.isApplicationClass())
										try {
											Body targetB = targetSM.retrieveActiveBody();
											if (targetB != null) {
												m.addOutCallTarget(targetSM.getSignature());
												StaticClass targetC = testApp.findClassByName(targetSC.getName());
												if (targetSC.getName().equals(c.getName()))
													targetC = c;
												StaticMethod targetM = testApp.findMethodByFullSignature(targetSM.getSignature());
												boolean needAddTC = false, needAddTM = false;
												if (targetC == null) {
													needAddTC = true;
													targetC = new StaticClass(targetSC.getName());
												}
												if (targetM == null) {
													needAddTM = true;
													targetM = new StaticMethod(targetSM.getSignature());
												}
												targetM.addInCallSource(m.getFullJimpleSignature());
												if (needAddTM) {
													targetC.addMethod(targetM);
												}
												if (needAddTC) {
													testApp.addClass(targetC);
												}
											}
										}	catch (Exception e1) {}
									}
									m.addStmt(s);
								}
							}
						}	catch (Exception e) {}
						if (needAddM)
							c.addMethod(m);;
					}
					if (needAddClass)
						testApp.addClass(c);
				}
			}
		}));

		String[] sootArgs = { "-d", testApp.outPath + "/soot/Jimples", "-f",
				"J", "-src-prec", "apk", "-ire", "-allow-phantom-refs", "-w",
				"-force-android-jar", Paths.androidJarPath, "-process-path",
				testApp.getTestApp().getAbsolutePath() };
		soot.Main.main(sootArgs);
	}

	public static int returnCounter = 1;

	public static void InstrumentEveryMethod(File file) throws Exception {

		File instrumentLog = new File(Paths.appDataDir + file.getName()
				+ "/soot/Instrumentation/methodLevelLog.csv");
		instrumentLog.getParentFile().mkdirs();
		final PrintWriter out = new PrintWriter(new FileWriter(instrumentLog));
		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.myTransform", new BodyTransformer() {
					protected void internalTransform(final Body b,
							String phaseName, Map<String, String> options) {
						String className = b.getMethod().getDeclaringClass()
								.getName();
						if (className.startsWith("android.support.v"))
							return;
						if (b.getMethod().getName().contains("<clinit>")
								|| b.getMethod().getName().contains("<init>"))
							return;
						final Local l_outPrint = Jimple.v().newLocal(
								"outPrint", RefType.v("java.io.PrintStream"));
						b.getLocals().add(l_outPrint);
						final SootMethod out_println = Scene.v()
								.getSootClass("java.io.PrintStream")
								.getMethod("void println(java.lang.String)");
						String methodSig = b.getMethod().getSignature()
								.replace(",", " ");
						PatchingChain<Unit> units = b.getUnits();
						// first instrument the beginning
						Iterator<Unit> unitsIT = b.getUnits()
								.snapshotIterator();
						for (int i = 0; i < b.getMethod().getParameterCount() + 1; i++)
							unitsIT.next();
						Unit firstUnit = unitsIT.next();
						units.insertBefore(
								Jimple.v()
										.newAssignStmt(
												l_outPrint,
												Jimple.v()
														.newStaticFieldRef(
																Scene.v()
																		.getField(
																				"<java.lang.System: java.io.PrintStream out>")
																		.makeRef())),
								firstUnit);
						units.insertBefore(
								Jimple.v().newInvokeStmt(
										Jimple.v().newVirtualInvokeExpr(
												l_outPrint,
												out_println.makeRef(),
												StringConstant
														.v("METHOD_STARTING,"
																+ methodSig))),
								firstUnit);
						b.validate();
						// then instrument the return
						unitsIT = b.getUnits().snapshotIterator();
						returnCounter = 1;
						while (unitsIT.hasNext()) {
							final Unit u = unitsIT.next();
							u.apply(new AbstractStmtSwitch() {
								public void caseReturnStmt(ReturnStmt rS) {
									PatchingChain<Unit> units = b.getUnits();
									String mSig = b.getMethod().getSignature()
											.replace(",", " ");
									units.insertBefore(
											Jimple.v()
													.newAssignStmt(
															l_outPrint,
															Jimple.v()
																	.newStaticFieldRef(
																			Scene.v()
																					.getField(
																							"<java.lang.System: java.io.PrintStream out>")
																					.makeRef())),
											u);
									units.insertBefore(
											Jimple.v()
													.newInvokeStmt(
															Jimple.v()
																	.newVirtualInvokeExpr(
																			l_outPrint,
																			out_println
																					.makeRef(),
																			StringConstant
																					.v("METHOD_RETURNING,"
																							+ mSig
																							+ ","
																							+ returnCounter))),
											u);
									returnCounter++;
								}

								public void caseReturnVoidStmt(ReturnVoidStmt rS) {
									PatchingChain<Unit> units = b.getUnits();
									String mSig = b.getMethod().getSignature()
											.replace(",", " ");
									units.insertBefore(
											Jimple.v()
													.newAssignStmt(
															l_outPrint,
															Jimple.v()
																	.newStaticFieldRef(
																			Scene.v()
																					.getField(
																							"<java.lang.System: java.io.PrintStream out>")
																					.makeRef())),
											u);
									units.insertBefore(
											Jimple.v()
													.newInvokeStmt(
															Jimple.v()
																	.newVirtualInvokeExpr(
																			l_outPrint,
																			out_println
																					.makeRef(),
																			StringConstant
																					.v("METHOD_RETURNING,"
																							+ mSig
																							+ ","
																							+ returnCounter))),
											u);
									returnCounter++;
								}
							});
						}
						b.validate();
						out.write(className + "," + b.getMethod().getName()
								+ "\n");
					}
				}));

		String[] args = {};
		List<String> argsList = new ArrayList<String>(Arrays.asList(args));
		argsList.addAll(Arrays.asList(new String[] { "-d",
				Paths.appDataDir + file.getName() + "/soot/Instrumentation",
				"-f", "dex", "-src-prec", "apk", "-ire", "-allow-phantom-refs",
				"-w", "-force-android-jar", Paths.androidJarPath,
				"-process-path", file.getAbsolutePath() }));
		args = argsList.toArray(new String[0]);
		File outFile = new File(Paths.appDataDir + file.getName()
				+ "/soot/Instrumentation/" + file.getName());
		if (outFile.exists())
			outFile.delete();
		Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
		soot.Main.main(args);
		soot.G.reset();
		out.close();
	}

}
