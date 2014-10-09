package smali;

public class BlockLabel {

	private String generalLabel;
	private String gotoLabel;
	private String condLabel;
	private String pswitchLabel;
	private String sswitchLabel;
	private String tryLabel;
	
	public BlockLabel() {
		this.setGeneralLabel("main");
		this.setCondLabel("");
		this.setGotoLabel("");
		this.setPswitchLabel("");
		this.setSswitchLabel("");
		this.setTryLabel("");
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

	public String getNormalLabel() {
		return generalLabel;
	}

	public void setGeneralLabel(String normalLabel) {
		this.generalLabel = normalLabel;
	}
	
}
