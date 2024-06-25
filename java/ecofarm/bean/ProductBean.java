package ecofarm.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import ecofarm.entity.Product;

public class ProductBean {
	private int productId;
	private int categoryId;
	private String categoryName;
	private String productName;
	private Double price;
	private String image;
	private int quantity;
	private String detail;
	private Date postingDate;
	private String unit;
	private MultipartFile imageFile;

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public ProductBean() {
		productId = 0;
		categoryName = "";
		categoryId = -1;
		productName = "";
		price = 0d;
		image = "";
		quantity = 0;
		detail = "";

		postingDate = new Date();
		unit = "";
	}

	public ProductBean(Product product) {
		productId = product.getProductId();
		categoryId = product.getCategory().getCategoryId();
		categoryName = product.getCategory().getName();
		productName = product.getProductName();
		price = product.getPrice();
		image = product.getImage();
		quantity = product.getQuantity();
		detail = product.getDetail();
		postingDate = product.getPostingDate();
		unit = product.getUnit();
	}

	public static List<ProductBean> convertProductBean(List<Product> list) {
		List<ProductBean> product = new ArrayList<>();
		for (var p : list) {
			ProductBean bean = new ProductBean(p);
			product.add(bean);
		}
		return product;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getPostingDate() {

		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
