package staticFamily;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class StaticField implements Serializable {

	private String declaration;
	private String name;
	private String type;
	private int modifiers;
	private StaticClass declaringClass;
	private ArrayList<String> inCallSourceSigs = new ArrayList<String>();

	public StaticField(String name, String type, int modifiers,
			String declaration) {
		this.name = name;
		this.type = type;
		this.modifiers = modifiers;
		this.declaration = declaration;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getSubSignature() {
		return type + " " + name;
	}

	public String getFullSignature() {
		return declaringClass.getName() + ": " + getSubSignature();
	}

	public int getModifiers() {
		return modifiers;
	}

	public String getDeclaration() {
		return declaration;
	}

	public String getDeclaringClassName() {
		return declaringClass.getName();
	}

	public ArrayList<String> getInCallSourceSigs() {
		return inCallSourceSigs;
	}

	public void addInCallSource(String sourceSig) {
		inCallSourceSigs.add(sourceSig);
	}

	public void setDeclaringClass(StaticClass declaringClass) {
		this.declaringClass = declaringClass;
	}
}
