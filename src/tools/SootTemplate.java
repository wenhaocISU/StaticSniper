package tools;

import java.io.File;
import java.util.Map;

import main.Paths;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.SceneTransformer;
import soot.Transform;

public class SootTemplate {

	public static void Template(File file) {

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", new SceneTransformer() {
					protected void internalTransform(String phaseName, Map<String, String> options) {
						// this method will be called only once
						System.out.println("WJTP");
					}
				}));

		PackManager.v().getPack("jtp").add(new Transform("jtp.myTransform", new BodyTransformer() {
					protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
						// this method will be called on each method in the DEX
						System.out.println("JTP " + b.getMethod().getSignature());
					}
				}));

		String[] args = {
				"-d", Paths.appDataDir + file.getName() + "/soot/Jimples",
				"-f", "J",
				"-src-prec", "apk",
				"-ire", "-allow-phantom-refs", "-w",
				"-force-android-jar", Paths.androidJarPath,
				"-process-path", file.getAbsolutePath() };
		soot.Main.main(args);
	}

}
