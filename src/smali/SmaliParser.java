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
			boolean insideMethod = false;
			int lastLineNumber = -1;
			BlockLabel currentBlock = new BlockLabel();
			while ((line = in.readLine())!=null) {
				String l = line.trim();
///////////////////////////////////////////////////////// first get into a method
				if (line.startsWith(".method")) {
					insideMethod = true;
					String bcSubSig = line.substring(line.lastIndexOf(" ") + 1);
					m = testApp.findMethodByBytecodeSignature("<" + 
							c.getName() + ": " + bcSubSig + ">");
					m.setIsConstructor(line.contains(" constructor "));
				}
				else if (line.startsWith(".end method")) {
					insideMethod = false;
					m = null;
				}
				else if (insideMethod && !line.equals("")) {
////////////////////////////////////////////////////////////////////////////////////////// inside method
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
					}
													// 3. labels by colons
					else if (l.startsWith(":")) {
														// 3.1 :sswitch_data_*
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
						}
														// 3.2 :pswitch_data_*
						else if (l.startsWith(":pswitch_data_")) {
							//  :pswitch_data_0
							//  .packed-switch 0x1
							//  :pswitch_0
							//  :pswitch_1
							//  .end packed-switch
							String wholeStmt = l;
							StaticSmaliStmt s = new StaticSmaliStmt(wholeStmt);
							String ll = "";
							while (!ll.equals(".end packed-switch")) {
								ll = in.readLine();
								if (ll.contains(" "))
									ll = ll.trim();
								wholeStmt += "\n" + ll;
								int offset = 0;
								String initValue = "";
								if (ll.startsWith(".packed-switch ")) {
									initValue = ll.substring(ll.indexOf(".packed-switch ") + ".packed-switch ".length());
								} else if (ll.startsWith(":")) {
									String tgtLabel = ll.substring(1);
									offset++;
								}
							}
						}
														// 3.3 :catch && :catchall
						else if (l.startsWith(":catch ") ||
								l.startsWith(":catchall ")) {
							
						}
														// 3.4 :try_end_*
						else if (l.startsWith(":try_end_")) {
							// delete :try_start_* from currentLabels
						}
														// 3.5 array
						else if (l.startsWith(":array")) {
							
						}
														// 3.6 the rest will be treat as normal labels
						else {
							
						}
					}
													// 4. instructions
					else {
						
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

}