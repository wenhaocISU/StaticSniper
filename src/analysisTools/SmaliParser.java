package analysisTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticField;
import staticFamily.StaticMethod;

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
			while ((line = in.readLine())!=null) {
				if (line.startsWith(".method")) {
					String bcSubSig = line.substring(line.lastIndexOf(" ") + 1);
					m = testApp.findMethodByBytecodeSignature("<" + 
							c.getName() + ": " + bcSubSig + ">");
					m.setIsConstructor(line.contains(" constructor "));
					insideMethod = true;
				}
				else if (line.startsWith(".end method")) {
					insideMethod = false;
					m = null;
				}
				else if (insideMethod) {
					// 1. line numbers
					if (line.trim().startsWith(".line ")) {
						lastLineNumber = Integer.parseInt(line.trim().split(" ")[1]);
						m.addSourceLineNumber(lastLineNumber);
					}
					// 2. labels
					else if (line.trim().startsWith(":")) {
						
					}
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