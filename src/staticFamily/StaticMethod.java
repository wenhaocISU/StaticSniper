package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class StaticMethod implements Serializable {

	private String declaringClassName;
	private String jimpleSignature, bytecodeSignature;
	private ArrayList<String> parameterTypes;
	private Map<String, String> localVariables, paramVariables;
	private String jimpleCode;
	private List<StaticStmt> statements;
	private ArrayList<Integer> sourceLineNumbers;
	private int returnLineNumber;
	private ArrayList<String> inCallSourceSigs;
	private ArrayList<String> outCallTargetSigs;
	private ArrayList<String> fieldRefSigs;
	private boolean isAbstract, isNative, hasBody;
	private int modifiers;

	public StaticMethod(String fullJimpleSig) {
		this.jimpleSignature = fullJimpleSig;
		this.bytecodeSignature = "";
		this.parameterTypes = new ArrayList<String>();
		this.localVariables = new HashMap<String, String>();
		this.paramVariables = new HashMap<String, String>();
		this.jimpleCode = "";
		this.statements = new ArrayList<StaticStmt>();
		this.sourceLineNumbers = new ArrayList<Integer>();
		this.returnLineNumber = -1;
		this.inCallSourceSigs = new ArrayList<String>();
		this.outCallTargetSigs = new ArrayList<String>();
		this.fieldRefSigs = new ArrayList<String>();
		this.isAbstract = false;
		this.isNative = false;
		this.hasBody = false;
		this.modifiers = -1;
	}

	///////// get attributes
	public String getFullJimpleSignature() {
		return jimpleSignature;
	}

	public String getSubJimpleSignature() {
		if (jimpleSignature.indexOf(": ") < jimpleSignature.length()-3)
			return jimpleSignature.substring(jimpleSignature.indexOf(": ")+2, jimpleSignature.length()-1);
		else return "";
	}

	public String getBytecodeSignature() {
		return bytecodeSignature;
	}

	public ArrayList<String> getParameterTypes() {
		return parameterTypes;
	}

	public Map<String, String> getLocalVariables() {
		return localVariables;
	}

	public Map<String, String> getParamVariables() {
		return paramVariables;
	}

	public String getDeclaringClassName() {
		return declaringClassName;
	}

	public StaticClass getDeclaringClass(StaticApp testApp) {
		return testApp.findClassByName(declaringClassName);
	}
	
	public String getJimpleCode() {
		return jimpleCode;
	}

	public List<StaticStmt> getStatements() {
		return statements;
	}

	public String getName() {
		if (this.getSubJimpleSignature().split(" ").length > 1)
			return this.getSubJimpleSignature().split(" ")[1];
		return "";
	}

	public String getReturnType() {
		if (this.getSubJimpleSignature().split(" ").length > 1)
			return this.getSubJimpleSignature().split(" ")[0];
		return "";
	}

	public int getModifiers() {
		return modifiers;
	}
	
	public List<Integer> getAllSourceLineNumbers() {
		return sourceLineNumbers;
	}

	public int getFirstLineNumber() {
		return sourceLineNumbers.get(0);
	}

	public int getReturnLineNumber() {
		return returnLineNumber;
	}

	public ArrayList<String> getOutCallTargetSigs() {
		return outCallTargetSigs;
	}

	public ArrayList<String> getInCallSourceSigs() {
		return inCallSourceSigs;
	}

	public ArrayList<String> getFieldRefSigs() {
		return fieldRefSigs;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public boolean isNative() {
		return isNative;
	}
	
	public boolean hasBody() {
		return hasBody;
	}

	///////// add attributes
	
	public void addStmt(StaticStmt s) {
		statements.add(s);
	}
	
	public void addOutCallTarget(String targetSig) {
		outCallTargetSigs.add(targetSig);
	}

	public void addInCallSource(String sourceSig) {
		inCallSourceSigs.add(sourceSig);
	}

	public void addFieldRef(String fieldSig) {
		fieldRefSigs.add(fieldSig);
	}

	public void addSourceLineNumber(int i) {
		sourceLineNumbers.add(i);
	}
	
	public void addParameterType(String pT) {
		this.parameterTypes.add(pT);
	}
	
	public void addLocalVariable(String name, String type) {
		this.localVariables.put(name, type);
	}
	
	public void addParamVariable(String name, String type) {
		this.paramVariables.put(name, type);
	}
	
	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}
	
	public void setDeclaringClass(String c) {
		declaringClassName = c;
	}

	public void setReturnLineNumber(int i) {
		this.returnLineNumber = i;
	}

	public void setBytecodeSignature(String bS) {
		this.bytecodeSignature = bS;
	}
	
	public void setJimpleCode(String jimpleCode) {
		this.jimpleCode = jimpleCode;
	}
	
	public void setIsAbstract(boolean flag) {
		this.isAbstract = flag;
	}

	public void setIsNative(boolean flag) {
		this.isNative = flag;
	}
	
	public void setHasBody(boolean flag) {
		this.hasBody = flag;
	}











}
