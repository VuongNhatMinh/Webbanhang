package ecofarm.DAOImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ecofarm.DAO.IFeedbackDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.entity.Feedback;
import ecofarm.entity.Product;

@Transactional
public class ProductDAOImpl implements IProductDAO {
	@Autowired
	private IFeedbackDAO feedbackDAO;
	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getAllProducts() {
		List<Product> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {

			String hql = "FROM Product";
			Query query = session.createQuery(hql);

			list = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getProductsByCategoryID(int categoryID) {
		List<Product> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {

			if (categoryID == 0) {
				String hql = "FROM Product";
				list = session.createQuery(hql).list();
			} else {
				String hql = "FROM Product WHERE category.categoryId = :categoryID";
				Query query = session.createQuery(hql);
				query.setParameter("categoryID", categoryID);
				list = query.list();
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Product getProductByID(int productID) {
		List<Product> products = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "FROM Product WHERE productId = :productID";
			Query query = session.createQuery(hql);
			query.setParameter("productID", productID);
			products = query.list();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (products.size() > 0) {
			setRatingStar(products.get(0));
			setReviews(products.get(0));
			return products.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getLatestProductsByCaID(int categoryID) {
		List<Product> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {

			if (categoryID == 0) {
				String hql = "FROM Product ORDER BY postingDate DESC";
				list = session.createQuery(hql).list();
			} else {
				String hql = "FROM Product WHERE category.categoryId = :categoryID ORDER BY postingDate DESC";
				Query query = session.createQuery(hql);
				query.setParameter("categoryID", categoryID);
				list = query.list();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getLatestProduct() {
		List<Product> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "FROM Product ORDER BY postingDate DESC";
			Query query = session.createQuery(hql);
			query.setMaxResults(6);
			list = query.list();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Product> getReviewProduct() {
		List<Product> list = getAllProducts();
		list.forEach(product -> {
			setReviews(product);
		});
		var reviewComparator = Comparator.comparing(Product::getReviews);
		Collections.sort(list, reviewComparator);
		return list;
	}

	@Override
	public List<Product> getRatedProduct() {
		List<Product> list = getAllProducts();
		list.forEach(product -> {
			setRatingStar(product);
		});
		var ratingComparator = Comparator.comparing(Product::getRatingStar);
		Collections.sort(list, ratingComparator);
		return list;
	}

	@Override
	public List<Product> searchProducts(String likeName) {
		Session ss = sessionFactory.getCurrentSession();
		try {
			likeName = (likeName == null) ? "%" : "%" + likeName + "%";
			String hql = "FROM Product WHERE productName LIKE :name";
			Query query = ss.createQuery(hql);
			query.setParameter("name", likeName);
			@SuppressWarnings("unchecked")
			List<Product> list = query.list();
			return list;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	@Override
	public boolean insertProduct(Product product) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(product);
			t.commit();
			return true;
		} catch (Exception ex) {
			System.out.println(ex);
			t.rollback();
		} finally {
			session.close();
		}
		return false;
	}

	@Override
	public boolean deleteProduct(Product product) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.delete(product);
			t.commit();
			return true;
		} catch (Exception ex) {
			t.rollback();
		} finally {
			session.close();
		}
		return false;
	}

	@Override
	public boolean updateProduct(Product product) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			session.update(product);
			t.commit();
			return true;
		} catch (Exception ex) {
			System.out.println(ex);
			t.rollback();
		} finally {
			session.close();
		}
		return false;
	}

	@Override
	public void setRatingStar(Product product) {
		List<Feedback> feedbacks = feedbackDAO.getFeedbackByProduct(product.getProductId());
		product.setRatingStar(0);

		if (feedbacks != null && !feedbacks.isEmpty()) {
			double[] totalRating = { 0 };
			feedbacks.forEach(feedback -> {
				totalRating[0] += feedback.getRatingStar();
			});
			double averageRating = totalRating[0] / feedbacks.size();
			averageRating = roudRatingStars(averageRating);
			product.setRatingStar(averageRating);
		}

	}

	private double roudRatingStars(double rating) {
		BigDecimal bdDecimal = new BigDecimal(Double.toString(rating));
		bdDecimal = bdDecimal.setScale(1, RoundingMode.HALF_UP);
		return bdDecimal.doubleValue();
	}

	@Override
	public void setReviews(Product product) {
		product.setReviews(0);
		List<Feedback> feedbacks = feedbackDAO.getFeedbackByProduct(product.getProductId());
		if (feedbacks != null) {
			product.setReviews(feedbacks.size());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Product getFeedbackProduct(int productID, int accountID) {
		Session session = sessionFactory.getCurrentSession();
		List<Product> list = new ArrayList<Product>();
		try {
			String hql = "FROM Product WHERE productId IN ("
					+" SELECT product.productId FROM OrderDetail WHERE order.account.accountId = :aid )"
					+" AND productId = :pid";
			Query query = session.createQuery(hql);
			query.setParameter("aid", accountID);
			query.setParameter("pid", productID);
			list = query.list();
			
		} catch (Exception ex) {
			System.out.println(ex);
		}
		if(list.size() > 0) return list.get(0);
		else return null;
	}
}
