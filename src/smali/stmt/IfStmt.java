package smali.stmt;

public class IfStmt extends Stmt{

	private String targetLabel;

	public String getTargetLabel() {
		return targetLabel;
	}

	public void setTargetLabel(String targetLabel) {
		this.targetLabel = targetLabel;
	}
	
	public boolean isIfZ() {
		return (this.getTheStmt().split(" ")[0].endsWith("z"));
	}
	
}
