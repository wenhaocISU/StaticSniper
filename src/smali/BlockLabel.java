package smali;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class BlockLabel implements Serializable{

	private ArrayList<String> normalLabels;
	private ArrayList<String> tryLabels;
	
	public BlockLabel() {
		this.setNormalLabels(new ArrayList<String>());
		this.setTryLabels(new ArrayList<String>());
	}
	
	public ArrayList<String> getTryLabels() {
		return tryLabels;
	}
	
	public void setTryLabels(ArrayList<String> tryLabels) {
		this.tryLabels = tryLabels;
	}

	public void addTryLabel(String tryLabel) {
		this.tryLabels.add(tryLabel);
	}
	

	public ArrayList<String> getNormalLabels() {
		return normalLabels;
	}


	public void setNormalLabels(ArrayList<String> normalLabels) {
		this.normalLabels = normalLabels;
	}
	
	public void addNormalLabel(String normalLabel) {
		this.normalLabels.add(normalLabel);
	}


	public int getNormalLabelCount() {
		return normalLabels.size();
	}
	

}
