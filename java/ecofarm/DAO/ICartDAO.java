package ecofarm.DAO;

import java.util.List;

import ecofarm.entity.Cart;

public interface ICartDAO {
	public List<Cart> getCartByAccountID(int accountID);
	public boolean addToCart(int productID, int accountID);
	public boolean addToCart(int productID, int accountID,int quantity);
	public boolean editCart(int productID, int accountID,int quantity);
	public boolean deleteCart(int productID, int accountID);
	public float getTotalPrice(List<Cart> cart);
	public boolean removeAllProductinCart(int accountID);
}
