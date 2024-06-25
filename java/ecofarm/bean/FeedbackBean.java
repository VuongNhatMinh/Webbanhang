package ecofarm.bean;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

public class FeedbackBean {
	
	private int ratingStar;
	@NotBlank(message = "Vui lòng nhập đánh giá chi tiết")
	private String feedbackContent;
	private int status;
	private int accountID;
	private int productID;
	private Date postingDate;
	public int getRatingStar() {
		return ratingStar;
	}
	public void setRatingStar(int ratingStar) {
		this.ratingStar = ratingStar;
	}
	public String getFeedbackContent() {
		return feedbackContent;
	}
	public void setFeedbackContent(String feedbackContent) {
		this.feedbackContent = feedbackContent;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getAccountID() {
		return accountID;
	}
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	public int getProductID() {
		return productID;
	}
	public void setProductID(int productID) {
		this.productID = productID;
	}
	public Date getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}
	
}
