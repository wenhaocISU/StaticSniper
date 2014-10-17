package smali.BackTrackAnalysis;

import java.util.ArrayList;
import java.util.Arrays;

import staticFamily.StaticSmaliStmt;

public class SmaliStmtParser {
	
	private StaticSmaliStmt s;
	
	private final ArrayList<String> stmt_1v = new ArrayList<String>(Arrays.asList(
			"if-eqz",
			"if-nez",
			"if-ltz",
			"if-gez",
			"if-gtz",
			"if-lez",
			"packed-switch",
			"sparse-switch"
	));
	
	private final ArrayList<String> stmt_2v = new ArrayList<String>(Arrays.asList(
			"if-eq",
			"if-ne",
			"if-lt",
			"if-ge",
			"if-gt",
			"if-le"
	));

	public SmaliStmtParser(StaticSmaliStmt theStmt) {
		this.s = theStmt;
	}
	
	private boolean hasOneVariable() {
		if (s.getSmaliStmt().contains(" "))
			return stmt_1v.contains(s.getSmaliStmt().split(" ")[0]);
		return false;
	}
	
	
	public String getFirstVariable() {
		if (s.getSmaliStmt().contains(" ")) {
			String[] splits = s.getSmaliStmt().split(" ");
			for (int i = 0, len = splits.length; i < len; i++) {
				String s = splits[i];
				if (s.endsWith(","))
					return s.substring(0, s.length()-1);
			}
		}
		return "";
	}
	
	public String getSecondVariable() {
		if (hasTwoVariables()) {
			String[] splits = s.getSmaliStmt().split(" ");
			int index = 1;
			for (int i = 0, len = splits.length; i < len; i++) {
				String s = splits[i];
				if (s.endsWith(",")) {
					if (index == 1)
						index++;
					else if (index == 2) {
						return s.substring(0, s.length()-1);
					}
				}
			}
		}
		return "";
	}
	
	private boolean hasTwoVariables() {
		if (!s.getSmaliStmt().contains(" "))
			return false;
		if (stmt_2v.contains(s.getSmaliStmt().split(" ")[0]))
			return true;
		if (s.getSmaliStmt().contains(", "))
			return true;
		return false;
	}
	
}
