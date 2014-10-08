package analysisTools;

public class SmaliGrammar {

	public static final String[] labelHeads = {
		":cond",
		":goto",
		":catch", ":catchall", ":try_start", ":try_end",
		":array",
		":sswitch", ":sswitch_data", ":pswitch", ":pswitch_data",
	};
	
	public static final String[] dotHeads = {
		".class", ".super", ".source", ".implements",
		".annotation", 
		
	};
	
}
