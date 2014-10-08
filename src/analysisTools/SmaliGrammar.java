package analysisTools;

public class SmaliGrammar {

	public static final String[] poundHeads = {
		"# interfaces",
		"# annotations",
		"# instance fields",
		"# static fields",
		"# direct methods",
		"# virtual methods",
		"#calls:",
		"getter for:",
		"setter for:",
		
	};
	
	public static final String[] labelHeads = {
		":cond",
		":goto",
		":catch", ":catchall", ":try_start", ":try_end",
		":array",
		":sswitch", ":sswitch_data", ":pswitch", ":pswitch_data",
	};
	
	public static final String[] dotHeads = {
		".class", ".super", ".source", ".implements", ".enum", 
		".annotation", ".end annotation", ".subannotation", ".end subannotation", 
		".field", ".end field",
		".method", ".end method", ".prologue", ".line", 
		".locals", ".local", ".end local", 
		".parameter", ".end parameter", 
		".catchall", ".catch", 
		".restart",
		".sparse-switch", ".end sparse-switch", 
		".packed-switch", ".end packed-switch", 
		".array-data", ".end array-data", 
	};
	
}
