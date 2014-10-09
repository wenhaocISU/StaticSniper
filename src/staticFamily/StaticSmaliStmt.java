package staticFamily;

import java.util.HashMap;
import java.util.Map;

import smali.BlockLabel;

public class StaticSmaliStmt {

	private String smaliStmt;
	private int srcLineNumber;
	private int stmtID;
	private BlockLabel blockLabel;
	private boolean flowsThrough, branches;
	private Map<String, String> switchMap;
	private String jumpTargetLabel;
	
	public StaticSmaliStmt() {
		this.switchMap = new HashMap<String, String>();
		this.setJumpTargetLabel("");
		this.setSmaliStmt("");
		this.setSourceLineNumber(-1);
		this.setStmtID(-1);
		this.setBlockLabel(null);
		this.setFlowsThrough(true);
		this.setBranches(false);
		this.setJumpTargetLabel("");
	}
	
	public StaticSmaliStmt(String smaliStmt) {
		this.smaliStmt = smaliStmt;
	}

	//////// setters & getters
	public String getSmaliStmt() {
		return smaliStmt;
	}	
	
	public void setSmaliStmt(String smaliStmt) {
		this.smaliStmt = smaliStmt;
	}
	
	public int getSourceLineNumber() {
		return srcLineNumber;
	}

	public void setSourceLineNumber(int lineNumber) {
		this.srcLineNumber = lineNumber;
	}

	public BlockLabel getBlockLabel() {
		return blockLabel;
	}

	public void setBlockLabel(BlockLabel blockLabel) {
		this.blockLabel = blockLabel;
	}

	public int getStmtID() {
		return stmtID;
	}

	public void setStmtID(int stmtID) {
		this.stmtID = stmtID;
	}

	public boolean flowsThrough() {
		return flowsThrough;
	}

	public void setFlowsThrough(boolean flowsThrough) {
		this.flowsThrough = flowsThrough;
	}

	public boolean branches() {
		return branches;
	}

	public void setBranches(boolean branches) {
		this.branches = branches;
	}

	public String getJumpTargetLabel() {
		return jumpTargetLabel;
	}

	public void setJumpTargetLabel(String jumpTargetLabel) {
		this.jumpTargetLabel = jumpTargetLabel;
	}

	public Map<String, String> getSwitchMap() {
		return switchMap;
	}

	public void addSwitchTarget(String value, String targetLabel) {
		this.switchMap.put(value, targetLabel);
	}

	
	
	
}
