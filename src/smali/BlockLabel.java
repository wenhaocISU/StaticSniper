package smali;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class BlockLabel implements Serializable{

	private String generalLabel;
	private String gotoLabel;
	private String condLabel;
	private String pswitchLabel;
	private String sswitchLabel;
	private String tryLabel;
	private String catchLabel;
	private String catchAllLabel;
	private int blockNumber;
	
	public BlockLabel() {
		this.setGeneralLabel("");
		this.setCondLabel("");
		this.setGotoLabel("");
		this.setPswitchLabel("");
		this.setSswitchLabel("");
		this.setTryLabel("");
		this.setCatchLabel("");
		this.setCatchAllLabel("");
	}
	
	public String getGotoLabel() {
		return gotoLabel;
	}
	
	public void setGotoLabel(String gotoLabel) {
		this.gotoLabel = gotoLabel;
	}
	
	public String getCondLabel() {
		return condLabel;
	}
	
	public void setCondLabel(String condLabel) {
		this.condLabel = condLabel;
	}
	
	public String getPswitchLabel() {
		return pswitchLabel;
	}
	
	public void setPswitchLabel(String pswitchLabel) {
		this.pswitchLabel = pswitchLabel;
	}
	
	public String getSswitchLabel() {
		return sswitchLabel;
	}
	
	public void setSswitchLabel(String sswitchLabel) {
		this.sswitchLabel = sswitchLabel;
	}
	
	public String getTryLabel() {
		return tryLabel;
	}
	
	public void setTryLabel(String tryLabel) {
		this.tryLabel = tryLabel;
	}

	public String getGeneralLabel() {
		return generalLabel;
	}

	public void setGeneralLabel(String normalLabel) {
		this.generalLabel = normalLabel;
	}

	public String getCatchLabel() {
		return catchLabel;
	}

	public void setCatchLabel(String catchLabel) {
		this.catchLabel = catchLabel;
	}

	public String getCatchAllLabel() {
		return catchAllLabel;
	}

	public void setCatchAllLabel(String catchAllLabel) {
		this.catchAllLabel = catchAllLabel;
	}
	
	public List<String> toStringList() {
		return Arrays.asList(
				this.generalLabel, this.condLabel, this.gotoLabel,
				this.pswitchLabel, this.sswitchLabel,
				this.tryLabel, this.catchAllLabel, this.catchLabel);
	}
	
}
