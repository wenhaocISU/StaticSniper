package analysisTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import staticFamily.StaticApp;
import main.Paths;

public class ApkTool {
	
	public static void extractAPK(StaticApp testApp) {
		try {
			File app = testApp.getTestApp();
			System.out.println("\n-- apktool starting, target file: " + app.getAbsolutePath());
			
			String outDir = testApp.outPath + "/apktool";
			Process pc = Runtime.getRuntime().exec("java -jar " + Paths.apktoolPath +
													" d" + " -f" +
													" " + app.getAbsolutePath() +
													" " + outDir);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(pc.getInputStream()));
			BufferedReader in_err = new BufferedReader(new InputStreamReader(pc.getErrorStream()));
			String line;
			while ((line = in.readLine())!=null)	System.out.println("   " + line);
			while ((line = in_err.readLine())!=null)	System.out.println("   " + line);
			in.close();
			in_err.close();
			System.out.println("-- apktool finished extracting App " + app.getAbsolutePath());
		}	catch (Exception e ) {e.printStackTrace();}
	}
	
}
