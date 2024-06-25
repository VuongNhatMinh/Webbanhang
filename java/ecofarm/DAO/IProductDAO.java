package ecofarm.DAO;

import java.util.List;

import ecofarm.entity.Product;

public interface IProductDAO {
	public List<Product> getAllProducts();
	public List<Product> getProductsByCategoryID(int categoryID);
	public List<Product> getLatestProductsByCaID(int categoryID);
	public Product getProductByID(int productID);
	
	public List<Product> getLatestProduct();
	public List<Product> getReviewProduct();
	public List<Product> getRatedProduct();
	public List<Product> searchProducts(String likeName);
	public boolean insertProduct(Product product);
	public boolean updateProduct(Product product);
	public boolean deleteProduct(Product product);
	public void setRatingStar(Product product);
	public void setReviews(Product product);
	public Product getFeedbackProduct(int productID, int accountID);
}
