package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class StaticField implements Serializable {

	private String declaration;
	private String fullJimpleSignature;
	private int modifiers;
	private String declaringClassName;
	private ArrayList<String> inCallSourceSigs = new ArrayList<String>();

	public StaticField(String fullSig) {
		this.fullJimpleSignature = fullSig;
		this.modifiers = -1;
		this.declaration = "";
	}

	//////////// read attributes
	public String getName() {
		return getSubJimpleSignature().split(" ")[1];
	}

	public String getType() {
		return getSubJimpleSignature().split(" ")[0];
	}

	public String getSubJimpleSignature() {
		return fullJimpleSignature.substring(fullJimpleSignature.indexOf(": ")+2, fullJimpleSignature.length()-1);
	}

	public String getFullJimpleSignature() {
		return fullJimpleSignature;
	}

	public int getModifiers() {
		return modifiers;
	}

	public String getDeclaration() {
		return declaration;
	}

	public String getDeclaringClassName() {
		return declaringClassName;
	}

	public StaticClass getDeclaringClass(StaticApp testApp) {
		return testApp.findClassByName(declaringClassName);
	}

	public ArrayList<String> getInCallSourceSigs() {
		return inCallSourceSigs;
	}

	//////////// add/set attributes
	
	public void addInCallSource(String sourceSig) {
		inCallSourceSigs.add(sourceSig);
	}
	
	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}
	
	public void setDeclaringClass(String declaringClassName) {
		this.declaringClassName = declaringClassName;
	}
	
	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}
	

}
