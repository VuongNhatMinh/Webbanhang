package ecofarm.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CartId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ProductID", nullable = false)
	private int productId;

	@Column(name = "AccountID", nullable = false)
	private int accountId;

	public CartId() {
	}

	public CartId(int productId, int accountId) {
		this.productId = productId;
		this.accountId = accountId;
	}

	public int getProductId() {
		return this.productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getAccountId() {
		return this.accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null) || !(other instanceof CartId))
			return false;
		CartId castOther = (CartId) other;

		return (this.getProductId() == castOther.getProductId()) && (this.getAccountId() == castOther.getAccountId());
	}

}
