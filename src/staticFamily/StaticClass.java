package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StaticClass implements Serializable {

	private String name;
	private String dexFormatName;
	
	private int modifiers;

	private List<StaticMethod> methods;
	private List<StaticField> fields;

	private ArrayList<String> interfaceNames;
	private ArrayList<String> innerClassNames;
	private String superClassName;
	private String outerClassName;

	private boolean isPublic, isPrivate, isProtected;
	private boolean isInnerClass;
	private boolean isInterface;
	private boolean isAbstract;
	private boolean isInDEX;
	private boolean isActivity;
	private boolean isMainActivity;

	public StaticClass(String className) {
		name = className;
		modifiers = -1;
		
		methods = new ArrayList<StaticMethod>();
		fields = new ArrayList<StaticField>();
		
		interfaceNames = new ArrayList<String>();
		innerClassNames = new ArrayList<String>();
		superClassName = "";
		outerClassName = "";
		
		isInnerClass = false;
		isInterface = false;
		isAbstract = false;
		isInDEX = false;
		isActivity = false;
		isMainActivity = false;
		

	}

	// //////////// get attributes
	public boolean hasInnerClass() {
		if (innerClassNames.size() > 0)
			return true;
		else
			return false;
	}

	public boolean hasSuperClass() {
		if (superClassName.equals("")
				|| superClassName.equals("java.lang.Object"))
			return false;
		else
			return true;
	}

	public boolean hasInterface() {
		if (interfaceNames.size() > 0)
			return true;
		else
			return false;
	}

	public boolean isPublic() {
		return isPublic;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public boolean isProtected() {
		return isProtected;
	}
	
	public boolean isInDEX() {
		return isInDEX;
	}
	
	public boolean isInnerClass() {
		return isInnerClass;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public boolean isActivity() {
		return isActivity;
	}

	public boolean isMainActivity() {
		return isMainActivity;
	}

	public String getName() {
		return name;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public String getOuterClassName() {
		return outerClassName;
	}
	
	public StaticClass getOuterClass(StaticApp testApp) {
		return testApp.findClassByName(outerClassName);
	}

	public int getModifier() {
		return modifiers;
	}

	public List<StaticMethod> getMethodList() {
		return methods;
	}

	public List<StaticField> getFieldList() {
		return fields;
	}

	public ArrayList<String> getInterfaceList() {
		return interfaceNames;
	}

	public List<String> getInnerClassNames() {
		return innerClassNames;
	}
	
	public List<StaticClass> getInnerClassList(StaticApp testApp) {
		List<StaticClass> result = new ArrayList<StaticClass>();
		for (String innerC : innerClassNames) {
			result.add(testApp.findClassByName(innerC));
		}
		return result;
	}
	
	public List<Integer> getAllSourceLineNumbers() {
		List<Integer> result = new ArrayList<Integer>();
		for (StaticMethod m : methods)
			for (int i : m.getAllSourceLineNumbers())
				result.add(i);
		return result;
	}

	////////// find method, field
	
	public StaticField findFieldByName(String fieldName) {
		for (StaticField f : fields)
			if (f.getName().equals(fieldName))
				return f;
		return null;
	}

	public StaticField findFieldByNameType(String fieldName, String fieldType) {
		for (StaticField f : fields)
			if (f.getName().equals(fieldName) && f.getType().equals(fieldType))
				return f;
		return null;
	}
	
	public StaticField findFieldByFullSignature(String fullSig) {
		for (StaticField f : fields)
			if (f.getFullJimpleSignature().equals(fullSig))
				return f;
		return null;
	}
	
	public StaticMethod findMethodByName(String methodName) {
		for (StaticMethod m : methods)
			if (m.getName().equals(methodName))
				return m;
		return null;
	}

	public StaticMethod findMethodByFullSignature(String fullSig) {
		for (StaticMethod m : methods)
			if (m.getFullJimpleSignature().equals(fullSig))
				return m;
		return null;
	}

	public StaticMethod findMethodBySubSignature(String subSig) {
		for (StaticMethod m : methods) {
			if (m.getSubJimpleSignature().equals(subSig))
				return m;
		}
		return null;
	}

	public StaticMethod findMethodByBytecodeSignature(String bcSig) {
		for (StaticMethod m : methods)
			if (m.getBytecodeSignature().equals(bcSig))
				return m;
		return null;
	}

	public StaticMethod findMethodBySourceLineNumber(int lineNo) {
		for (StaticMethod m : methods)
			if (m.getAllSourceLineNumbers().contains(lineNo))
				return m;
		return null;
	}

	////////// add/set attributes

	public void addInnerClass(String innerClassName) {
		innerClassNames.add(innerClassName);
	}

	public void addInterface(String interfaceName) {
		interfaceNames.add(interfaceName);
	}

	public void addMethod(StaticMethod m) {
		methods.add(m);
	}
	
	public void addField(StaticField f) {
		fields.add(f);
	}

	public void setIsActivity(boolean flag) {
		isActivity = flag;
	}

	public void setIsMainActivity(boolean flag) {
		isMainActivity = flag;
	}
	
	public void setIsInnerClass(boolean flag) {
		isInnerClass = flag;
	}

	public void setIsInterface(boolean flag) {
		isInterface = flag;
	}
	
	public void setIsAbstract(boolean flag) {
		isAbstract = flag;
	}

	public void setIsInDEX(boolean flag) {
		isInDEX = flag;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public void setSuperClass(String superClassName) {
		this.superClassName = superClassName;
	}
	
	public void setOuterClass(String outerClassName) {
		this.outerClassName = outerClassName;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setIsPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public void setIsProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	public String getDexFormatName() {
		return dexFormatName;
	}

	public void setDexFormatName(String dexFormatName) {
		this.dexFormatName = dexFormatName;
	}

}
