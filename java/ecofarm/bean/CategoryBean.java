package ecofarm.bean;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import ecofarm.entity.Category;
import ecofarm.entity.Product;

public class CategoryBean {
	private int id;
	private String name;
	private String image;
	private MultipartFile fileImage;
	private Set<Product> products = new HashSet<>(0);

	public CategoryBean() {
		this.name = "";
		this.image = "";
	}
	
	public CategoryBean(Category category) {
		this.id = category.getCategoryId();
		this.name = category.getName();
		this.image = category.getImage();
	}

	public CategoryBean(int id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}
	public CategoryBean(int id, String name, String image, MultipartFile fileImage, Set<Product> products) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
		this.fileImage = fileImage;
		this.products = products;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public MultipartFile getFileImage() {
		return fileImage;
	}
	public void setFileImage(MultipartFile fileImage) {
		this.fileImage = fileImage;
	}
	public Set<Product> getProducts() {
		return products;
	}
	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	
}
