package ecofarm.entity;

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

@Entity
//@Table(name = "Account", schema = "dbo", catalog = "DB_Webns")
@Table(name = "Account", schema = "dbo", catalog = "DB_Webnongsan")
public class Account {

	@Id
	@GeneratedValue
	@Column(name = "AccountID", unique = true, nullable = false)
	private int accountId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RoleID", nullable = false)
	private Role role;

	@Column(name = "LastName", nullable = false)
	private String lastName;

	@Column(name = "FirstName", nullable = false)
	private String firstName;

	@Column(name = "Email", nullable = false)
	private String email;

	@Column(name = "PhoneNumber")
	private String phoneNumber;

	@Column(name = "Avatar")
	private String avatar;

	@Column(name = "Status", nullable = false)
	private int status;

	@Column(name = "Password", nullable = false)
	private String password;
	


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private Set<Address> addresses = new HashSet<>(0);

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DefaultAddress")
	private Address defaultAddress;

	public Address getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(Address defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	public Set<Wishlist> getWishlist() {
		return wishlist;
	}

	public void setWishlist(Set<Wishlist> wishlist) {
		this.wishlist = wishlist;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
	private Set<Orders> orders = new HashSet<>(0);

	/*
	 * @OneToMany(fetch = FetchType.LAZY, mappedBy = "account") private Set<Coupon>
	 * coupons = new HashSet<>(0);
	 */

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private Set<Product> products = new HashSet<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private Set<Feedback> feedbacks = new HashSet<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private Set<Cart> carts = new HashSet<>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
	private Set<Wishlist> wishlist = new HashSet<>(0);

	public Account() {

	}

	public Account(Role role, String lastName, String firstName, String email, String phoneNumber, String avatar,
			String password) {
		super();
		this.status = 1;
		this.role = role;
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.avatar = avatar;
		this.password = password;
	}

	@Override
	public String toString() {
		return String.format("Account ID: {0}", accountId);
	}
	
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Address> getAddresses() {

		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}

	public Set<Orders> getOrders() {
		return orders;
	}

	public void setOrders(Set<Orders> orders) {
		this.orders = orders;
	}


	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public Set<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(Set<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public Set<Cart> getCarts() {
		return carts;
	}

	public void setCarts(Set<Cart> carts) {
		this.carts = carts;
	}

	public String getName() {
		return lastName + " " + firstName;
	}
}
