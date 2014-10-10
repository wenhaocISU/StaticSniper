package staticFamily;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import smali.BlockLabel;

@SuppressWarnings("serial")
public class StaticSmaliStmt implements Serializable{

	private String smaliStmt;
	private int srcLineNumber;
	private boolean hasRealSourceLineNumber;
	private int stmtID;
	private BlockLabel blockLabel;
	private boolean flowsThrough, branches, goesTo, returns;
	private Map<String, String> switchTargetMap;
	private String jumpTargetLabel;
	private String pswitchInitValue;
	private String catchRangeLabel;
	private String exceptionType;
	private boolean isArrayDefStmt;
	
	private boolean isInvokeStmt, invokeResultMoved;
	private int moveInvokeResultToID;
	private String invokeTarget;
	
	private boolean isGetFieldStmt;
	private boolean isPutFieldStmt;
	private String fieldTarget;
	
	private boolean isMoveResultStmt;
	private int moveInvokeResultFromID;
	
	public StaticSmaliStmt() {
		this.switchTargetMap = new HashMap<String, String>();
		this.setJumpTargetLabel("");
		this.setSmaliStmt("");
		this.setSourceLineNumber(-1);
		this.setStmtID(-1);
		this.setBlockLabel(null);
		this.setFlowsThrough(true);
		this.setBranches(false);
		this.setJumpTargetLabel("");
		this.setCatchRangeLabel("");
		this.setExceptionType("");
		this.setIsArrayDefStmt(false);
		this.setGoesTo(false);
		this.setReturns(false);
		this.setIsInvoke(false);
		this.setIsGetFieldStmt(false);
		this.setIsPutFieldStmt(false);
		this.setInvokeTarget("");
		this.setFieldTarget("");
		this.setInvokeResultMoved(false);
		this.setMoveInvokeResultFromID(-1);
		this.setIsMoveResultStmt(false);
		this.setMoveInvokeResultToID(-1);
		this.setHasRealSourceLineNumber(false);
	}
	
	public StaticSmaliStmt(String smaliStmt) {
		this.switchTargetMap = new HashMap<String, String>();
		this.setJumpTargetLabel("");
		this.setSmaliStmt(smaliStmt);
		this.setSourceLineNumber(-1);
		this.setStmtID(-1);
		this.setBlockLabel(null);
		this.setFlowsThrough(true);
		this.setBranches(false);
		this.setJumpTargetLabel("");
		this.setCatchRangeLabel("");
		this.setExceptionType("");
		this.setIsArrayDefStmt(false);
		this.setGoesTo(false);
		this.setReturns(false);
		this.setIsInvoke(false);
		this.setIsGetFieldStmt(false);
		this.setIsPutFieldStmt(false);
		this.setInvokeTarget("");
		this.setFieldTarget("");
		this.setInvokeResultMoved(false);
		this.setMoveInvokeResultFromID(-1);
		this.setIsMoveResultStmt(false);
		this.setMoveInvokeResultToID(-1);
		this.setHasRealSourceLineNumber(false);
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
		return switchTargetMap;
	}

	public void addSwitchTarget(String value, String targetLabel) {
		this.switchTargetMap.put(value, targetLabel);
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

	
}
