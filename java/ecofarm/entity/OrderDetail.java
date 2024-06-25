package ecofarm.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;


@Entity
//@Table(name = "OrderDetail", schema = "dbo", catalog = "DB_Webns")
@Table(name = "OrderDetail", schema = "dbo", catalog = "DB_Webnongsan")
public class OrderDetail {

	@EmbeddedId
	private OrderDetailId id;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("orderId")
	@JoinColumn(name = "OrderID", nullable = false, insertable = false, updatable = false)
	private Orders order;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("productId")
	@JoinColumn(name = "ProductID", nullable = false, insertable = false, updatable = false)
	private Product product;

	@Column(name = "Quantity", nullable = false)
	private int quantity;
	
	@Column(name = "ProductUnitPrice", nullable = false, scale = 4)
	private Double price;
	
	public OrderDetail(OrderDetailId id, Orders order, Product product, int quantity, double price) {
		this.id = id;
		this.order = order;
		this.product = product;
		this.quantity = quantity;
		this.price = price;
	}

	public Double getPrice() {
		return (price == null)?0.0:price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public OrderDetail() {

	}

	public OrderDetailId getId() {
		return this.id;
	}

	public void setId(OrderDetailId id) {
		this.id = id;
	}

	public Orders getOrder() {
		return this.order;
	}

	public void setOrder(Orders order) {
		this.order = order;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
