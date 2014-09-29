package staticFamily;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.Paths;

@SuppressWarnings("serial")
public class StaticApp implements Serializable{

	private File testApp;
	public String outPath;
	private String packageName;
	private List<StaticClass> classList;
	private List<StaticClass> activityList;
	
	public StaticApp(File app) {
		classList = new ArrayList<StaticClass>();
		activityList = new ArrayList<StaticClass>();
		testApp = app;
		outPath = Paths.appDataDir + testApp.getName();
	}
	
////////////// attribute operation
	
	public StaticClass getMainActivity() {
		return activityList.get(0);
	}
	
	public List<StaticClass> getClassList() {
		return classList;
	}
	
	public List<StaticClass> getActivityList() {
		return activityList;
	}
	
	public File getTestApp() {
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
	
/////////// query methods
	
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
	
	public StaticMethod findMethodByName(String className, String methodName) {
		return findClassByName(className).findMethodByName(methodName);
	}
	
	public StaticMethod findMethodByFullSignature(String fullSig) {
		for (StaticClass c : classList)
			for (StaticMethod m : c.getMethodList())
				if (m.getJimpleFullSignature().equals(fullSig))
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
			if (f.getSubSignature().equals(fieldSubSig))
				return f;
		return null;
	}
	
	public StaticField findFieldByFullSignature(String fullSig) {
		String className = fullSig.split(": ")[0];
		String fieldSubSig = fullSig.split(": ")[1];
		return findFieldBySubSignature(className, fieldSubSig);
	}

}
