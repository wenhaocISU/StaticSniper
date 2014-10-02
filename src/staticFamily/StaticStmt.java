package staticFamily;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StaticStmt implements Serializable {

	private String jimpleStmt;
	private String familyMethodSig;
	private boolean containsMethodCall;
	private boolean containsFieldRef;
	private boolean containsBreakpointStmt;
	private boolean containsAssignStmt;
	private boolean containsIdentityStmt;
	private boolean containsEnterMonitorStmt;
	private boolean containsExitMonitorStmt;
	private boolean containsGotoStmt;
	private boolean containsIfStmt;
	private boolean containsLookupSwitchStmt;
	private boolean containsNopStmt;
	private boolean containsRetStmt;
	private boolean containsReturnStmt;
	private boolean containsReturnVoidStmt;
	private boolean containsTableSwitchStmt;
	private boolean containsThrowStmt;
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
	
	public boolean containsLookupSwitchStmt() {
		return containsLookupSwitchStmt;
	}
	
	public boolean containsIfStmt() {
		return containsIfStmt;
	}
	
	public boolean containsGotoStmt() {
		return containsGotoStmt;
	}
	
	public boolean containsExitMonitorStmt() {
		return containsExitMonitorStmt;
	}
	
	public boolean containsBreakpointStmt() {
		return containsBreakpointStmt;
	}
	
	public boolean containsAssignStmt() {
		return containsAssignStmt;
	}
	
	public boolean containsIdentityStmt() {
		return containsIdentityStmt;
	}
	
	public boolean containsEnterMonitorStmt() {
		return containsEnterMonitorStmt;
	}
	
	public boolean containsThrowStmt() {
		return containsThrowStmt;
	}
	
	public boolean containsTableSwitchStmt() {
		return containsTableSwitchStmt;
	}
	
	public boolean containsReturnVoidStmt() {
		return containsReturnVoidStmt;
	}
	
	public boolean containsReturnStmt() {
		return containsReturnStmt;
	}
	
	public boolean containsNopStmt() {
		return containsNopStmt;
	}
	
	public boolean containsRetStmt() {
		return containsRetStmt;
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

	public void setContainsBreakpointStmt(boolean containsBreakpointStmt) {
		this.containsBreakpointStmt = containsBreakpointStmt;
	}

	public void setContainsAssignStmt(boolean containsAssignStmt) {
		this.containsAssignStmt = containsAssignStmt;
	}

	public void setContainsIdentityStmt(boolean containsIdentityStmt) {
		this.containsIdentityStmt = containsIdentityStmt;
	}

	public void setContainsEnterMonitorStmt(boolean containsEnterMonitorStmt) {
		this.containsEnterMonitorStmt = containsEnterMonitorStmt;
	}

	public void setContainsExitMonitorStmt(boolean containsExitMonitorStmt) {
		this.containsExitMonitorStmt = containsExitMonitorStmt;
	}

	public void setContainsGotoStmt(boolean containsGotoStmt) {
		this.containsGotoStmt = containsGotoStmt;
	}

	public void setContainsIfStmt(boolean containsIfStmt) {
		this.containsIfStmt = containsIfStmt;
	}

	public void setContainsLookupSwitchStmt(boolean containsLookupSwitchStmt) {
		this.containsLookupSwitchStmt = containsLookupSwitchStmt;
	}

	public void setContainsRetStmt(boolean containsRetStmt) {
		this.containsRetStmt = containsRetStmt;
	}

	public void setContainsNopStmt(boolean containsNopStmt) {
		this.containsNopStmt = containsNopStmt;
	}

	public void setContainsReturnStmt(boolean containsReturnStmt) {
		this.containsReturnStmt = containsReturnStmt;
	}

	public void setContainsReturnVoidStmt(boolean containsReturnVoidStmt) {
		this.containsReturnVoidStmt = containsReturnVoidStmt;
	}

	public void setContainsTableSwitchStmt(boolean containsTableSwitchStmt) {
		this.containsTableSwitchStmt = containsTableSwitchStmt;
	}

	public void setContainsThrowStmt(boolean containsThrowStmt) {
		this.containsThrowStmt = containsThrowStmt;
	}
	
}
