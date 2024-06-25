package ecofarm.DAOImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import ecofarm.DAO.IFeedbackDAO;
import ecofarm.entity.Feedback;

@Transactional
public class FeedbackDAOImpl implements IFeedbackDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Feedback> getFeedbackByProduct(int productId) {
		List<Feedback> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();

		try {
			String hql = "FROM Feedback WHERE product.productId = :productId and status = 1";
			Query query = session.createQuery(hql);
			query.setParameter("productId", productId);
			list = query.list();
		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		if (list.size() > 0)
			return list;
		return null;
	}

	@Override
	public boolean addFeedback(int productId, int accountId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Feedback> getAllFeedbacks() {
		Session session = sessionFactory.getCurrentSession();
		String hql = "FROM Feedback";
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Feedback> feedback = query.list();
		return feedback;
	}

	@Override
	public List<Feedback> searchFeedback(String search) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		String hql;
		Query query;

		try {
			// Kiểm tra xem search có thể chuyển đổi thành số nguyên hay không
			int feedbackId = Integer.parseInt(search);
			// Nếu thành công, tìm kiếm theo feedbackId
			hql = "FROM Feedback WHERE feedbackId = :id OR feedbackContent LIKE :content";
			query = session.createQuery(hql);
			query.setParameter("id", feedbackId);
			query.setParameter("content", "%" + search + "%");
		} catch (NumberFormatException e) {
			// Nếu không thể chuyển đổi thành số nguyên, tìm kiếm theo feedbackContent
			hql = "FROM Feedback WHERE feedbackContent LIKE :content";
			query = session.createQuery(hql);
			query.setParameter("content", "%" + search + "%");
		}

		@SuppressWarnings("unchecked")
		List<Feedback> list = query.list();
		session.getTransaction().commit();
		session.close();

		return list;
	}

	@Override
	public Feedback getFeedBackById(int fid) {
		Session session = sessionFactory.getCurrentSession();
//		session.beginTransaction();
		String hql = "FROM Feedback WHERE feedbackId LIKE :fid";
		Query query = session.createQuery(hql);
		query.setInteger("fid", fid);
		Feedback feedback = (Feedback) query.uniqueResult();
//		session.getTransaction().commit();
//		session.close();
		return feedback;
	}

	@Override
	public boolean updateFeedback(Feedback feedback) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.update(feedback);
			t.commit();
			return true;
		} catch (Exception e) {
			System.out.println("Update feedback failed: " + e.getCause());
			t.rollback();
		} finally {
			ss.close();
		}
		return false;
	}

	@Override
	public boolean addFeedback(Feedback feedback) {
		Session session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			session.save(feedback);
			tr.commit();
			return true;
		} catch (Exception e) {
			System.out.println("Add feedback failed: " + e.getCause());
			tr.rollback();
		} finally {
			session.close();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Feedback> getFeedbacksByAccout(int accountId) {
		Session session = sessionFactory.getCurrentSession();
		List<Feedback> list = new ArrayList<Feedback>();
		try {
			String hql = "FROM Feedback WHERE account.accountId = :aid";
			Query query = session.createQuery(hql);
			query.setParameter("aid", accountId);
			list = query.list();
		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Feedback getFeedback(int productId, int accountId) {
		Session session = sessionFactory.getCurrentSession();
		List<Feedback> list = new ArrayList<Feedback>();
		try {
			String hql = "FROM Feedback WHERE account.accountId = :aid AND product.productId = :pid";
			Query query = session.createQuery(hql);
			query.setParameter("aid", accountId);
			query.setParameter("pid", productId);
			list = query.list();
			return list.get(0);
		}catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
	}
}
