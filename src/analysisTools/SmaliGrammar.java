package analysisTools;

public class SmaliGrammar {

	
	public static final String[] poundHeads = {
		"# interfaces",
		"# annotations",
		"# instance fields",
		"# static fields",
		"# direct methods",
		"# virtual methods",
		// these 3 probably doesn't matter
		"#calls:",
		"#getter for:",
		"#setter for:",
	};
	
	public static final String[] instructions_that_jumps = {
		"if-",				// if-eqz v0, :cond_0
		"goto",				// goto :goto_0
		"fill-array-data",	// fill-array-data v0, :array_0
		".catch",			// .catch EXCEPTION/TYPE {:try_start_0 .. :try_end_0} :catch_0
		"sparse-switch",	// sparse-switch p1, :sswitch_data_0
							//  :sswitch_data_0
							//	.sparse-switch
							//	0x1389 -> :sswitch_1
							//	0x138a -> :sswitch_2
							//  .end sparse-switch
		"packed-switch",	// packed-switch v2, :pswitch_data_0
							//  :pswitch_data_0
							//  .packed-switch 0x1
							//  :pswitch_0
							//  :pswitch_1
							//  .end packed-switch
	};
	
	public static final String[] labelHeads = {
		":cond",
		":goto",
		":catch", ":catchall", ":try_start", ":try_end", // can probably ignore these
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
