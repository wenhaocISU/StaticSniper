package smali;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class BlockLabel implements Serializable{

	private String normalLabel;
	private String tryLabel;
	private int normalLabelCount;
	
	public BlockLabel() {

	}
	

	public String getTryLabel() {
		return tryLabel;
	}
	
	public void setTryLabel(String tryLabel) {
		this.tryLabel = tryLabel;
	}


}
