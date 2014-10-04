package staticFamily;

public class StaticSmaliStmt {

	private String smaliStmt;
	private int lineNumber;
	
	public StaticSmaliStmt(String smaliStmt) {
		this.smaliStmt = smaliStmt;
	}

	//////// setters & getters
	public String getSmaliStmt() {
		return smaliStmt;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	
	
	
}
