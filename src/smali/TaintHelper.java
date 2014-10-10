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
	
	public ArrayList<StaticMethod> findMissingMethods(String methodSignature, int missingLine) {
		ArrayList<StaticMethod> result = new ArrayList<StaticMethod>();
		StaticMethod m = testApp.findMethodByFullSignature(methodSignature);
		StaticSmaliStmt s = m.getSmaliStmt(missingLine);
		
		return result;
	}
	
}
