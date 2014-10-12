package staticFamily;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import smali.BlockLabel;

@SuppressWarnings("serial")
public class StaticSmaliStmt implements Serializable{

	private String smaliStmt = "";
	private int srcLineNumber = -1;
	private boolean hasRealSourceLineNumber = false;
	private int stmtID = -1;
	private BlockLabel blockLabel = new BlockLabel();
	
	private boolean flowsThrough = true;
	private boolean branches = false, goesTo = false, returns = false, switches = false;
	private String gotoTargetLabel = "";
	private String ifTargetLabel = "";
	private String switchTableLabel = "";
	private String pswitchInitValue = "";
	private String catchRangeLabel = "";
	private String exceptionType = "";
	
	private boolean isArrayDefStmt = false;
	
	private boolean isSwitchTable = false;
	private Map<String, String> switchTable = new HashMap<String, String>();
	private String switchTableName = "";
	
	private boolean isInvokeStmt = false, invokeResultMoved = false;
	private int moveInvokeResultToID = -1;
	private String invokeTarget = "";
	private boolean isMoveResultStmt = false;
	private int moveInvokeResultFromID = -1;
	
	private boolean isGetFieldStmt = false;
	private boolean isPutFieldStmt = false;
	private String fieldTarget = "";
	

	
	public StaticSmaliStmt() {

	}
	
	public StaticSmaliStmt(String smaliStmt) {
		this.setSmaliStmt(smaliStmt);
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
		BlockLabel l = new BlockLabel();
		l.setNormalLabels(blockLabel.getNormalLabels());
		l.setTryLabels(blockLabel.getTryLabels());
		l.setNormalLabelSection(blockLabel.getNormalLabelSection());
		this.blockLabel = l;
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

	public String getGotoTargetLabel() {
		return gotoTargetLabel;
	}

	public void setGotoTargetLabel(String jumpTargetLabel) {
		this.gotoTargetLabel = jumpTargetLabel;
	}

	public Map<String, String> getSwitchTable() {
		return switchTable;
	}

	public void addSwitchTarget(String value, String targetLabel) {
		this.switchTable.put(value, targetLabel);
	}

	public String getpswitchInitValue() {
		return pswitchInitValue;
	}

	public void setpswitchInitValue(String pswitchInitValue) {
		this.pswitchInitValue = pswitchInitValue;
	}

	public String getCatchLabel() {
		return catchRangeLabel;
	}

	public void setCatchRangeLabel(String catchLabel) {
		this.catchRangeLabel = catchLabel;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public boolean isArrayDefStmt() {
		return isArrayDefStmt;
	}

	public void setIsArrayDefStmt(boolean isArrayDefStmt) {
		this.isArrayDefStmt = isArrayDefStmt;
	}

	public boolean goesTo() {
		return goesTo;
	}

	public void setGoesTo(boolean goesTo) {
		this.goesTo = goesTo;
	}

	public boolean returns() {
		return returns;
	}

	public void setReturns(boolean returns) {
		this.returns = returns;
	}

	public boolean isInvoke() {
		return isInvokeStmt;
	}

	public void setIsInvoke(boolean isInvoke) {
		this.isInvokeStmt = isInvoke;
	}

	public boolean isGetFieldStmt() {
		return isGetFieldStmt;
	}

	public void setIsGetFieldStmt(boolean isGetFieldStmt) {
		this.isGetFieldStmt = isGetFieldStmt;
	}

	public boolean isPutFieldStmt() {
		return isPutFieldStmt;
	}

	public void setIsPutFieldStmt(boolean isPutFieldStmt) {
		this.isPutFieldStmt = isPutFieldStmt;
	}

	public String getInvokeTarget() {
		return invokeTarget;
	}

	public void setInvokeTarget(String invokeTarget) {
		this.invokeTarget = invokeTarget;
	}

	public String getFieldTarget() {
		return fieldTarget;
	}

	public void setFieldTarget(String fieldTarget) {
		this.fieldTarget = fieldTarget;
	}

	public boolean isMoveResultStmt() {
		return isMoveResultStmt;
	}

	public void setIsMoveResultStmt(boolean isMoveResultStmt) {
		this.isMoveResultStmt = isMoveResultStmt;
	}

	public boolean invokeResultMoved() {
		return invokeResultMoved;
	}

	public void setInvokeResultMoved(boolean invokeResultMoved) {
		this.invokeResultMoved = invokeResultMoved;
	}

	public int getMoveInvokeResultFromID() {
		return moveInvokeResultFromID;
	}

	public void setMoveInvokeResultFromID(int moveInvokeResultFromID) {
		this.moveInvokeResultFromID = moveInvokeResultFromID;
	}

	public int getMoveInvokeResultToID() {
		return moveInvokeResultToID;
	}

	public void setMoveInvokeResultToID(int moveInvokeResultToID) {
		this.moveInvokeResultToID = moveInvokeResultToID;
	}

	public boolean isHasRealSourceLineNumber() {
		return hasRealSourceLineNumber;
	}

	public void setHasRealSourceLineNumber(boolean hasRealSourceLineNumber) {
		this.hasRealSourceLineNumber = hasRealSourceLineNumber;
	}

	public String getIfTargetLabel() {
		return ifTargetLabel;
	}

	public void setIfTargetLabel(String ifTargetLabel) {
		this.ifTargetLabel = ifTargetLabel;
	}

	public String getSwitchTableLabel() {
		return switchTableLabel;
	}

	public void setSwitchTableLabel(String switchTableLabel) {
		this.switchTableLabel = switchTableLabel;
	}

	public boolean switches() {
		return switches;
	}

	public void setSwitches(boolean switches) {
		this.switches = switches;
	}

	public boolean isSwitchTable() {
		return isSwitchTable;
	}

	public void setIsSwitchTable(boolean isSwitchTable) {
		this.isSwitchTable = isSwitchTable;
	}

	public String getSwitchTableName() {
		return switchTableName;
	}

	public void setSwitchTableName(String switchTableName) {
		this.switchTableName = switchTableName;
	}

	
	
	///////////////////////////// utility
	
}
