package staticAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticMethod;

public class ParseSmali {
	
	public StaticApp parseLineNumbers(StaticApp testApp) {
		for (StaticClass c : testApp.getClassList()) {
			File smali = new File(testApp.outPath + "/apktool/smali/" + c.getName().replace(".", "/") + ".smali");
			try {
				BufferedReader in = new BufferedReader(new FileReader(smali));
				String line;
				boolean insideMethod = false;
				StaticMethod m = null;
				int lastLineNumber = -1;
				while ((line = in.readLine())!= null) {
					if (line.startsWith(".method")) {
						insideMethod = true;
						String bcSubsig = line.substring(line.lastIndexOf(" ")+1);
						m = testApp.findMethodByBytecodeSignature("<" + c.getName() + ": " + bcSubsig + ">");
					} else if (line.startsWith(".end method")){
						insideMethod = false;
						m = null;
						lastLineNumber = -1;
					} else {
						if (!insideMethod)	continue;
						if (line.trim().startsWith(".line ")) {
							int i = Integer.parseInt(line.substring(line.indexOf(".line ") + ".line ".length()));
							m.addSourceLineNumber(i);
							lastLineNumber = i;
						} else if (line.trim().startsWith("return")) {
							m.setReturnLineNumber(lastLineNumber);
						}
					}
				}
				in.close();
			}	catch (Exception e) {e.printStackTrace();}
		}
		
		return testApp;
	}


}