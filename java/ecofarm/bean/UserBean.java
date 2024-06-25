package ecofarm.bean;


import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class UserBean {
	
	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;

	private String password;

	@NotBlank(message = "Tên không được để trống")
	private String firstName;
	
	@NotBlank(message = "Họ không được để trống")
	private String lastName;
	
	@Pattern(regexp = "^0\\d{9}$|^$", message = "Số điện thoại phải theo dạng: 0xxxxxxxxx")
	private String phoneNumber;

	private MultipartFile avatar;

	private String avatarDir;

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public MultipartFile getAvatar() {
		return avatar;
	}

	public void setAvatar(MultipartFile avatar) {
		this.avatar = avatar;
	}

	public String getAvatarDir() {
		return avatarDir;
	}

	public void setAvatarDir(String avatarDir) {
		this.avatarDir = avatarDir;
	}

	public UserBean(String email, String password, String firstName, String lastName, String phoneNumber,
			MultipartFile avatar, String avatarDir) {
		super();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.avatar = avatar;
		this.avatarDir = avatarDir;
	}

	public UserBean() {
		email = "";
		password = "";
		lastName = "";
		firstName = "";
		phoneNumber = "";
		avatarDir = "";
	}
	
	public UserBean(String email, String firstName, String lastName, String phoneNumber) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
	}
	
	public UserBean(String email, String firstName, String lastName, String phoneNumber, String avatar) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.avatarDir = avatar;
	}
}
