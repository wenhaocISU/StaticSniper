package staticAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smali.SmaliParser;
import staticFamily.StaticApp;
import staticFamily.StaticClass;
import tools.ApkTool;
import tools.Soot;

public class StaticInfo {

	private static StaticApp staticApp;
	private static File errorLog;

	public static StaticApp initAnalysis(StaticApp testApp, boolean forceAll) {

		staticApp = testApp;
		
		errorLog = new File(staticApp.outPath + "/static.log");
		File manifestFile = new File(staticApp.outPath
				+ "/apktool/AndroidManifest.xml");
		File resFolder = new File(staticApp.outPath + "/apktool/res/");
		File staticInfoFile = new File(staticApp.outPath + "/static.info");

		if (!manifestFile.exists() || !resFolder.exists()
				|| !staticInfoFile.exists() || forceAll) {
			ApkTool.extractAPK(staticApp);
			Soot.generateAppData(staticApp);
			System.out.println("parsing AndroidManifest.xml...");
			parseManifest();
			System.out.println("parsing smali code... might take a while...");
			SmaliParser.parseAll(staticApp);
			System.out.println("parsing XMLs...");
			parseXMLs();
			System.out.println("parsing jimple code...");
			processJimpleCode();
			saveStaticInfo();
			printErrorLog();
			
		} else {
			staticApp = loadStaticInfo(staticInfoFile);
		}
		
		return staticApp;
	}

	private static void processJimpleCode() {

	}

	private static void parseXMLs() {

	}


	private static StaticApp loadStaticInfo(File staticInfoFile) {
		StaticApp result = null;
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(staticInfoFile));
			result = (StaticApp) in.readObject();
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
		return result;
	}

	private static void saveStaticInfo() {
		try {
			File outFile = new File(staticApp.outPath + "/static.info");
			if (outFile.exists())
				outFile.delete();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
			out.writeObject(staticApp);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void parseManifest() {
		try {
			File manifestFile = new File(staticApp.outPath + "/apktool/AndroidManifest.xml");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile);
			doc.getDocumentElement().normalize();
			Node manifestNode = doc.getFirstChild();
			String pkgName = manifestNode.getAttributes().getNamedItem("package").getNodeValue();
			staticApp.setPackageName(pkgName);
			NodeList aList = doc.getElementsByTagName("activity");
			boolean mainActFound = false;
			for (int i = 0, len = aList.getLength(); i < len; i++) {
				Node a = aList.item(i);
				String aName = a.getAttributes().getNamedItem("android:name").getNodeValue();
				if (aName.startsWith("."))
					aName = aName.substring(1, aName.length());
				if (!aName.contains("."))
					aName = pkgName + "." + aName;
				StaticClass c = staticApp.findClassByName(aName);
				if (c == null) {
					System.out.println("Unexpected null pointer: can't find StaticClass object for class " + aName);
					writeErrorLog("Activity In Manifest Not Found In Code," + aName);
					continue;
				}
				c.setIsActivity(true);
				Element e = (Element) a;
				NodeList actions = e.getElementsByTagName("action");
				for (int j = 0, len2 = actions.getLength(); j < len2; j++) {
					Node action = actions.item(j);
					if (action.getAttributes().getNamedItem("android:name")
							.getNodeValue()
							.equals("android.intent.action.MAIN")) {
						c.setIsMainActivity(true);
						mainActFound = true;
						break;
					}
				}
			}
			if (!mainActFound) {
				NodeList aaList = doc.getElementsByTagName("activity-alias");
				for (int i = 0, len = aaList.getLength(); i < len; i++) {
					if (mainActFound)
						break;
					Node aa = aaList.item(i);
					String aName = aa.getAttributes().getNamedItem("android:targetActivity").getNodeValue();
					if (aName.startsWith("."))
						aName = aName.substring(1, aName.length());
					if (!aName.contains("."))
						aName = pkgName + "." + aName;
					Element e = (Element) aa;
					NodeList actions = e.getElementsByTagName("action");
					for (int j = 0, len2 = actions.getLength(); j < len2; j++) {
						Node action = actions.item(j);
						if (action.getAttributes().getNamedItem("android:name").getNodeValue().equals("android.intent.action.MAIN")) {
							StaticClass c = staticApp.findClassByName(aName);
							if (c == null) {
								System.out.println("Unexpected null pointer: can't find StaticClass object for class "+ aName);
								writeErrorLog("Activity In Manifest Not Found In Code," + aName);
								continue;
							}
							c.setIsMainActivity(true);
							mainActFound = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeErrorLog(String s){
		try {
			PrintWriter out = new PrintWriter(new FileWriter(errorLog, true));
			out.write(s + "\n");
			out.close();
		} 	catch (Exception e) {e.printStackTrace();}
	}

	private static void printErrorLog() {
		try {
			if (!errorLog.exists())
				return;
			BufferedReader in = new BufferedReader(new FileReader(errorLog));
			String line;
			while ((line = in.readLine())!=null)
				System.out.print(line);
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
	}

}
