package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smali.BlockLabel;

@SuppressWarnings("serial")
public class StaticMethod implements Serializable {

	private String declaringClassName;
	private String jimpleSignature, bytecodeSignature;
	private ArrayList<String> jimpleParameterTypes;
	private Map<String, String> jimpleLocalVariables, jimpleParamVariables;
	private String jimpleCode, smaliCode;
	private ArrayList<StaticJimpleStmt> jimpleStatements;
	private ArrayList<StaticSmaliStmt> smaliStatements;
	private ArrayList<Integer> sourceLineNumbers;
	private int returnLineNumber;
	private ArrayList<String> inCallSourceSigs;
	private ArrayList<String> outCallTargetSigs;
	private ArrayList<String> fieldRefSigs;
	private boolean isDeclaredHere, isAbstract, isNative, hasBody;
	private int modifiers;
	private boolean isPublic, isPrivate, isProtected, isStatic, isConstructor;

	public StaticMethod(String fullJimpleSig) {
		this.jimpleSignature = fullJimpleSig;
		this.bytecodeSignature = "";
		this.jimpleParameterTypes = new ArrayList<String>();
		this.jimpleLocalVariables = new HashMap<String, String>();
		this.jimpleParamVariables = new HashMap<String, String>();
		this.jimpleCode = "";
		this.smaliCode = "";
		this.jimpleStatements = new ArrayList<StaticJimpleStmt>();
		this.smaliStatements = new ArrayList<StaticSmaliStmt>();
		this.sourceLineNumbers = new ArrayList<Integer>();
		this.returnLineNumber = -1;
		this.inCallSourceSigs = new ArrayList<String>();
		this.outCallTargetSigs = new ArrayList<String>();
		this.fieldRefSigs = new ArrayList<String>();
		this.isDeclaredHere = false;
		this.isAbstract = false;
		this.isNative = false;
		this.hasBody = false;
		this.modifiers = -1;
	}

	///////// get attributes
	
	public StaticSmaliStmt getSmaliStmtByID(int stmtID) {
		for (StaticSmaliStmt s : smaliStatements)
			if (s.getStmtID() == stmtID)
				return s;
		return null;
	}
	
	public StaticSmaliStmt getSmaliStmtByLineNumber(int lineNumber) {
		for (StaticSmaliStmt s : smaliStatements)
			if (s.getSourceLineNumber() == lineNumber)
				return s;
		return null;
	}
	
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
		return jimpleParameterTypes;
	}

	public Map<String, String> getLocalVariables() {
		return jimpleLocalVariables;
	}

	public Map<String, String> getParamVariables() {
		return jimpleParamVariables;
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

	public String getSmaliCode() {
		return smaliCode;
	}
	
	public ArrayList<StaticJimpleStmt> getJimpleStatements() {
		return jimpleStatements;
	}

	public ArrayList<StaticSmaliStmt> getSmaliStatements() {
		return smaliStatements;
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
		if (sourceLineNumbers.size() > 0)
			return sourceLineNumbers.get(0);
		else return -1;
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
	
	public boolean isConstructor() {
		return isConstructor;
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

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public boolean isStatic() {
		return isStatic;
	}
	
	public boolean isDeclaredHere() {
		return this.isDeclaredHere;
	}
	
	///////// add attributes
	
	public void addStmt(StaticJimpleStmt s) {
		jimpleStatements.add(s);
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
		this.jimpleParameterTypes.add(pT);
	}
	
	public void addLocalVariable(String name, String type) {
		this.jimpleLocalVariables.put(name, type);
	}
	
	public void addParamVariable(String name, String type) {
		this.jimpleParamVariables.put(name, type);
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

	public void setIsDeclaredHere(boolean flag) {
		this.isDeclaredHere = flag;
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

	public void setIsStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public void setIsConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	public void addSmaliStatement(StaticSmaliStmt smaliStatement) {
		this.smaliStatements.add(smaliStatement);
	}

	public void setSmaliCode(String smaliCode) {
		this.smaliCode = smaliCode;
	}

	/////////////////////////////////////// utility
	
	public List<StaticSmaliStmt> getSmaliStmtsInNormalBlock(BlockLabel bl) {
		List<StaticSmaliStmt> result = new ArrayList<StaticSmaliStmt>();
		for (StaticSmaliStmt s : smaliStatements) {
			if (s.getBlockLabel().hasSameNormalLabels(bl))
				result.add(s);
		}
		return result;
	}
	
	public List<BlockLabel> getBasicBlockLabels() {
		List<BlockLabel> result = new ArrayList<BlockLabel>();
		for (StaticSmaliStmt s : smaliStatements) {
			BlockLabel newL = s.getBlockLabel();
			boolean exists = false;
			for (BlockLabel oldL : result)
				if (oldL.hasSameNormalLabels(newL) && oldL.hasSameNormalLabelSection(newL)) {
					exists = true;
					break;
				}
			if (!exists)
				result.add(newL);
		}
		return result;
	}
	
	public Map<BlockLabel, List<StaticSmaliStmt>> getBlockGraph() {
		Map<BlockLabel, List<StaticSmaliStmt>> blockGraph = new HashMap<BlockLabel, List<StaticSmaliStmt>>();
		for (StaticSmaliStmt s : smaliStatements) {
			BlockLabel key = s.getBlockLabel();
			List<StaticSmaliStmt> value = new ArrayList<StaticSmaliStmt>();
			for (Map.Entry<BlockLabel, List<StaticSmaliStmt>> entry : blockGraph.entrySet()) {
				if (entry.getKey().isSameNormalLabel(s.getBlockLabel())) {
					key = entry.getKey();
					value = entry.getValue();
					break;
				}
			}
			value.add(s);
			blockGraph.put(key, value);
		}
		return blockGraph;
	}
	
	
	

}
