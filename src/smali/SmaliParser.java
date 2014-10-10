package smali;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticMethod;
import staticFamily.StaticSmaliStmt;

public class SmaliParser {

	private static StaticApp testApp;
	
	@SuppressWarnings("unused")
	private final String[] smaliComments = {
			"# interfaces",
			"# annotations",
			"# static fields",
			"# instance fields",
			"# direct methods",
			"# virtual methods",
	};
	
	public static void parseAll(StaticApp staticApp) {
		testApp = staticApp;
		parseLineNumbers();
		for (StaticClass c : testApp.getClassList()) {
			File classSmali = new File(testApp.outPath + "/apktool/smali/" + 
						c.getName().replace(".", "/") + ".smali");
			parseSmaliFile(classSmali, c);
		}
	}
	
	private static void parseSmaliFile(File classSmali, StaticClass c) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(classSmali));
			String line;
			// get to method district
			while ((line = in.readLine())!=null) {
				if (line.equals("# direct methods") || line.equals("# virtual methods"))
					break;
			}
			// start reading
			StaticMethod m = null;
			String methodSmali = "";
			boolean insideMethod = false;
			int lastLineNumber = -1, stmtID = 0;
			BlockLabel label = new BlockLabel();
			int lastInvokeStmtID = -1;
			while ((line = in.readLine())!=null) {
				String l = line.trim();
///////////////////////////////////////////////////////// first get into a method
				if (line.startsWith(".method")) {
					insideMethod = true;
					String bcSubSig = line.substring(line.lastIndexOf(" ") + 1);
					m = testApp.findMethodByBytecodeSignature("<" + 
							c.getName() + ": " + bcSubSig + ">");
					m.setIsConstructor(line.contains(" constructor "));
					methodSmali = line;
					label = new BlockLabel();
					label.setGeneralLabel("main");
					lastInvokeStmtID = -1;
					stmtID = 0;
				}
				else if (line.startsWith(".end method")) {
					methodSmali += "\n" + line;
					m.setSmaliCode(methodSmali);
					methodSmali = "";
					insideMethod = false;
					m = null;
				}
				else if (insideMethod && !line.equals("")) {
////////////////////////////////////////////////////////////////////////////////////////// inside method
					methodSmali += "\n" + line;
													// 1. comments
					if (l.startsWith("#")) {
						continue;
					}
													// 2. sections by dots
					else if (l.startsWith(".")) {
														// 2.1 .line *
						if (l.startsWith(".line")) {
							lastLineNumber = Integer.parseInt(l.split(" ")[1]);
							m.addSourceLineNumber(lastLineNumber);
						}
														// 2.2 .catch && .catchall
						else if (l.startsWith(".catch ")
								|| l.startsWith(".catchall ")) {
							StaticSmaliStmt s = new StaticSmaliStmt(l);
							String range = l.substring(l.indexOf("{")+1, l.indexOf("}"));
							range = range.split(" .. ")[0];
							String tgtLabel = l.substring(l.lastIndexOf(" :") + 1);
							if (l.startsWith(".catch ")) {
								String exceptionType = l.substring(
										l.indexOf(".catch ") + ".catch ".length(),
										l.indexOf("; {"));
								s.setExceptionType(exceptionType);
							}
							s.setCatchRangeLabel(range);
							s.setJumpTargetLabel(tgtLabel);
							s.setBlockLabel(label);
							s.setBranches(true);
							s.setStmtID(stmtID);
							stmtID++;
							m.addSmaliStatement(s);
						}
														// 2.3 .annotation
						else if (l.startsWith(".annotation")) {
							while (!l.equals(".end annotation")) {
								l = in.readLine();
								if (l.contains(" "))
									l = l.trim();
							}
						}
						
					}
													// 3. labels by colons
					else if (l.startsWith(":")) {
														// 3.1 special case :sswitch_data_*
						if (l.startsWith(":sswitch_data_")) {
							//  :sswitch_data_0
							//	.sparse-switch
							//	0x1389 -> :sswitch_1
							//	0x138a -> :sswitch_2
							//  .end sparse-switch
							String wholeStmt = l;
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							String ll = "";
							while (!ll.equals(".end sparse-switch")) {
								ll = in.readLine();
								if (ll.contains(" "))
									ll = ll.trim();
								wholeStmt += "\n" + ll;
								if (ll.contains(" -> :")) {
									String thisValue = ll.split(" -> :")[0];
									String tgtLabel = ll.split(" -> :")[1];
									s.addSwitchTarget(thisValue, tgtLabel);
								}
							}
							s.setSmaliStmt(wholeStmt);
							s.setBranches(true);
							s.setStmtID(stmtID);
							stmtID++;
							m.addSmaliStatement(s);
						}
														// 3.2 special case :pswitch_data_*
						else if (l.startsWith(":pswitch_data_")) {
							//  :pswitch_data_0
							//  .packed-switch 0x1
							//  :pswitch_0
							//  :pswitch_1
							//  .end packed-switch
							String wholeStmt = l;
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							String ll = "", initValue = "";
							while (!ll.equals(".end packed-switch")) {
								ll = in.readLine();
								if (ll.contains(" "))
									ll = ll.trim();
								wholeStmt += "\n\t" + ll;
								int offset = 0;
								if (ll.startsWith(".packed-switch ")) {
									initValue = ll.substring(ll.indexOf(".packed-switch ") + ".packed-switch ".length());
								} else if (ll.startsWith(":")) {
									String tgtLabel = ll.substring(1);
									s.addSwitchTarget("" + offset, tgtLabel);
									offset++;
								}
							}
							s.setpswitchInitValue(initValue);
							s.setSmaliStmt(wholeStmt);
							s.setBlockLabel(label);
							s.setBranches(true);
							s.setStmtID(stmtID);
							stmtID++;
							m.addSmaliStatement(s);
						}
														// 3.3 special case :array
						else if (l.startsWith(":array_")) {
							String wholeStmt = l;
							while (!l.contains(".end array-data")) {
								l = in.readLine();
								if (l.contains(" "))
									l = l.trim();
								wholeStmt += "\n\t" + l;
							}
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							s.setIsArrayDefStmt(true);
							s.setStmtID(stmtID);
							stmtID++;
							m.addSmaliStatement(s);
						}
														// 3.4 normal cases (just labels)
						else {
							if (l.startsWith(":try_start_")) {
								label.setTryLabel(l);
							}
							else if (l.startsWith(":try_end_")) {
								label.setTryLabel("");
							}
							else if (l.startsWith(":catch_")) {
								label.setCatchLabel(l);
							}
							else if (l.startsWith(":catchall_")) {
								label.setCatchAllLabel(l);
							}
							else if (l.startsWith(":sswitch_")) {
								label.setSswitchLabel(l);
							}
							else if (l.startsWith(":pswitch_")) {
								label.setPswitchLabel(l);
							}
							else if (l.startsWith(":cond_")){
								label.setCondLabel(l);
							}
							else if (l.startsWith(":goto_")) {
								label.setGotoLabel(l);
							}
							else {	// in case undiscovered label
								label.setGeneralLabel(l);
							}
						}
					}
													// 4. instructions
					else {
						StaticSmaliStmt s = new StaticSmaliStmt(l);
						s.setBlockLabel(label);
						s.setStmtID(stmtID);
						stmtID++;
						s.setFlowsThrough(true);
						if (StmtChecker.isGoto(l)) {
							label = new BlockLabel();
							s.setGoesTo(true);
						}
						else if (StmtChecker.isReturn(l)) {
							label = new BlockLabel();
							s.setReturns(true);
						}
						else if (StmtChecker.isInvoke(l)) {
							String target = l.substring(l.indexOf("}, ") + "}, ".length());
							String tgtClass = ClassNameDexToJava(target.split("->")[0]);
							String tgtMethodSubSig = target.split("->")[1];
							target = "<" + tgtClass + ": " + tgtMethodSubSig + ">";
							s.setIsInvoke(true);
							s.setInvokeTarget(target);
							lastInvokeStmtID = s.getStmtID();
						}
						else if (StmtChecker.isMoveResult(l)) {
							StaticSmaliStmt is = m.getSmaliStmt(lastInvokeStmtID);
							is.setInvokeResultMoved(true);
							is.setMoveInvokeResultToID(s.getStmtID());
							s.setIsMoveResultStmt(true);
							s.setMoveInvokeResultFromID(is.getStmtID());
						}
						else if (StmtChecker.isGetField(l)) {
							
						}
						else if (StmtChecker.isPutField(l)) {
							
						}
						m.addSmaliStatement(s);
					}
////////////////////////////////////////////////////////////////////////////////////////////// end inside method
				}
			}
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
	}
	
	public static StaticApp parseLineNumbers() {
		for (StaticClass c : testApp.getClassList()) {
			File smali = new File(testApp.outPath + "/apktool/smali/"
					+ c.getName().replace(".", "/") + ".smali");
			try {
				BufferedReader in = new BufferedReader(new FileReader(smali));
				String line;
				boolean insideMethod = false;
				StaticMethod m = null;
				int lastLineNumber = -1;
				while ((line = in.readLine()) != null) {
					if (line.startsWith(".method")) {
						insideMethod = true;
						String bcSubsig = line
								.substring(line.lastIndexOf(" ") + 1);
						m = testApp.findMethodByBytecodeSignature("<"
								+ c.getName() + ": " + bcSubsig + ">");
					} else if (line.startsWith(".end method")) {
						insideMethod = false;
						m = null;
						lastLineNumber = -1;
					} else {
						if (!insideMethod)
							continue;
						if (line.trim().startsWith(".line ")) {
							int i = Integer.parseInt(line.substring(line
									.indexOf(".line ") + ".line ".length()));
							m.addSourceLineNumber(i);
							lastLineNumber = i;
						} else if (line.trim().startsWith("return")) {
							m.setReturnLineNumber(lastLineNumber);
						}
					}
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return testApp;
	}

	public static String ClassNameDexToJava(String dexCN) {
		String result = "";
		if (dexCN.startsWith("L") && dexCN.endsWith(";"))
			result = dexCN.replace("/", ".").substring(1, dexCN.length()-1);
		return result;
	}
	
}