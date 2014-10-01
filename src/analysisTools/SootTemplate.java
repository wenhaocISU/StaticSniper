package analysisTools;

import java.io.File;
import java.util.Map;

import main.Paths;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.SceneTransformer;
import soot.Transform;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;
import soot.jimple.toolkits.callgraph.CHATransformer;

public class SootTemplate {

	public static void Template(File file) {

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTransform", new SceneTransformer() {
					protected void internalTransform(String phaseName, Map<String, String> options) {
						// this method will be called only once

					}
				}));

		PackManager.v().getPack("jtp").add(new Transform("jtp.myTransform", new BodyTransformer() {
					protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
						// this method will be called on each method in the DEX
						System.out.println("--Method-- " + b.getMethod().getName());
						for (Unit u : b.getUnits()) {
							System.out.println(" -Unit- " + u.toString());
							for (UnitBox uB : u.getUnitBoxes()) {
								System.out.println("  *unit box:  ");
								System.out.println("    " + uB.getUnit().toString());
							}
							for (ValueBox vB : u.getDefBoxes()) {
								System.out.println("  *def box:  ");
								System.out.println("    " + vB.getValue().getType().toString());
							}
							for (ValueBox vB : u.getUseAndDefBoxes()) {
								System.out.println("  *use and def box:  ");
								System.out.println("    " + vB.getValue().getType().toString());
							}
							for (UnitBox uB : u.getBoxesPointingToThis()) {
								System.out.println("  *boxes pointing to this:  ");
								System.out.println("    " + uB.getUnit().toString());
							}
							for (ValueBox vB : u.getUseBoxes()) {
								System.out.println("  *use box:  ");
								System.out.println("    " + vB.getValue().toString());
							}
							System.out.print("\n");
						}
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
