package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StaticClass implements Serializable{
	
	private StaticApp app;
	private String name;
	private int modifiers;

	private List<StaticMethod> methods = new ArrayList<StaticMethod>();
	private List<StaticField> fields = new ArrayList<StaticField>();
	
	private ArrayList<String> interfaceNames = new ArrayList<String>();
	private ArrayList<String> innerClassNames = new ArrayList<String>();
	private String superClassName = "";
	private String outerClassName = "";

	private boolean isInnerClass;
	private boolean isInterface;
	private boolean isAbstract;
	private boolean isInDEX;

	private boolean isActivity;
	private boolean isMainActivity;
	
	public StaticClass(String className) {
		name = className;
		isInterface = false;		isAbstract = false;			isInDEX = false;
		isActivity = false;			isMainActivity = false;
	}

	public StaticClass(String className, ArrayList<String> interfaceList,
			List<StaticField> fList, List<StaticMethod> mList,
			boolean isInnerClass, boolean isAbstractClass,
			boolean isApplicationClass, boolean isInterface,
			int modifiers, String superClassName, String outerClassName) {
		
		this.name = className;
		this.modifiers = modifiers;
		this.interfaceNames = interfaceList;
		this.fields = fList;
		this.methods = mList;
		this.isInnerClass = isInnerClass;
		this.isAbstract = isAbstractClass;
		this.isInDEX = isApplicationClass;
		this.isInterface = isInterface;
		this.superClassName = superClassName;
		this.outerClassName = outerClassName;
		
		this.isActivity = false;
		this.isMainActivity = false;

	}
	
////////////// query methods
	
	public boolean hasInnerClass() {
		if (innerClassNames.size() > 0)
			return true;
		else return false;
	}
	
	public boolean hasSuperClass() {
		if (superClassName.equals("") || superClassName.equals("java.lang.Object"))
			return false;
		else return true;
	}
	
	public boolean hasInterface() {
		if (interfaceNames.size()>0)
			return true;
		else return false;
	}

	
	public boolean isInnerClass() {		return isInnerClass;	}
	
	public boolean isAbstract() {		return isAbstract;		}
	
	public boolean isInterface() {		return isInterface;		}
	
	public boolean isActivity()  {		return isActivity;		}
	
	public boolean isMainActivity() {	return isMainActivity;	}

	public String getName() 		{	return name;			}
	
	public String getSuperClassName()	{return superClassName; }
	
	public String getOuterClassName()	{return outerClassName;	}
	
	public int getModifier()			{return modifiers;		}
	
	public List<StaticMethod> getMethodList() {	return methods;	}
	
	public List<StaticField> getFieldList()	{	return fields;	}
	
	public ArrayList<String> getInterfaceList() { return interfaceNames;}
	
	public List<String> getInnerClassNames()	{ return innerClassNames;}
	
	public boolean isInDEX() {	return isInDEX;	}

	public StaticField findField(String fieldName) {
		for (StaticField f : fields)
			if (f.getName().equals(fieldName))
				return f;
		return null;
	}
	
	public StaticField findField(String fieldName, String fieldType) {
		for (StaticField f : fields)
			if (f.getName().equals(fieldName) && f.getType().equals(fieldType))
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
			if (m.getJimpleFullSignature().equals(fullSig))
				return m;
		return null;
	}
	
	public StaticMethod findMethodBySubSignature(String subSig) {
		for (StaticMethod m : methods) {
			if (m.getJimpleSubSignature().equals(subSig))
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
	
	public List<Integer> getAllSourceLineNumbers() {
		List<Integer> result = new ArrayList<Integer>();
		for (StaticMethod m : methods)
			for (int i : m.getAllSourceLineNumbers())
				result.add(i);
		return result;
	}
	
	
	public void setIsActivity(boolean flag) {
		isActivity = flag;
	}
	
	public void setIsMainActivity(boolean flag) {
		isMainActivity = flag;
	}

	public void addInnerClass(String innerClassName) {
		innerClassNames.add(innerClassName);
	}
	
	public void setStaticApp(StaticApp app) {
		this.app = app;
	}
	
	public StaticApp getStaticApp() {
		return app;
	}

}
