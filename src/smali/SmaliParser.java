package smali;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticMethod;
import staticFamily.StaticSmaliStmt;

public class SmaliParser {

	private static StaticApp testApp;
	private static int largestLineNumber = 0;
	private static String classSmali = "";
	
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
		for (StaticClass c : testApp.getClassList()) {
			File classSmali = new File(testApp.outPath + "/apktool/smali/" + 
						c.getName().replace(".", "/") + ".smali");
			parseLineNumbers(classSmali);
			parseSmaliFile(classSmali, c);
			File newSmali = new File(testApp.outPath + "/apktool/newSmali/" + 
						c.getName().replace(".", "/") + ".smali");
			writeInstrumentedSmali(newSmali);
		}
		File original = new File(testApp.outPath + "/apktool/smali/");
		File instrumented = new File(testApp.outPath + "/apktool/newSmali/");
		System.out.println("moving original smali files into /apktool/oldSmali/...");
		original.renameTo(new File(testApp.outPath + "/apktool/oldSmali/"));
		System.out.println("moving instrumented smali files into /apktool/smali/...");
		instrumented.renameTo(new File(testApp.outPath + "/apktool/smali/"));
	}
	
	private static void writeInstrumentedSmali(File smaliFile) {
		smaliFile.getParentFile().mkdirs();
		try {
			PrintWriter out = new PrintWriter(new FileWriter(smaliFile));
			out.write(classSmali);
			out.close();
		}	catch (Exception e) {e.printStackTrace();}
	}

	private static void parseSmaliFile(File smaliFile, StaticClass c) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(smaliFile));
			String line;
			classSmali = "";
			// get to method district
			while ((line = in.readLine())!=null) {
				classSmali += line + "\n";
				if (line.equals("# direct methods") || line.equals("# virtual methods"))
					break;
			}
			// start reading
			StaticMethod m = null;
			String methodSmali = "";
			boolean insideMethod = false;
			boolean normalLabelAlreadyUsed = false;
			int lastLineNumber = -1, stmtID = 0;
			BlockLabel label = new BlockLabel();
			int lastInvokeStmtID = -1;
			while ((line = in.readLine())!=null) {
///////////////////////////////////////////////////////// first get into a method
				if (line.startsWith(".method")) {
					classSmali += line + "\n";
					methodSmali = line + "\n";
					insideMethod = true;
					String bcSubSig = line.substring(line.lastIndexOf(" ") + 1);
					m = testApp.findMethodByBytecodeSignature("<" + 
							c.getName() + ": " + bcSubSig + ">");
					m.setIsConstructor(line.contains(" constructor "));
					label = new BlockLabel();
					ArrayList<String> newNL = new ArrayList<String>();
					newNL.add(":main");
					label.setNormalLabels(newNL);
					lastInvokeStmtID = -1;
					stmtID = 0;
				}
				else if (line.startsWith(".end method")) {
					classSmali += line + "\n\n";
					methodSmali += line + "\n";
					m.setSmaliCode(methodSmali);
					methodSmali = "";
					insideMethod = false;
					m = null;
				}
				else if (insideMethod) {
////////////////////////////////////////////////////////////////////////////////////////// inside method
					methodSmali += line + "\n";
					String l = line;
					if (l.contains(" "))
						l = l.trim();
													// 1. comments
					if (l.startsWith("#")) {
						classSmali += line + "\n";
						continue;
					}
													// 2. sections by dots
					else if (l.startsWith(".")) {
						classSmali += line + "\n";
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
							normalLabelAlreadyUsed = true;
							s.setBranches(true);
							s.setStmtID(stmtID);
							stmtID++;
							s.setSourceLineNumber(lastLineNumber);
							lastLineNumber = -1;
							m.addSmaliStatement(s);
						}
														// 2.3 .annotation
						else if (l.startsWith(".annotation")) {
							while (!l.equals(".end annotation")) {
								l = in.readLine();
								classSmali += l + "\n";
								methodSmali += l + "\n";
								if (l.contains(" "))
									l = l.trim();
							}
						}
						
					}
													// 3. labels by colons
					else if (l.startsWith(":")) {
						classSmali += line + "\n";
														// 3.1 special case :sswitch_data_*
						if (l.startsWith(":sswitch_data_")) {
							//  :sswitch_data_0
							//	.sparse-switch
							//	0x1389 -> :sswitch_1
							//	0x138a -> :sswitch_2
							//  .end sparse-switch
							String wholeStmt = line + "\n";
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							String ll = "";
							while (!ll.equals(".end sparse-switch")) {
								ll = in.readLine();
								classSmali += ll + "\n";
								methodSmali += ll + "\n";
								wholeStmt += ll + "\n";
								if (ll.contains(" "))
									ll = ll.trim();
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
							s.setSourceLineNumber(lastLineNumber);
							lastLineNumber = -1;
							m.addSmaliStatement(s);
						}
														// 3.2 special case :pswitch_data_*
						else if (l.startsWith(":pswitch_data_")) {
							//  :pswitch_data_0
							//  .packed-switch 0x1
							//  :pswitch_0
							//  :pswitch_1
							//  .end packed-switch
							String wholeStmt = line + "\n";
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							String ll = "", initValue = "";
							while (!ll.equals(".end packed-switch")) {
								ll = in.readLine();
								classSmali += ll + "\n";
								methodSmali += ll + "\n";
								wholeStmt += ll + "\n";
								if (ll.contains(" "))
									ll = ll.trim();
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
							normalLabelAlreadyUsed = true;
							s.setBranches(true);
							s.setStmtID(stmtID);
							stmtID++;
							s.setSourceLineNumber(lastLineNumber);
							lastLineNumber = -1;
							m.addSmaliStatement(s);
						}
														// 3.3 special case :array
						else if (l.startsWith(":array_")) {
							String wholeStmt = line + "\n";
							while (!l.contains(".end array-data")) {
								l = in.readLine();
								classSmali += l + "\n";
								methodSmali += l + "\n";
								if (l.contains(" "))
									l = l.trim();
								wholeStmt += "\n\t" + l;
							}
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							s.setIsArrayDefStmt(true);
							s.setStmtID(stmtID);
							stmtID++;
							s.setSourceLineNumber(lastLineNumber);
							lastLineNumber = -1;
							m.addSmaliStatement(s);
						}
														// 3.4 other cases. They are:
									// :try_start_, :try_end_, :catch_, :catchall_, :sswitch_, :pswtich_, :cond_, :goto_
						else {
							if (l.startsWith(":try_start_")) {
								label.addTryLabel(l);
							}
							else if (l.startsWith(":try_end_")) {
								ArrayList<String> newTL = label.getTryLabels();
								newTL.remove(l.replace("_end_", "_start_"));
								label.setTryLabels(newTL);
							}
							else {
								if (normalLabelAlreadyUsed) {
									ArrayList<String> newNL = new ArrayList<String>();
									newNL.add(l);
									label.setNormalLabels(newNL);
									normalLabelAlreadyUsed = false;
								} else {
									label.addNormalLabel(l);
								}
							}
						}
					}
													// 4. instructions
					else {
						classSmali += line + "\n";
						if (line.equals(""))
							continue;
						StaticSmaliStmt s = new StaticSmaliStmt(l);
						s.setBlockLabel(label);
						normalLabelAlreadyUsed = true;
						s.setStmtID(stmtID);
						stmtID++;
						s.setSourceLineNumber(lastLineNumber);
						lastLineNumber = -1;
						s.setHasRealSourceLineNumber(true);
						if (s.getSourceLineNumber() == -1) {
							s.setHasRealSourceLineNumber(false);
							s.setSourceLineNumber(largestLineNumber+1);
							largestLineNumber++;
							String left = classSmali.substring(0, classSmali.lastIndexOf("\n\n")+2);
							String right = classSmali.substring(classSmali.lastIndexOf("\n\n")+2);
							classSmali = left + "    .line " + s.getSourceLineNumber() + "\n" + right;
							m.addSourceLineNumber(s.getSourceLineNumber());
						}
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
							String target = l.substring(l.lastIndexOf(", ")+2);
							String tgtClass = ClassNameDexToJava(target.split("->")[0]);
							String tgtFieldName = target.split("->")[1].split(":")[0];
							s.setFieldTarget(tgtClass + ": " + tgtFieldName);
							s.setIsGetFieldStmt(true);
						}
						else if (StmtChecker.isPutField(l)) {
							String target = l.substring(l.lastIndexOf(", ")+2);
							String tgtClass = ClassNameDexToJava(target.split("->")[0]);
							String tgtFieldName = target.split("->")[1].split(":")[0];
							s.setFieldTarget(tgtClass + ": " + tgtFieldName);
							s.setIsPutFieldStmt(true);
						}
						m.addSmaliStatement(s);
					}
////////////////////////////////////////////////////////////////////////////////////////////// end inside method
				}
			}
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
	}
	
	private static void parseLineNumbers(File smaliFile) {
		largestLineNumber = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(smaliFile));
			String line;
			while ((line = in.readLine()) != null) {
				if (!line.startsWith("    .line"))
					continue;
				int i = Integer.parseInt(line.substring(line
						.indexOf(".line ") + ".line ".length()));
				if (i > largestLineNumber)
					largestLineNumber = i;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String ClassNameDexToJava(String dexCN) {
		String result = "";
		if (dexCN.startsWith("L") && dexCN.endsWith(";"))
			result = dexCN.replace("/", ".").substring(1, dexCN.length()-1);
		return result;
	}
	
}