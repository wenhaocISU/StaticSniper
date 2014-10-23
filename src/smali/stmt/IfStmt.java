package smali.stmt;

public class IfStmt extends SmaliStmt{

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
