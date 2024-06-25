package ecofarm.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "Product", schema = "dbo", catalog = "DB_Webnongsan")
public class Product {

	@Id
	@GeneratedValue
	@Column(name = "ProductID", unique = true, nullable = false)
	private int productId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CategoryID", nullable = false)
	private Category category;

	@Column(name = "ProductName", nullable = false)
	private String productName;

	@Column(name = "Price", nullable = false, scale = 4)
	private double price;

	@Column(name = "Image")
	private String image;

	@Column(name = "Quantity", nullable = false)
	private int quantity;

	@Column(name = "Detail")
	private String detail;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "PostingDate", nullable = false, length = 10)
	private Date postingDate;

	@Column(name = "Unit")
	private String unit;
	
	@Transient
	private double ratingStar;
	@Transient	
	private int reviews;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AccountID", nullable = false)
	private Account account;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	private Set<Cart> carts = new HashSet<>(0);

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
	private Set<OrderDetail> orderDetails = new HashSet<>(0);

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
	private Set<Feedback> feedbacks = new HashSet<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private Set<Wishlist> wishlist = new HashSet<>(0);

	public Product() {
	}

	public Product(int productId, Category category, String productName, double price, int quantity, Date postingDate) {
		this.productId = productId;
		this.category = category;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
		this.postingDate = postingDate;
	}

	public Product(int productId, Category category, String productName, double price, String image, int quantity,
			String detail, Date postingDate, Date expiryDate, Set<Cart> carts, Set<OrderDetail> orderDetails,
//			Set<Coupon> coupons, 
			Set<Feedback> feedbacks) {
		this.productId = productId;
		this.category = category;
		this.productName = productName;
		this.price = price;
		this.image = image;
		this.quantity = quantity;
		this.detail = detail;
		this.postingDate = postingDate;
		this.carts = carts;
		this.orderDetails = orderDetails;
		this.feedbacks = feedbacks;
	}

	public int getProductId() {
		return this.productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return this.price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return this.image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getPostingDate() {
		return this.postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public Set<Cart> getCarts() {
		return this.carts;
	}

	public void setCarts(Set<Cart> carts) {
		this.carts = carts;
	}

	public Set<OrderDetail> getOrderDetails() {
		return this.orderDetails;
	}

	public void setOrderDetails(Set<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Set<Feedback> getFeedbacks() {
		return this.feedbacks;
	}

	public void setFeedbacks(Set<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	/*
	 * public Coupon getCoupon() { return coupon; }
	 * 
	 * public void setCoupon(Coupon coupon) { this.coupon = coupon; }
	 */

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public double getRatingStar() {
		return ratingStar;
	}

	public void setRatingStar(double ratingStar) {
		this.ratingStar = ratingStar;
	}

	public int getReviews() {
		return reviews;
	}

	public void setReviews(int reviews) {
		this.reviews = reviews;
	}

	
}
