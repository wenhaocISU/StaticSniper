package staticFamily;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.Paths;

@SuppressWarnings("serial")
public class StaticApp implements Serializable {

	private File testApp;
	public String instrumentedAppPath;
	public String outPath;
	private String packageName;
	private List<StaticClass> classList;

	public StaticApp(File app) {
		classList = new ArrayList<StaticClass>();
		testApp = app;
		outPath = Paths.appDataDir + testApp.getName();
		instrumentedAppPath = outPath + "/InstrumentedApps/";
	}

	////////////// attribute operation

	public String getSmaliInstrumentedAppPath() {
		String name = testApp.getName().substring(0, testApp.getName().lastIndexOf(".apk"));
		return instrumentedAppPath + name + "_smali.apk";
	}
	
	public String getSootInstrumentedAppPath() {
		String name = testApp.getName().substring(0, testApp.getName().lastIndexOf(".apk"));
		return instrumentedAppPath + name + "_soot.apk";
	}
	
	
	public StaticClass getMainActivity() {
		for (StaticClass c : classList) {
			if (c.isMainActivity())
				return c;
		}
		return null;
	}

	public List<StaticClass> getClassList() {
		return classList;
	}

	public List<StaticClass> getActivityList() {
		List<StaticClass> result = new ArrayList<StaticClass>();
		for (StaticClass c : classList)
			if (c.isActivity())
				result.add(c);
		return result;
	}

	public File getAPKFile() {
		return testApp;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String pkgName) {
		packageName = pkgName;
	}

	public void addClass(StaticClass c) {
		classList.add(c);
	}

	// ///////// query methods

	public boolean hasClass(String className) {
		for (StaticClass c : classList)
			if (c.getName().equals(className))
				return true;
		return false;
	}

	public StaticClass findClassByName(String className) {
		for (StaticClass c : classList)
			if (c.getName().equals(className))
				return c;
		return null;
	}

	public StaticClass findClassByDexName(String dexName) {
		for (StaticClass c : classList)
			if (c.getDexFormatName().equals(dexName))
				return c;
		return null;
	}
	
	public StaticMethod findMethodFromLineNumber(String className, int lineNumber) {
		StaticClass c = findClassByName(className);
		for (StaticMethod m : c.getMethodList()) {
			if (m.getAllSourceLineNumbers().contains(lineNumber))
				return m;
		}
		return null;
	}
	
	public StaticMethod findMethodByName(String className, String methodName) {
		return findClassByName(className).findMethodByName(methodName);
	}

	public StaticMethod findMethodByFullSignature(String fullSig) {
		for (StaticClass c : classList)
			for (StaticMethod m : c.getMethodList())
				if (m.getFullJimpleSignature().equals(fullSig))
					return m;
		return null;
	}

	public StaticMethod findMethodBySubSignature(String className, String subSig) {
		return findClassByName(className).findMethodBySubSignature(subSig);
	}

	public StaticMethod findMethodByBytecodeSignature(String bcSig) {
		for (StaticClass c : classList)
			for (StaticMethod m : c.getMethodList())
				if (m.getBytecodeSignature().equals(bcSig))
					return m;
		return null;
	}

	public StaticField findFieldByName(String className, String fieldName) {
		for (StaticField f : findClassByName(className).getFieldList())
			if (f.getName().equals(fieldName))
				return f;
		return null;
	}

	public StaticField findFieldBySubSignature(String className, String fieldSubSig) {
		for (StaticField f : findClassByName(className).getFieldList())
			if (f.getSubJimpleSignature().equals(fieldSubSig))
				return f;
		return null;
	}

	public StaticField findFieldByFullSignature(String fullSig) {
		for (StaticClass c : classList)
			for (StaticField f : c.getFieldList())
				if (f.getFullJimpleSignature().equals(fullSig))
					return f;
		return null;
	}

	///////////////////////////////////////
	
	public ArrayList<String> getCallSequenceForMethod(StaticMethod m) {
		Map<String, Boolean> callMap = new HashMap<String, Boolean>();
		callMap.put(m.getFullJimpleSignature(), false);
		boolean finished = false;
		while (!finished) {
			ArrayList<String> addAfterIteration = new ArrayList<String>();
			Iterator<Map.Entry<String, Boolean>> it = callMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Boolean> entry = it.next();
				if (entry.getValue())
					continue;
				String currentSource = entry.getKey();
				if (entry.getKey().contains(","))
					currentSource = entry.getKey().split(",")[entry.getKey().split(",").length-1];
				StaticMethod srcM = findMethodByFullSignature(currentSource);
				if (srcM == null || srcM.getInCallSourceSigs().size()<1) {
					entry.setValue(true);
					continue;
				}
				ArrayList<String> newSources = srcM.getInCallSourceSigs();
				int newSrcCount = 0;
				for (String newSrc : newSources) {
					if (callMap.containsKey(newSrc))
						continue;
					newSrcCount++;
					addAfterIteration.add(entry.getKey() + "," + newSrc);
				}
				if (newSrcCount > 0)
					it.remove();
			}
			for (String newEntry: addAfterIteration)
				callMap.put(newEntry, false);
			finished = true;
			for (Map.Entry<String, Boolean> e : callMap.entrySet()) {
				if (!e.getValue()) {
					finished = false;
					break;
				}
			}
		}
		ArrayList<String> results = new ArrayList<String>();
		for (Map.Entry<String, Boolean> e : callMap.entrySet())
			results.add(e.getKey());
		return results;
	}
	
}
