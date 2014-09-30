package staticFamily;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StaticStmt implements Serializable {

	private StaticMethod familyMethod;
	private String jimpleCode;
	private boolean containsMethodCall;
	private boolean containsFieldRef;
	private String targetClass, targetSubSig;

	public StaticStmt(String stmt, boolean hasInvokeExpr, boolean hasFieldRef,
			String targetClass, String targetSubSig) {
		this.jimpleCode = stmt;
		this.containsMethodCall = hasInvokeExpr;
		this.containsFieldRef = hasFieldRef;
		this.targetClass = targetClass;
		this.targetSubSig = targetSubSig;
	}

	public String getJimpleStmt() {
		return jimpleCode;
	}

	public boolean containsMethodCall() {
		return containsMethodCall;
	}

	public boolean containsFieldRef() {
		return containsFieldRef;
	}

	public StaticMethod getResidingMethod() {
		return familyMethod;
	}

	public void setMethod(StaticMethod m) {
		this.familyMethod = m;
	}

	public String getTargetClass() {
		return targetClass;
	}

	public String getTargetSubSig() {
		return targetSubSig;
	}

}
