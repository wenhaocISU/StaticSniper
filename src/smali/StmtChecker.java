package smali;

import java.util.ArrayList;
import java.util.Arrays;

public class StmtChecker {

	
	private static ArrayList<String> ifFormat = new ArrayList<String>(Arrays.asList(
			"if-eq",
			"if-ne",
			"if-lt",
			"if-ge",
			"if-gt",
			"if-le",
			"if-eqz",
			"if-nez",
			"if-ltz",
			"if-gez",
			"if-gtz",
			"if-lez"
	));
	
	private static ArrayList<String> invokeFormat = new ArrayList<String>(Arrays.asList(
			"invoke-virtual",
			"invoke-super",
			"invoke-direct",
			"invoke-static",
			"invoke-interface",
			"invoke-virtual/range",
			"invoke-virtual/range",
			"invoke-super/range",
			"invoke-direct/range",
			"invoke-static/range",
			"invoke-interface/range"
	));
	
	private static ArrayList<String> getFieldFormat = new ArrayList<String>(Arrays.asList(
			"iget",
			"iget-wide",
			"iget-object",
			"iget-boolean",
			"iget-byte",
			"iget-char",
			"iget-short",
			"sget",
			"sget-wide",
			"sget-object",
			"sget-boolean",
			"sget-byte",
			"sget-char",
			"sget-short"
	));
	
	private static ArrayList<String> putFieldFormat = new ArrayList<String>(Arrays.asList(
			"iput",
			"iput-wide",
			"iput-object",
			"iput-boolean",
			"iput-byte",
			"iput-char",
			"iput-short",
			"sput",
			"sput-wide",
			"sput-object",
			"sput-boolean",
			"sput-byte",
			"sput-char",
			"sput-short"
	));
	
	public static boolean isGoto(String l) {
		if (l.startsWith("goto :") ||
			l.startsWith("goto/16 :") ||
			l.startsWith("goto/32 :"))
			return true;
		return false;
	}
	
	public static boolean isReturn(String l) {
		if (l.startsWith("return-void") ||
			l.startsWith("return-object ") ||
			l.startsWith("return-wide ") ||
			l.startsWith("return "))
			return true;
		return false;
	}
	
	public static boolean isInvoke(String l) {
		if (l.startsWith("invoke") && l.contains(" ")) {
			if (invokeFormat.contains(l.split(" ")[0]))
				return true;
		}
		return false;
	}
	
	public static boolean isMoveResult(String l) {
		if (l.startsWith("move-result ") ||
			l.startsWith("move-result-wide ") ||
			l.startsWith("move-result-object "))
			return true;
		return false;
	}
	
	public static boolean isGetField(String l) {
		if (l.contains(" "))
			return getFieldFormat.contains(l.split(" ")[0]);
		return false;
	}
	
	public static boolean isPutField(String l) {
		if (l.contains(" "))
			return putFieldFormat.contains(l.split(" ")[0]);
		return false;
	}
	
	public static boolean isIfStmt(String l) {
		if (l.contains(", :"))
			return ifFormat.contains(l.split(" ")[0]);
		return false;
	}
	
	public static boolean isSwitch(String l) {
		if (l.startsWith("packed-switch ") ||
			l.startsWith("sparse-switch "))
			return true;
		return false;
	}
	
}
