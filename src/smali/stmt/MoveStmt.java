package smali.stmt;

public class MoveStmt extends Stmt{

	private int resultMovedFrom = -1;
	
	public boolean isMoveResult() {
		return (this.getTheStmt().startsWith("move-result"));
	}

	public int getResultMovedFrom() {
		return resultMovedFrom;
	}

	public void setResultMovedFrom(int resultMovedFrom) {
		this.resultMovedFrom = resultMovedFrom;
	}
	
}
