package main;

import java.io.File;

import staticAnalysis.StaticInfo;
import staticFamily.StaticApp;
import staticFamily.StaticClass;
import staticFamily.StaticField;
import staticFamily.StaticMethod;
import staticFamily.StaticStmt;

public class Main {

	public static void main(String[] args){
		
		File[] apps = {
				new File("C:/Users/Wenhao/Documents/juno_workspace/AndroidTest/bin/AndroidTest.apk"),
				new File("testApps/Fast.apk"),
				new File("testApps/backupHelper.apk"),
				new File("testApps/Butane.apk"),
				new File("testApps/CalcA.apk"),
				new File("testApps/KitteyKittey.apk"),
		};
		
		StaticApp testApp = new StaticApp(apps[1]);
		testApp = StaticInfo.initAnalysis(testApp, true);
		test(testApp);

		try {

		}	catch (Exception e) {e.printStackTrace();}

	}
	
	static void test(StaticApp testApp) {
		for (StaticClass c : testApp.getClassList()) {
			if (c.getName().startsWith("android.support.v"))	continue;
			System.out.println("- " + c.getName());
			if (c.isActivity())	System.out.print("  IS ACTIVITY");
			if (c.isMainActivity())	System.out.print("  IS MainActivity");
			//System.out.print("\n");
			if (c.isInnerClass())	System.out.println("  IS INNER CLASS OF " + c.getOuterClassName());
			if (c.hasSuperClass())	System.out.println("     SUPER CLASS IS " + c.getSuperClassName());
			if (c.hasInnerClass())	{
				System.out.println("  HAS INNER CLASSES: ");
				for (String s : c.getInnerClassNames())
					System.out.println("        " + s);
			}
			for (StaticField f : c.getFieldList()) {
				System.out.println(" f " + f.getFullJimpleSignature());
			}
			for (StaticMethod m : c.getMethodList()) {
				System.out.println(" m " + m.getBytecodeSignature() + " " + m.getReturnLineNumber());
				for (String p : m.getParameterTypes()) {
					System.out.print("    " + p);
				}
				for (StaticStmt stmt : m.getStatements()) {
					System.out.println("    " + stmt.getJimpleStmt());
				}
				for (String s : m.getOutCallTargetSigs()) {
					System.out.println("    calls " + s);
					if (testApp.findMethodByFullSignature(s) !=null)
						System.out.println("found it.");
					else System.out.println("no m. " + s);
				}
				for (String s : m.getInCallSourceSigs()) {
					System.out.println("    called by " + s);
					if (testApp.findMethodByFullSignature(s) !=null)
						System.out.println("found it.");
					else System.out.println("no m. " + s);
				}
				for (String s : m.getFieldRefSigs()) {
					System.out.println("    field ref " + s);
					if (testApp.findFieldByFullSignature(s) !=null)
						System.out.println("found it.");
					else System.out.println("no f. " + s);
				}
			}
			System.out.println("\n");
		}
	}
}