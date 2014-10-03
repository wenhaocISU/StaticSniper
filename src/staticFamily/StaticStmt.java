package staticFamily;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StaticStmt implements Serializable {

	private String jimpleStmt;
	private String familyMethodSig;
	
	private boolean containsMethodCall;
	private boolean containsFieldRef;
	
	private boolean isInvokeStmt;
	private boolean isBreakpointStmt;
	private boolean isAssignStmt;
	private boolean isIdentityStmt;
	private boolean isEnterMonitorStmt;
	private boolean isExitMonitorStmt;
	private boolean isGotoStmt;
	private boolean isIfStmt;
	private boolean isLookupSwitchStmt;
	private boolean isNopStmt;
	private boolean isRetStmt;
	private boolean isReturnStmt;
	private boolean isReturnVoidStmt;
	private boolean isTableSwitchStmt;
	private boolean isThrowStmt;
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
	
	public boolean isLookupSwitchStmt() {
		return isLookupSwitchStmt;
	}
	
	public boolean isIfStmt() {
		return isIfStmt;
	}
	
	public boolean isGotoStmt() {
		return isGotoStmt;
	}
	
	public boolean isExitMonitorStmt() {
		return isExitMonitorStmt;
	}
	
	public boolean isBreakpointStmt() {
		return isBreakpointStmt;
	}
	
	public boolean isAssignStmt() {
		return isAssignStmt;
	}
	
	public boolean isIdentityStmt() {
		return isIdentityStmt;
	}
	
	public boolean isEnterMonitorStmt() {
		return isEnterMonitorStmt;
	}
	
	public boolean isThrowStmt() {
		return isThrowStmt;
	}
	
	public boolean isTableSwitchStmt() {
		return isTableSwitchStmt;
	}
	
	public boolean isReturnVoidStmt() {
		return isReturnVoidStmt;
	}
	
	public boolean isReturnStmt() {
		return isReturnStmt;
	}
	
	public boolean isNopStmt() {
		return isNopStmt;
	}
	
	public boolean isRetStmt() {
		return isRetStmt;
	}

	public boolean isInvokeStmt() {
		return isInvokeStmt;
	}
	
	//////// add/set attributes

	public void setFamilyMethod(String fullSig) {
		this.familyMethodSig = fullSig;
	}

	public void setTargetSignature(String targetSig) {
		this.targetSignature = targetSig;
	}
	
	public void setContainsMethodCall(boolean flag) {
		this.containsMethodCall = flag;
	}
	
	public void setContainsFieldRef(boolean flag) {
		this.containsFieldRef = flag;
	}

	public void setIsBreakpointStmt(boolean isBreakpointStmt) {
		this.isBreakpointStmt = isBreakpointStmt;
	}

	public void setIsAssignStmt(boolean isAssignStmt) {
		this.isAssignStmt = isAssignStmt;
	}

	public void setIsIdentityStmt(boolean isIdentityStmt) {
		this.isIdentityStmt = isIdentityStmt;
	}

	public void setIsEnterMonitorStmt(boolean isEnterMonitorStmt) {
		this.isEnterMonitorStmt = isEnterMonitorStmt;
	}

	public void setIsExitMonitorStmt(boolean isExitMonitorStmt) {
		this.isExitMonitorStmt = isExitMonitorStmt;
	}

	public void setIsGotoStmt(boolean isGotoStmt) {
		this.isGotoStmt = isGotoStmt;
	}

	public void setIsIfStmt(boolean isIfStmt) {
		this.isIfStmt = isIfStmt;
	}

	public void setIsLookupSwitchStmt(boolean isLookupSwitchStmt) {
		this.isLookupSwitchStmt = isLookupSwitchStmt;
	}

	public void setIsRetStmt(boolean isRetStmt) {
		this.isRetStmt = isRetStmt;
	}

	public void setIsNopStmt(boolean isNopStmt) {
		this.isNopStmt = isNopStmt;
	}

	public void setIsReturnStmt(boolean isReturnStmt) {
		this.isReturnStmt = isReturnStmt;
	}

	public void setIsReturnVoidStmt(boolean isReturnVoidStmt) {
		this.isReturnVoidStmt = isReturnVoidStmt;
	}

	public void setIsTableSwitchStmt(boolean isTableSwitchStmt) {
		this.isTableSwitchStmt = isTableSwitchStmt;
	}

	public void setIsThrowStmt(boolean isThrowStmt) {
		this.isThrowStmt = isThrowStmt;
	}

	public void setIsInvokeStmt(boolean isInvokeStmt) {
		this.isInvokeStmt = isInvokeStmt;
	}
	
}
