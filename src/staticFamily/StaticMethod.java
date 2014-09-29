package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class StaticMethod implements Serializable{

	private String name;
	private String returnType;
	private StaticClass declaringClass;
	private String fullSignature;
	private String subSignature;
	private String bytecodeSignature;
	private ArrayList<String> parameters;
	private Map<String, String> localVariables;
	private Map<String, String> paramVariables;
	private String jimpleCode;
	private List<StaticStmt>	statements;
	private ArrayList<Integer>	sourceLineNumbers = new ArrayList<Integer>();
	private int returnLineNumber;
	private ArrayList<String> inCallSourceSigs = new ArrayList<String>();
	private ArrayList<String> outCallTargetSigs = new ArrayList<String>();
	private ArrayList<String> fieldRefSigs = new ArrayList<String>();
	
	public StaticMethod(String signature, String subSignature,
			String bytecodeSignature, boolean isAbstract, boolean isNative,
			int modifiers, ArrayList<String> parameters,
			Map<String, String> localVariables, Map<String, String> paramVariables,
			String sM_jimple, List<StaticStmt> statements) {
		
		this.fullSignature = signature.substring(1, signature.length()-1);
		this.subSignature = subSignature;
		this.bytecodeSignature = bytecodeSignature;
		this.parameters = parameters;
		this.localVariables = localVariables;
		this.paramVariables = paramVariables;
		this.jimpleCode = sM_jimple;
		this.returnType = subSignature.substring(0, subSignature.indexOf(" "));
		this.name = subSignature.substring(subSignature.indexOf(" ")+1, subSignature.indexOf("("));
		this.statements = statements;
		
	}

	public String getJimpleFullSignature() {	return fullSignature;	}
	
	public String getJimpleSubSignature() {		return subSignature;	}
	
	public String getBytecodeSignature() {		return bytecodeSignature;}
	
	public ArrayList<String> getParameters() {	return parameters;		}
	
	public Map<String, String> getLocalVariables() {	return localVariables; }
	
	public Map<String, String> getParamVariables() {	return paramVariables; }
	
	public String getDeclaringClassName() {	return declaringClass.getName();	}
	
	public String getJimpleCode() 			{	return jimpleCode;	}
	
	public List<StaticStmt> getStatements() {	return statements;	}
	
	public String getName() 				{	return name;}
	
	public String getReturnType() 			{	return returnType;}
	
	public List<Integer> getAllSourceLineNumbers() {	return sourceLineNumbers;	}
	
	public int getFirstLineNumber() 		{	return sourceLineNumbers.get(0);	}
	
	public int getReturnLineNumber() 		{	return returnLineNumber;	}
	
	public ArrayList<String> getOutCallTargetSigs() {return outCallTargetSigs;	}
	
	public ArrayList<String> getInCallSourceSigs() { return inCallSourceSigs;	}
	
	public ArrayList<String> getFieldRefSigs() 		{return fieldRefSigs;	}
	
	public void addOutCallTarget(String targetSig) {
		outCallTargetSigs.add(targetSig);
	}
	
	public void addInCallSource(String sourceSig) {
		inCallSourceSigs.add(sourceSig);
	}
	
	public void addFieldRef(String fieldSig) {
		fieldRefSigs.add(fieldSig);
	}
	
	public void setDeclaringClass(StaticClass c) {
		declaringClass = c;
	}
	
	public void addSourceLineNumber(int i) {
		sourceLineNumbers.add(i);
	}
	
	public void setReturnLineNumber(int i) {
		this.returnLineNumber = i;
	}
	
}
