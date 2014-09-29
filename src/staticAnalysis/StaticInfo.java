package staticAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import staticFamily.StaticApp;
import staticFamily.StaticClass;
import analysisTools.ApkTool;
import analysisTools.Soot;

public class StaticInfo {

	private static StaticApp app;
	
	public static StaticApp initAnalysis(StaticApp testApp, boolean forceAll) {
		
		app = testApp;
		File manifestFile = new File(app.outPath + "/apktool/AndroidManifest.xml");
		File resFolder = new File(app.outPath + "/apktool/res/");
		File staticInfoFile = new File(app.outPath + "/static.info");
		
		if (!manifestFile.exists() || !resFolder.exists() ||
			!staticInfoFile.exists() || forceAll) {
			ApkTool.extractAPK(app);
			Soot.generateAppData(app);
			parseSmali();
			parseManifest();
			parseXMLs();
			processJimpleCode();
			
			saveStaticInfo();
			return app;
		}
		else {
			return loadStaticInfo(staticInfoFile);
		}
	}

	private static void processJimpleCode() {
		
	}
	
	private static void parseXMLs() {
		
	}
	
	private static void parseSmali() {
		new ParseSmali().parseLineNumbers(app);
	}
	
	private static StaticApp loadStaticInfo(File staticInfoFile) {
		StaticApp result = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(staticInfoFile));
			result = (StaticApp) in.readObject();
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
		return result;
	}
	
	private static void saveStaticInfo() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(app.outPath + "/static.info"));
			out.writeObject(app);
			out.close();
		}	catch (Exception e) {e.printStackTrace();}
	}
	
	private static void parseManifest() {
		try {
			File manifestFile = new File(app.outPath + "/apktool/AndroidManifest.xml");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile);
			doc.getDocumentElement().normalize();
			Node manifestNode = doc.getFirstChild();
			String pkgName = manifestNode.getAttributes().getNamedItem("package").getNodeValue();
			app.setPackageName(pkgName);
			NodeList aList = doc.getElementsByTagName("activity");
			boolean mainActFound = false;
			for (int i = 0, len = aList.getLength(); i < len; i++) {
				Node a = aList.item(i);
				String aName = a.getAttributes().getNamedItem("android:name").getNodeValue();
				if (aName.startsWith("."))	aName = aName.substring(1, aName.length());
				if (!aName.contains("."))	aName = pkgName + "." + aName;
				StaticClass c = app.findClassByName(aName);
				if (c == null) {
					System.out.println("Unexpected null pointer: can't find StaticClass object for class " + aName);
					continue;
				}
				c.setIsActivity(true);
				Element e = (Element) a;
				NodeList actions = e.getElementsByTagName("action");
				for (int j = 0, len2 = actions.getLength(); j < len2; j++) {
					Node action = actions.item(j);
					if (action.getAttributes().getNamedItem("android:name").getNodeValue().equals("android.intent.action.MAIN")) {
						c.setIsMainActivity(true);
						mainActFound = true;
						break;
					}
				}
			}
			if (!mainActFound) {
				NodeList aaList = doc.getElementsByTagName("activity-alias");
				for (int i = 0, len = aaList.getLength(); i < len; i++) {
					if (mainActFound)	break;
					Node aa = aaList.item(i);
					String aName = aa.getAttributes().getNamedItem("android:targetActivity").getNodeValue();
					if (aName.startsWith("."))	aName = aName.substring(1, aName.length());
					if (!aName.contains("."))	aName = pkgName + "." + aName;
					Element e = (Element) aa;
					NodeList actions = e.getElementsByTagName("action");
					for (int j = 0, len2 = actions.getLength(); j < len2; j++) {
						Node action = actions.item(j);
						if (action.getAttributes().getNamedItem("android:name").getNodeValue().equals("android.intent.action.MAIN")) {
							StaticClass c = app.findClassByName(aName);
							if (c == null){
								System.out.println("Unexpected null pointer: can't find StaticClass object for class " + aName);
								continue;
							}
							c.setIsMainActivity(true);
							mainActFound = true;
							break;
						}
					}
				}
			}
		}	catch (Exception e) {e.printStackTrace();}
	}


	/*
	public static ArrayList<StaticLayout> getLayoutList(File file) {
		return layoutList;
	}
	
	
	public static void initAnalysis(File file, boolean forceAllSteps) {
		File info = new File(Paths.appDataDir + file.getName() + "/apktool/apktool.yml");
		//if (!info.exists() || forceAllSteps)
			//analysisTools.ApkTool.extractAPK(file);
		File apkInfo = new File(Paths.appDataDir + file.getName() + "/ApkInfo.csv");
		if (!apkInfo.exists() || forceAllSteps)
			analysisTools.Soot.generateAPKData(file);
		System.out.println("processing call graph...");
		File cgNodesFile = new File(Paths.appDataDir + file.getName() + "/CallGraph.nodes");
		if (!cgNodesFile.exists() || forceAllSteps)
			prepareCG(file);
		if (callGraphNodes.size()<1)
			readCGObject(file);
		System.out.println("finished reading call graph\nparsing XML layouts");
		parseXMLLayouts(file);
		System.out.println("finished parsing XML layouts\nparsing java layouts");
		parseJavaLayouts(file);
		System.out.println("finished parsing java layouts\nprocessing intents and setcontentview");
		process_Intents_And_setContentView(file);
		System.out.println("finished processing intents and setcontentviews");
		System.out.println("initAnalysis finished.");
	}
	

	private static void readCGObject(File file) {
		try {
			System.out.println("reading Call Graph from '/CallGraph.nodes'...");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(Paths.appDataDir + file.getName() + "/CallGraph.nodes"));
			callGraphNodes = (ArrayList<StaticNode>) in.readObject();
			in.close();
		}	catch (Exception e ) {e.printStackTrace();}
	}
	
	public static void prepareCG(File file) {
		File cgFile = new File(Paths.appDataDir + file.getName() + "/CallGraph.csv");
		try {
			System.out.println("reading '/CallGraph.csv'...");
			BufferedReader in = new BufferedReader(new FileReader(cgFile));
			String line;
			while ((line = in.readLine())!=null) {
				String srcClass = line.split(",")[1];
				String srcMethodSig = line.split(",")[2];
				String tgtClass = line.split(",")[3];
				String tgtSig = line.split(",")[4];
				String lineNo = line.split(",")[5];
				StaticNode srcNode = findStaticNode(srcClass, srcMethodSig);
				StaticNode tgtNode = findStaticNode(tgtClass, tgtSig);
				if (srcNode == null)	{ srcNode = new StaticNode(srcClass, srcMethodSig, "Method");	callGraphNodes.add(srcNode); }
				if (tgtNode == null)	{ tgtNode = new StaticNode(tgtClass, tgtSig, line.split(",")[0]); callGraphNodes.add(tgtNode); }
				srcNode.addOutCall(tgtNode, lineNo);
				tgtNode.addInCall(srcNode, lineNo);
			}
			in.close();
			System.out.println("writing /CallGraph.nodes...");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Paths.appDataDir + file.getName() + "/CallGraph.nodes"));
			out.writeObject(callGraphNodes);
			out.close();
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getSuperClassName(File file, String className) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(Paths.appDataDir + file.getName() + "/ApkInfo.csv"));
			String line;
			in.readLine();
			while ((line = in.readLine())!=null) {
				if (line.split(",")[1].equals(className))
					return line.split(",")[2];
			}
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
		return "no super class";
	}
	
	public static String isMethodDeadOrAlive(File file, String className, String methodSubSig) {
		try {
			String superClass = StaticInfo.getSuperClassName(file, className);
			ArrayList<String> inCallers = StaticInfo.getAllPossibleIncomingCallers(className, methodSubSig);
			ArrayList<String> eventHandlers = StaticInfo.findEventHandlersThatMightDirectlyCallThisMethod(file, className, methodSubSig);
			if (inCallers.size() < 1) { // is source
				if (eventHandlers.size() > 0) {
					for (String eH : eventHandlers) {
						String layoutName = eH.split(",")[1];
						if (isLayoutAlive(layoutName))
							return "ALIVE";		// it's an event handler method
					}
				}
				else {	// it's source, but not event handler
					if (superClass.equals("java.lang.Object")) return "DEAD";
				};
				return "NOT SURE";
			} else { // not source
				boolean allDead = true;
				for (String s : inCallers) {
					String inCallerClass = s.split(":")[0];
					String inCallerMethod = s.split(":")[1];
					String inCallerStatus = isMethodDeadOrAlive(file, inCallerClass, inCallerMethod);
					if (inCallerStatus.equals("DEAD"))
						allDead = allDead && true;
					else {
						allDead = false;
						break;
					}
				}
				if (allDead)	return "DEAD";
				else return "NOT SURE";
			}
		}	catch (Exception e) {e.printStackTrace();}
		return "NOT SURE";
	}
	
	public static boolean isLayoutAlive(String layoutName) {
		for (String uic : UIChanges)
			if (uic.startsWith("setContentView,1,")) {
				if (layoutName.equals(uic.split(",")[7]))
					return true;
			}
		return false;
	}
	
	public static StaticNode findStaticNode(String className, String signature) {
		for (StaticNode cgNode : callGraphNodes)
			if (cgNode.getDeclaringClassName().equals(className) && cgNode.getSignature().equals(signature))
				return cgNode;
		return null;
	}
	
	public static ArrayList<String> getClassNames(File file) {
		// return all class names within an app
		ArrayList<String> results = new ArrayList<String>();
		try {
			File apkInfoFile = new File(Paths.appDataDir + file.getName() + "/ApkInfo.csv");
			if (!apkInfoFile.exists())	{
				System.out.println("can't find ApkInfo file! Did you call 'analysisTools.Soot.generateAPKData(file)' before this?");
				return results;
			}
			BufferedReader in = new BufferedReader(new FileReader(apkInfoFile));
			in.readLine();
			String line;
			while ((line = in.readLine())!=null)
				results.add(line.split(",")[1]);
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
		return results;
	}
	
	private static void parseXMLLayouts(File file) {
		// create a Layout Object for each layout xml, also spot out the custom layouts
		// create ViewNodes from components that has 'andriod:id' in each layout
		File layoutFolder = new File(Paths.appDataDir + file.getName() + "/apktool/res/layout/");
		if (!layoutFolder.exists()) {
			System.out.println("can't find /apktool/res/layout folder! Run apktool first.");
			return;
		}
		File[] layoutFiles = layoutFolder.listFiles();
		for (File layoutFile: layoutFiles){
			if (!layoutFile.getName().endsWith(".xml"))	continue;
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(layoutFile);
				Node layoutNode = doc.getFirstChild();
				String layoutName = layoutFile.getName().substring(0, layoutFile.getName().length()-4);
				String layoutType = layoutNode.getNodeName();
				ArrayList<String> classNames = getClassNames(file);
				boolean isCustom = false;
				for (String c: classNames)
					if (c.equals(layoutType))
						isCustom = true;
				StaticLayout thisLayout = new StaticLayout(layoutName, layoutNode, layoutType, isCustom);
				layoutList.add(thisLayout);
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("*");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (hasID(node)) {
						String ID = node.getAttributes().getNamedItem("android:id").getNodeValue();
						String type = node.getNodeName();
						isCustom = false;
						for (String c: classNames)
							if (c.equals(layoutType))
								isCustom = true;
						StaticViewNode thisNode = new StaticViewNode(type, ID, node, isCustom);
						thisLayout.addNode(thisNode);
					} else {
						// TODO need to add solution to nodes that has event handlers but no id
					}
				}
			}	catch (Exception e) {e.printStackTrace();}
		}
	}
	
	private static boolean hasID(Node node) {
		// if this view has ID, then it's worth keeping
		boolean result = false;
		if (!node.hasAttributes())	return false;
		NamedNodeMap attrs = node.getAttributes();
		for (int i = 0, len = attrs.getLength(); i < len; i++)
			if (attrs.item(i).getNodeName().equals("android:id")) {
				result = true;
				break;
			}
		return result;
	}
	
	private static void collectLayoutTypes(File file) {
		File known = new File(Paths.appDataDir + "KnownLayoutTypes.txt");
		File custom = new File(Paths.appDataDir + "CustomLayoutParents.txt");
		ArrayList<String> standardLayoutTypes = new ArrayList<String>();
		ArrayList<String> customLayoutParents = new ArrayList<String>();
		if (known.exists())
			standardLayoutTypes = new ArrayList<String>(Arrays.asList(readDatFile(new File(Paths.appDataDir + "KnownLayoutTypes.txt")).split("\n")));
		if (custom.exists())
			customLayoutParents = new ArrayList<String>(Arrays.asList(readDatFile(new File(Paths.appDataDir + "CustomLayoutParents.txt")).split("\n")));
		for (StaticLayout l : layoutList) {
			if (!l.isCustomLayout()) {
				if (!standardLayoutTypes.contains(l.getType()))	
					standardLayoutTypes.add(l.getType());	
				continue;
			}
			if (l.getType().startsWith("android.support.v"))	continue;
			File classFile = new File(Paths.appDataDir + file.getName() + "/soot/Jimples/" + l.getType() + ".jimple");
			if (!classFile.exists())	continue;
			try {
				BufferedReader in = new BufferedReader(new FileReader(classFile));
				String firstline = in.readLine();

				if (firstline.contains("extends ")) {
					String p = firstline.substring(firstline.indexOf("extends ") + "extends ".length());
					if (p.contains("implements"))
						p = p.substring(0, p.indexOf("implements"));
					if (!p.contains(",") && !customLayoutParents.contains(p.trim()))	customLayoutParents.add(p.trim());
					else for (String s: p.trim().split(","))  if (!customLayoutParents.contains(s.trim()))	customLayoutParents.add(s.trim());
				};
				if (firstline.contains("implements ")) {
					String p = firstline.substring(firstline.indexOf("implements ") + "implements ".length());
					if (!p.contains(","))	if (!customLayoutParents.contains(p.trim())) customLayoutParents.add(p.trim());
					else for (String s: p.trim().split(","))	if (!customLayoutParents.contains(s.trim())) customLayoutParents.add(s.trim());
				};
				
				in.close();
			}	catch (Exception e) {e.printStackTrace();}
		}
		try {
			PrintWriter out = new PrintWriter(new FileWriter(Paths.appDataDir + "KnownLayoutTypes.txt"));
			for (String s: standardLayoutTypes)
				out.write(s + "\n");
			out.close();
			out = new PrintWriter(new FileWriter(Paths.appDataDir + "CustomLayoutParents.txt"));
			for (String s: customLayoutParents)
				out.write(s + "\n");
			out.close();
		}	catch (Exception e) {e.printStackTrace();}
	}
	
	private static void parseJavaLayouts(File file) {
		// TODO parse java layouts and add views
		// first read the code of those custom layouts, then scan the others
		//collectLayoutTypes(file);
		for (StaticLayout l : layoutList) {
			if (!l.isCustomLayout())	continue;
			File classFile = new File(Paths.appDataDir + file.getName() + "/soot/Jimples/" + l.getType() + ".jimple");
			if (!classFile.exists())	continue;
			View v = null;
			
		}
	}
	
	public static String findViewNameByID(File file, String ID) {
		// takes a hex ID string as input, look for the view Name in /res/values/public.xml
		// this is to deal with 'findViewById()' in the jimple code
		String result = "";
		File manifestFile = new File(Paths.appDataDir + file.getName() + "/apktool/res/values/public.xml");
		if (!manifestFile.exists()) {
			System.out.println("can't find /apktool/res/values/public.xml! Run apktool first.");
			return result;
		}
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile);
			doc.getDocumentElement().normalize();
			NodeList nl = doc.getElementsByTagName("*");
			for (int i = 0, len = nl.getLength(); i < len; i++) {
				Node n = nl.item(i);
				if (!n.hasAttributes())	continue;
				Node idNode = n.getAttributes().getNamedItem("id");
				Node nameNode = n.getAttributes().getNamedItem("name");
				if (idNode==null)	continue;
				if (ID.equals(idNode.getNodeValue())) {
					result = nameNode.getNodeValue();
					break;
				}
			}
		}	catch (Exception e) {e.printStackTrace();}
		return result;
	}
	
	public static StaticLayout getLayoutObject(String name) {
		StaticLayout result = null;
		for (StaticLayout l: layoutList)
			if (l.getName().equals(name))
				result = l;
		return result;
	}
	
	private static void process_Intents_And_setContentView(File file) {
		File[] classFolders = new File(Paths.appDataDir + file.getName() + "/ClassesInfo/").listFiles();
		try {
			// first collect all startActivity and setContentView in code, and determine their characteristics
			for (File classFolder: classFolders) {

				File[] methodJimples = classFolder.listFiles();
				String className = classFolder.getName();
				for (File methodJimple: methodJimples) {
					if (!methodJimple.getName().endsWith(".jimple"))	continue;
					String methodFileName = methodJimple.getName().substring(0, methodJimple.getName().length()-7);
					BufferedReader in_mJ = new BufferedReader(new FileReader(methodJimple));
					int lineNumber = 1;
					String line;
					while ((line = in_mJ.readLine())!=null) {
						if (EventHandlers.isStartActivity(line) > -1 && line.contains("virtualinvoke"))
							UIChanges.add(solveIntent(file, className, methodFileName, lineNumber));
						else if (EventHandlers.isSetContentView(line) > -1 && line.contains("virtualinvoke"))
							UIChanges.add(solveSetContentView(file, className, methodFileName, lineNumber));
						lineNumber++;
					}
					in_mJ.close();
				}
			}
			// then find default layout for each activity
			// "StartActivity",foundTargetActvt?,inOnCreate?,inEventHandler?,classname,methodsig,linenumber,targetActvt,NumberOfOnCreate,NumberOfViews,actvt1,...(layout&&widgetID),...
			// "setContentView",foundTargetLayout?,inOnCreate?,inEventHandler?,classname,methodsig,linenumber,targetLayout,NumberOfOnCreate,NumberOfViews,actvt1,...(layout&&widgetID),...
			ArrayList<String> actvts = getActivityNames(file);
			for (String actvt : actvts) {
				int count = 0;
				for (String U: UIChanges) {
					String[] u = U.split(",");
					if (!u[0].equals("setContentView"))	continue;
					// get the direct setContentView calls first
					if (u[2].equals("1") && u[4].equals(actvt) && u[5].equals("void onCreate(android.os.Bundle)")) {
						count++;
						if (!defLayouts.containsKey(actvt))		defLayouts.put(actvt, u[7]);
						else 	defLayouts.put(actvt, defLayouts.get(actvt) + "," + u[7]);
					}
					// maybe this setContenView is not in an onCreate, but called by one
					if (!u[8].equals("0")) {
						int calledCount = Integer.parseInt(u[8]);
						for (int i = 0; i < calledCount; i++)
							if (u[10+i].equals(actvt)) {
								count++;
								if (!defLayouts.containsKey(actvt))		defLayouts.put(actvt, u[7]);
								else 	defLayouts.put(actvt, defLayouts.get(actvt) + "," + u[7]);
							}
					}
				}
				if (count!=1) {
					File outfile = new File(Paths.appDataDir + file.getName() + "/UtilLogs/defaultLayout.log");
					outfile.getParentFile().mkdirs();
					PrintWriter out = new PrintWriter(new FileWriter(outfile, true));
					out.write("Found " + count + " default layouts for," + actvt + "\n");
					out.close();
				}
			}
			// then assign leaving and staying widgets
			// "StartActivity",foundTargetActvt?,inOnCreate?,inEventHandler?,classname,methodsig,linenumber,targetActvt,NumberOfOnCreate,NumberOfViews,actvt1,...(avtvt&&layout&&widgetID&&EH),...
			// "setContentView",foundTargetLayout?,inOnCreate?,inEventHandler?,classname,methodsig,linenumber,targetLayout,NumberOfOnCreate,NumberOfViews,actvt1,...(avtvt&&layout&&widgetID&&EH),...
			for (String UIChange: UIChanges) {
				String[] u = UIChange.split(",");
				if (u[3].equals("0") && u[9].equals("0"))	continue;
				int onCreateCount = Integer.parseInt(u[8]);
				int viewCount = Integer.parseInt(u[9]);
				for (int i = 0; i < viewCount; i++) {
					String[] thisViewInfo = u[10+onCreateCount+i].split("&&");
					String actvtNm = thisViewInfo[0];
					String layoutNm = thisViewInfo[1];
					String vID = thisViewInfo[2];
					String vEH = thisViewInfo[3];
					StaticViewNode theVN = StaticInfo.getLayoutObject(layoutNm).getViewNodeById(vID);
					theVN.addLeavingEventHandler(actvtNm + "," + vEH + "," + u[0] + "," + u[7]);
				}
			}
		}	catch (Exception e) {e.printStackTrace();}
	}
	
	public static StaticLayout getDefaultLayout(File file, String activityName) {
		StaticLayout result = null;
		if (defLayouts.containsKey(activityName))
			result = getLayoutObject(defLayouts.get(activityName));
		return result;
	}
	
	private static String findIntentTarget(File file, String className, String methodFileName, int lineNumber) throws Exception {
		String targetActivityName = " ";
		boolean targetActivityFound = false;
		File mJFile = new File(Paths.appDataDir + file.getName() + "/ClassesInfo/" + className + "/" + methodFileName + ".jimple");
		BufferedReader in_mJ = new BufferedReader(new FileReader(mJFile));
		for (int i = 0; i < lineNumber - 1; i++)
			in_mJ.readLine();
		String theStmt = in_mJ.readLine();
		in_mJ.close();
		// TODO add more solution to other startActivity() signatures
		// now it's just 'void startActivity(android.content.Intent)'
		if (EventHandlers.isStartActivity(theStmt) == 0) {
			String intentLocalName = theStmt.substring(theStmt.indexOf(EventHandlers.intentSigs[0])+EventHandlers.intentSigs[0].length(), theStmt.length());
			intentLocalName = intentLocalName.substring(intentLocalName.indexOf("(")+1, intentLocalName.lastIndexOf(")"));
			if (intentLocalName.equals("null"))
				return "null";
			//get all stmts that contains that Intent's variable name
			in_mJ = new BufferedReader(new FileReader(mJFile));
			String line;
			ArrayList<String> intentStmts = new ArrayList<String>();
			for (int i = 0; i < lineNumber - 1; i++) {
				line = in_mJ.readLine();
				if (line.contains(intentLocalName))
					intentStmts.add(line);
			}
			in_mJ.close();
			// TODO here I assume everyone will use 'void <init>()' to give target class name
			// I will put the exceptions in ~/UtilLogs/intentSolving.log
			String intentInitSig = "specialinvoke " + intentLocalName + ".<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>";
			for (int i = intentStmts.size()-1; i >=0; i--) {
				String thisStmt = intentStmts.get(i);
				if (intentStmts.get(i).contains(intentInitSig)) {
					targetActivityName = thisStmt.substring(thisStmt.indexOf(intentInitSig) + intentInitSig.length(), thisStmt.length());
					targetActivityName = targetActivityName.substring(targetActivityName.indexOf("(")+1, targetActivityName.lastIndexOf(")"));
					targetActivityName = targetActivityName.split(",")[1].trim(); // get the 2nd paramter variable
					if (targetActivityName.startsWith("class \"")) {
						targetActivityName = targetActivityName.substring(targetActivityName.indexOf("\"")+1, targetActivityName.lastIndexOf("\""));
						if (targetActivityName.indexOf("/") > -1)
							targetActivityName = targetActivityName.replace("/", ".");
						targetActivityFound = true;
					}
				}
			}
			if (!targetActivityFound) {
				File outIntentLog = new File(Paths.appDataDir + file.getName() + "/UtilLogs/intentSolving.log");
				outIntentLog.getParentFile().mkdirs();
				PrintWriter out = new PrintWriter(new FileWriter(outIntentLog, true));
				out.write("Cannot Solve Target Activity," + file + "," + className + "," + methodFileName + "," + lineNumber + "\n");
				out.close();
			}
		}
		return targetActivityName;
	}
	
	private static String findSetContentViewTarget(File file, String className, String methodFileName, int lineNumber) throws Exception {
		String targetLayout = " ";
		boolean foundTargetLayout = false;
		File mJFile = new File(Paths.appDataDir + file.getName() + "/ClassesInfo/" + className + "/" + methodFileName + ".jimple");
		BufferedReader in_mJ = new BufferedReader(new FileReader(mJFile));
		for (int i = 0; i < lineNumber - 1; i++)
			in_mJ.readLine();
		String theStmt = in_mJ.readLine();
		// TODO add more solution to other setContentView() signatures
		// now it's just 'void setContentView(int)'
		if (EventHandlers.isSetContentView(theStmt) == 0) {
			//TODO now I assume everyone will use constant
			//if the parameter is not int constant, write to /UtilLogs/setContentViewSolving.log
			String viewID = theStmt.substring(theStmt.indexOf(EventHandlers.setContentViewSigs[0]) + EventHandlers.setContentViewSigs[0].length() , theStmt.length());
			viewID = viewID.substring(viewID.indexOf("(") + 1 , viewID.lastIndexOf(")"));
			if (!viewID.startsWith("$")) {
				viewID = "0x" + Integer.toHexString(Integer.parseInt(viewID));
				while (viewID.length()<10)
					viewID += "0";
				targetLayout = findViewNameByID(file, viewID);
				if (!targetLayout.equals(""))
					foundTargetLayout = true;
			}
			if (!foundTargetLayout) {
				File logFile = new File(Paths.appDataDir + file.getName() + "/UtilLogs/setContentViewSolving.log");
				logFile.getParentFile().mkdirs();
				PrintWriter out = new PrintWriter(new FileWriter(logFile, true));
				out.write("Can not solve setContentView target," + className + "," + methodFileName + "," + lineNumber + "\n");
				out.close();
			}
		}
		in_mJ.close();
		return targetLayout;
	}
	
	public static ArrayList<String> getOutCallTargets(String className, String methodSubsig) {
		for (StaticNode cN : callGraphNodes) {
			if (cN.getDeclaringClassName().equals(className) && cN.getSignature().equals(methodSubsig)) {
				return cN.getOutCallTargets();
			}
		}
		return new ArrayList<String>();
	}
	
	public static ArrayList<String> getInCallSources(String className, String methodSubsig) {
		for (StaticNode cN : callGraphNodes)
			if (cN.getDeclaringClassName().equals(className) && cN.getSignature().equals(methodSubsig))
				return cN.getInCallSources();
		return new ArrayList<String>();
	}
	
	public static ArrayList<String> getPossibleCallSequences(String className, String methodSubsig) {
		
		Map<String, Boolean> callMap = new HashMap<String, Boolean>();
		callMap.put(className + ":" + methodSubsig, false);
		boolean finished = false;
		while (!finished) {
			ArrayList<String> addAfterIteration = new ArrayList<String>();
			Iterator<Map.Entry<String, Boolean>> it = callMap.entrySet().iterator();
			while (it.hasNext()) {
				// for each existing path, fetch the source node, get its callers
				// if no caller then its a source
				// append those callers to the list
				// not all callers: if a caller is already in this path, ignore him
				Map.Entry<String, Boolean> entry = it.next();
				if (entry.getValue()) continue;
				String currentSource = entry.getKey();
				if (entry.getKey().contains(","))	// first find the source in this path
					currentSource = entry.getKey().split(",")[entry.getKey().split(",").length-1];
				StaticNode srcNode = StaticInfo.findStaticNode(currentSource.split(":")[0], currentSource.split(":")[1]);
				if (srcNode == null || !srcNode.hasInCalls()) {	entry.setValue(true); continue;	} // if no new callers, the path reached source
				ArrayList<String> newSources = srcNode.getInCallSources();
				int newSrcCount = 0;
				for (String newSrc : newSources) {
					if (callMap.containsKey(newSrc))	continue; // if new caller already in this path, ignore this caller
					newSrcCount++;
					addAfterIteration.add(entry.getKey() + "," + newSrc);
				}
				if (newSrcCount>0)	 it.remove();	// if path grows, delete the old one
			}
			for (String newEntry : addAfterIteration)	// put extended paths in
				callMap.put(newEntry, false);
			// after one round of extending path, see if another round is needed
			finished = true;
			for (Map.Entry<String, Boolean> e : callMap.entrySet())
				if (!e.getValue()) { finished = false; break; }
		}
		ArrayList<String> results = new ArrayList<String>();
		for (Map.Entry<String, Boolean> e : callMap.entrySet())
			results.add(e.getKey());
		return results;
	}
	
	public static boolean isSourceMethod(String className, String methodSubSig) {
		StaticNode node = StaticInfo.findStaticNode(className, methodSubSig);
		if (node == null)
			return true;
		if (node.getInCallSources().size() < 1)
			return true;
		return false;
	}
	
	public static ArrayList<String> getAllPossibleIncomingCallers(String className, String methodSubSig) {
		Map<String, Boolean> callMap = new HashMap<String, Boolean>();
		ArrayList<String> result = new ArrayList<String>();
		StaticNode node = StaticInfo.findStaticNode(className, methodSubSig);
		if (node == null)
			return result;
		// get direct callers
		ArrayList<String> inCalls = node.getInCallSources();
		for (String iC : inCalls) {
			String callerClass = iC.split(":")[0];
			String callerSig = iC.split(":")[1];
			String lineNo = iC.split(":")[2];
			String callSig = callerClass + ":" + callerSig + ":" + lineNo;
			if (!callMap.containsKey(callSig))
				callMap.put(callSig, false);
		}
		// get callers of callers
		ArrayList<String> toAdd = new ArrayList<String>();
		Set<Entry<String, Boolean>> entrySets = callMap.entrySet();
		for (Map.Entry<String, Boolean> entry: entrySets) {
			if (entry.getValue())	continue;
			String nextClassName = entry.getKey().split(":")[0];
			String nextMethodSubSig = entry.getKey().split(":")[1];
			ArrayList<String> nextLevelCaller = getAllPossibleIncomingCallers(nextClassName, nextMethodSubSig);
			for (String nLC : nextLevelCaller)
				if (!callMap.containsKey(nLC) && !toAdd.contains(nLC))
					toAdd.add(nLC);
			entry.setValue(true);
		}
		for (String newKey : toAdd)
			callMap.put(newKey, false);
		for (Map.Entry<String, Boolean> entry: callMap.entrySet())
			result.add(entry.getKey());
		return result;
	}
	
	public static void methodAnalysis(File file, String targetClass, String targetMethod) {

		ArrayList<String> directEHs = StaticInfo.findEventHandlersThatMightDirectlyCallThisMethod(file, targetClass, targetMethod);
		boolean isOnCreate = StaticInfo.isOnCreate(file, targetClass, targetMethod);
		System.out.println("==========\nThe CallGraph Analysis result for \'" + targetClass + ":" + targetMethod + "\' is: ");
		if (directEHs.size()>0) {
			System.out.println("  - this is the event handler method for: ");
			for (String e: directEHs)
				System.out.print("      " + e);
			System.out.print("\n");
		}
		else System.out.println("  - no widgets found connecting to this method.");
		if (isOnCreate)
			System.out.println("  - this is the onCreate method for activity: " + targetClass);
		ArrayList<String> callers = StaticInfo.getAllPossibleIncomingCallers(targetClass, targetMethod);
		// format is: caller class name , caller method signature, at which line did the call happen
		if (callers.size()<1) {
			System.out.println("  - no source found from CallGraph.");
			return;
		}
		System.out.println("here are the CallGraph sources for target method: " + targetClass + " : " + targetMethod + "");
		int counter = 1;
		for (String caller: callers) {
			String callerClass = caller.split(",")[0];
			String callerMethod = caller.split(",")[1];
			directEHs = StaticInfo.findEventHandlersThatMightDirectlyCallThisMethod(file, callerClass, callerMethod);
			isOnCreate = StaticInfo.isOnCreate(file, callerClass, callerMethod);
			System.out.println(counter + ". " + caller);
			counter++;
			if (directEHs.size()>0) {
				System.out.println("  - this is the event handler method for: ");
				for (String e: directEHs)
					System.out.print("      " + e);
			}
			if (isOnCreate)
				System.out.println("  - this is the onCreate method for activity: " + callerClass);
		}
	}
	
	public static boolean isOnCreate(File file, String className, String methodSubSig) {
		return (methodSubSig.equals("void onCreate(android.os.Bundle)") && isActivity(file, className));
	}
	
	public static ArrayList<String> findEventHandlersThatMightDirectlyCallThisMethod(File file, String className, String methodSubSig) {
		ArrayList<String> result = new ArrayList<String>();
		if (!StaticInfo.isActivity(file, className))	return result;
		for (StaticLayout l: layoutList) {
			ArrayList<StaticViewNode> vNs = l.getAllViewNodes();
			for (StaticViewNode vN : vNs) {
				String id = vN.getID();
				Map<String, String> eHs = vN.getAllEventHandlers();
				for (Map.Entry<String, String> entry: eHs.entrySet()) {
					String eH = entry.getKey();
					String eHMethodSig = entry.getValue() + "(android.view.View)";
					if (methodSubSig.endsWith(eHMethodSig)) {
						result.add(className + "," + l.getName() + "," + id + "," + eH);
					}
				}
			}
		}
		return result;
	}
	
	private static String solveIntent(File file, String className, String methodFileName, int lineNumber) throws Exception {
		String result = "";
		// Step 1, find target
		String targetActivity = findIntentTarget(file, className, methodFileName, lineNumber);
		// Step 2, is it in onCreate or an EventHandler?
		File mIFile = new File(Paths.appDataDir + file.getName() + "/ClassesInfo/" + className + "/" + methodFileName + ".csv");
		BufferedReader in_mI = new BufferedReader(new FileReader(mIFile));
		String methodSubSig = in_mI.readLine().split(",")[5];
		in_mI.close();
		boolean inOnCreate = isOnCreate(file, className, methodSubSig);

		ArrayList<String> possibleEHs = findEventHandlersThatMightDirectlyCallThisMethod(file, className, methodSubSig);

		boolean inEventHandler = false;
		if (possibleEHs.size() > 0)	inEventHandler = true;
		// Step 3, get all possible incoming method calls, pick onCreate and EventHandlers from them
		ArrayList<String> onCreateCallers = new ArrayList<String>();
		ArrayList<String> possibleCallers = getAllPossibleIncomingCallers(className, methodSubSig);
		for (String caller: possibleCallers) {
			String callerClass = caller.split(":")[0];
			String callerMethodSig = caller.split(":")[1];
			if (isOnCreate(file, className, callerMethodSig))
				onCreateCallers.add(callerClass);
			ArrayList<String> eh = findEventHandlersThatMightDirectlyCallThisMethod(file, callerClass, callerMethodSig);
			for (String s: eh)
				if (!possibleEHs.contains(s))
					possibleEHs.add(s);
		}
		// "StartActivity",foundTargetActvt?,inOnCreate?,inEventHandler?,classname,methodname,linenumber,targetActvt,NumberOfOnCreateCaller,NumberOfEventHandlers,...,...
		result = "startActivity,";
		if (targetActivity.equals(" "))	result+="0,"; else result+="1,";
		if (inOnCreate)	result+="1,"; else result+="0,";
		if (inEventHandler)	result+="1,"; else result+="0,";
		result += className + "," + methodFileName + "," + lineNumber + "," + targetActivity + ",";
		result += onCreateCallers.size() + "," + possibleEHs.size();
		for (String s: onCreateCallers)	result += "," + s;
		for (String s: possibleEHs)	result += "," + s.replace(",", "&&");
		return result;
	}
	
	private static String solveSetContentView(File file, String className, String methodFileName, int lineNumber) throws Exception {
		String result = "";
		// Step 1, find target
		String targetLayout = findSetContentViewTarget(file, className, methodFileName, lineNumber);
		// Step 2, is it in onCreate or an EventHandler?
		File mIFile = new File(Paths.appDataDir + file.getName() + "/ClassesInfo/" + className + "/" + methodFileName + ".csv");
		BufferedReader in_mI = new BufferedReader(new FileReader(mIFile));
		String methodSubSig = in_mI.readLine().split(",")[5];
		in_mI.close();
		boolean inOnCreate = isOnCreate(file, className, methodSubSig);
		ArrayList<String> possibleEHs = findEventHandlersThatMightDirectlyCallThisMethod(file, className, methodSubSig);
		boolean inEventHandler = false;
		if (possibleEHs.size() > 0)	inEventHandler = true;
		// Step 3, get all possible incoming method calls, pick onCreate and EventHandlers from them
		ArrayList<String> onCreateCallers = new ArrayList<String>();
		ArrayList<String> possibleCallers = getAllPossibleIncomingCallers(className, methodSubSig);
		for (String caller: possibleCallers) {
			String callerClass = caller.split(":")[0];
			String callerMethodSig = caller.split(":")[1];
			if (isOnCreate(file, className, callerMethodSig))
				onCreateCallers.add(callerClass);
			ArrayList<String> eh = findEventHandlersThatMightDirectlyCallThisMethod(file, callerClass, callerMethodSig);
			for (String s: eh)
				if (!possibleEHs.contains(s))
					possibleEHs.add(s);
		}
		result = "setContentView,";
		if (targetLayout.equals(" "))	result+="0,"; else result+="1,";
		if (inOnCreate)	result+="1,"; else result+="0,";
		if (inEventHandler)	result+="1,"; else result+="0,";
		result += className + "," + methodFileName + "," + lineNumber + "," + targetLayout + ",";
		result += onCreateCallers.size() + "," + possibleEHs.size();
		for (String s: onCreateCallers)	result += "," + s;
		for (String s: possibleEHs)	result += "," + s.replace(",", "&&");
		return result;
	}
	
	public static String readDatFile(File file) {
		String result = "", currentLine = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			while ((currentLine = in.readLine())!=null)
				result+=currentLine+"\n";
			in.close();
		}	catch (Exception e) {e.printStackTrace();}
		return result;
	}
*/
}
