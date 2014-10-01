package staticFamily;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StaticStmt implements Serializable {

	private String jimpleStmt;
	private String familyMethodSig;
	private boolean containsMethodCall;
	private boolean containsFieldRef;
	private String targetSignature;

	public StaticStmt(String stmt) {
		this.jimpleStmt = stmt;
		this.containsMethodCall = false;
		this.containsFieldRef = false;
		this.targetSignature = "";
	}

	///////// read attributes
	public String getJimpleStmt() {
		return jimpleStmt;
	}

	public boolean containsMethodCall() {
		return containsMethodCall;
	}

	public boolean containsFieldRef() {
		return containsFieldRef;
	}

	public String getFamilyMethodSignature() {
		return familyMethodSig;
	} 
	
	public StaticMethod getFamilyMethod(StaticApp testApp) {
		return testApp.findMethodByFullSignature(familyMethodSig);
	}

	public String getTargetSignature() {
		return targetSignature;
	}
	//////// add/set attributes

	public void setFamilyMethod(String fullSig) {
		this.familyMethodSig = fullSig;
	}

	public void setTargetSignature(String targetSig) {
		this.targetSignature = targetSig;
	}

	public void setContainsFieldRef(boolean flag) {
		this.containsFieldRef = flag;
	}
	
	public void setContainsMethodCall(boolean flag) {
		this.containsMethodCall = flag;
	}
}
