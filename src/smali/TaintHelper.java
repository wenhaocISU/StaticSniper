package smali;

import java.util.ArrayList;

import staticFamily.StaticApp;
import staticFamily.StaticMethod;
import staticFamily.StaticSmaliStmt;

public class TaintHelper {

	private StaticApp testApp;
	
	public TaintHelper(StaticApp staticApp) {
		this.testApp = staticApp;
	}
	/**
	 * suppose the lines that were hit during jdb are:
	 *    line 1
	 *    line 2
	 *    line 3
	 *    line 4
	 *    line 8
	 *    line 9
	 *    line 10
	 * and we wanna know how to hit line 5,6,7
	 * 
	 * 
	 * */
	public ArrayList<StaticMethod> findMissingMethods(String methodSignature, ArrayList<Integer> linesHit, int targetLine) {
		ArrayList<StaticMethod> result = new ArrayList<StaticMethod>();
		StaticMethod m = testApp.findMethodByFullSignature(methodSignature);
		StaticSmaliStmt s = m.getSmaliStmt(targetLine);
		BlockLabel label = s.getBlockLabel();
		ArrayList<StaticSmaliStmt> stmtsIn = new ArrayList<StaticSmaliStmt>();
		
		return result;
	}
	
	private void analyzeHitLog(ArrayList<Integer> linesHit) {
		
	}
	
}
