package ecofarm.bean;

public class ChangePassword {

	private String oldPass;
	private String newPass;
	private String confirmPass;

	public ChangePassword() {
		super();
	}

	public ChangePassword(String oldPass, String newPass, String confirmPass) {
		super();
		this.oldPass = oldPass;
		this.newPass = newPass;
		this.confirmPass = confirmPass;
	}

	public String getOldPass() {
		return oldPass;
	}

	public void setOldPass(String oldPass) {
		this.oldPass = oldPass;
	}

	public String getNewPass() {
		return newPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public String getConfirmPass() {
		return confirmPass;
	}

	public void setConfirmPass(String confirmPass) {
		this.confirmPass = confirmPass;
	}

}