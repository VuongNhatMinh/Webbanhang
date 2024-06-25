package ecofarm.DAO;

import java.util.List;

import ecofarm.entity.Wishlist;

public interface IWishlistDAO {
	public List<Wishlist> getWishlistByAccountID(int accountID);
	public boolean addToWishlist(int productID, int accountID);
	public boolean deleteFromWishlist(int wishlistID, int accountID);
}
