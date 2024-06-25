package ecofarm.bean;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class LoginBean {
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;
	
	@NotBlank(message = "Mật khẩu không được để trống")
	private String password;
	private String isRemember;
	@NotBlank(message = "Mã captcha không được để trống")
	private String captchaCode;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIsRemember() {
		return isRemember;
	}
	public void setIsRemember(String isRemember) {
		this.isRemember = isRemember;
	}
	public String getCaptchaCode() {
		return captchaCode;
	}
	public void setCaptchaCode(String captchaCode) {
		this.captchaCode = captchaCode;
	}
	
	
}
