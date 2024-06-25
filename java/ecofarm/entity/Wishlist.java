package ecofarm.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
//@Table(name = "Wishlist", schema = "dbo", catalog = "DB_Webns")
@Table(name = "Wishlist", schema = "dbo", catalog = "DB_Webnongsan")
public class Wishlist {
	@EmbeddedId
	private WishlistId id;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("accountId")
	@JoinColumn(name = "AccountID", nullable = false, insertable = false, updatable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("productId")
	@JoinColumn(name = "ProductID", nullable = false, insertable = false, updatable = false)
	private Product product;

	public Wishlist() {
	}

	public Wishlist(WishlistId id, Account account, Product product) {
		this.id = id;
		this.account = account;
		this.product = product;

	}

	public WishlistId getId() {
		return this.id;
	}

	public void setId(WishlistId id) {
		this.id = id;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
