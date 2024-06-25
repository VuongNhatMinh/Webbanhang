package ecofarm.bean;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class ContactBean {
	@NotBlank(message = "Tiêu đề không được bỏ trống")
	@Size(max=100,message = "Tiêu đề không được quá 100 ký tự")
	private String title;
	
	@NotBlank(message = "Email không được bỏ trống")
	@Email(message = "Email không hợp lệ")
	private String emailContact;
	
	@NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải là số và theo dạng: 0xxxxxxxxx")
    private String phone;
	
    @Size(min = 20, message = "Nội dung phải có tối thiểu 20 ký tự")
    private String comments;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmailContact() {
		return emailContact;
	}

	public void setEmailContact(String emailContact) {
		this.emailContact = emailContact;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
