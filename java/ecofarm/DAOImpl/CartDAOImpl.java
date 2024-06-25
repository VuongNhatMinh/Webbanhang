package ecofarm.DAOImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.ICartDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.entity.Account;
import ecofarm.entity.Cart;
import ecofarm.entity.CartId;
import ecofarm.entity.Product;

@Transactional
public class CartDAOImpl implements ICartDAO {
	@Autowired
	private IProductDAO productDAO;
	@Autowired
	private IAccountDAO accountDAO;

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Cart> getCartByAccountID(int accountID) {
		List<Cart> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {

			String hql = "FROM Cart WHERE account.accountId = :accountID";
			Query query = session.createQuery(hql);
			query.setParameter("accountID", accountID);
			list = query.list();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean addToCart(int productID, int accountID) {
		boolean isAdded = false;
		Cart cart = new Cart();
		CartId cartId = new CartId();
		cartId.setAccountId(accountID);
		cartId.setProductId(productID);

		Product product = productDAO.getProductByID(productID);
		Account account = accountDAO.getAccountByID(accountID);

		Cart checkCart = getCartByID(productID, accountID);
		if (product != null) {
			cart.setAccount(account);
			cart.setProduct(product);
			cart.setId(cartId);
			if (checkCart == null) {
				cart.setQuantity(1);
				if(cart.getQuantity() > product.getQuantity()) {
					return false;
				}
				Session session = sessionFactory.openSession();
				try {
					Transaction tr = session.beginTransaction();
					session.save(cart);
					tr.commit();
					isAdded = true;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
					e.printStackTrace();
				} finally {
					session.close();
				}
			} else {
				cart.setQuantity(checkCart.getQuantity() + 1);
				if(cart.getQuantity() > product.getQuantity()) {
					return false;
				}
				Session session = sessionFactory.openSession();
				try {
					Transaction tr = session.beginTransaction();
					session.update(cart);
					tr.commit();
					isAdded = true;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
					e.printStackTrace();
				} finally {
					session.close();
				}
			}
		}
		return isAdded;
	}

	@SuppressWarnings("unchecked")
	private Cart getCartByID(int productID, int accountID) {
		List<Cart> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {

			String hql = "FROM Cart WHERE account.accountId =:accountID AND product.productId =:productID";
			Query query = session.createQuery(hql);
			query.setParameter("accountID", accountID);
			query.setParameter("productID", productID);
			list = query.list();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	@Override
	public boolean editCart(int productID, int accountID, int quantity) {
		boolean isEdit = false;
		Cart checkCart = getCartByID(productID, accountID);
		Product product = productDAO.getProductByID(productID);
		if (checkCart != null) {
			if(checkCart.getQuantity() > product.getQuantity()) {
				return false;
			}
			Session session = sessionFactory.openSession();
			try {
				Transaction tr = session.beginTransaction();
				checkCart.setQuantity(quantity);
				session.update(checkCart);
				tr.commit();
				isEdit = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		return isEdit;
	}

	@Override
	public boolean deleteCart(int productID, int accountID) {
		boolean isDeleted = false;
		Cart checkCart = getCartByID(productID, accountID);
		if (checkCart != null) {
			Session session = sessionFactory.openSession();
			try {
				Transaction tr = session.beginTransaction();
				session.delete(checkCart);
				tr.commit();
				isDeleted = true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		return isDeleted;
	}

	@Override
	public float getTotalPrice(List<Cart> cart) {
		float total = 0;
		if (cart.size() > 0) {
			for (Cart item : cart) {
				total += item.getQuantity() * item.getProduct().getPrice();
			}
			return total;
		}
		return total;
	}

	@Override
	public boolean addToCart(int productID, int accountID, int quantity) {
		boolean isAdded = false;
		Cart cart = new Cart();
		CartId cartId = new CartId();
		cartId.setAccountId(accountID);
		cartId.setProductId(productID);

		Product product = productDAO.getProductByID(productID);
		Account account = accountDAO.getAccountByID(accountID);

		Cart checkCart = getCartByID(productID, accountID);

		if (product != null) {
			cart.setAccount(account);
			cart.setProduct(product);
			cart.setId(cartId);
			if (checkCart == null) {
				cart.setQuantity(quantity);
				if(cart.getQuantity() > product.getQuantity()) {
					return false;
				}
				Session session = sessionFactory.openSession();
				try {
					Transaction tr = session.beginTransaction();
					session.save(cart);
					tr.commit();
					isAdded = true;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
					e.printStackTrace();
				} finally {
					session.close();
				}
			} else {
				cart.setQuantity(checkCart.getQuantity() + quantity);
				if(cart.getQuantity() > product.getQuantity()) {
					return false;
				}
				Session session = sessionFactory.openSession();
				try {
					Transaction tr = session.beginTransaction();
					session.update(cart);
					tr.commit();
					isAdded = true;
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.getMessage());
					e.printStackTrace();
				} finally {
					session.close();
				}
			}
		}
		return isAdded;
	}

	@Override
	public boolean removeAllProductinCart(int accountID) {
		Session session = sessionFactory.openSession();
		Transaction t = null;
		try {
			t = session.beginTransaction();

			String hql = "DELETE FROM Cart c WHERE c.account.accountId = :account";
			int deletedCount = session.createQuery(hql).setParameter("account", accountID).executeUpdate();
			t.commit();
			return deletedCount > 0;

		} catch (Exception e) {
			if (t != null) {
				t.rollback();
			}
			System.out.print(e.getMessage());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return false;
	}

}
