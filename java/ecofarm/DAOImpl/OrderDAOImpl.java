package ecofarm.DAOImpl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import ecofarm.DAO.IOrderDAO;
import ecofarm.entity.Address;
import ecofarm.entity.OrderDetail;
import ecofarm.entity.Orders;

@Transactional
public class OrderDAOImpl implements IOrderDAO {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<Orders> getOrderFromAccount(int accountId) {
		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "FROM Orders WHERE account.accountID = :accountId ORDER BY orderId DESC";
			Query query = session.createQuery(hql);
			query.setParameter("accountId", accountId);
			@SuppressWarnings("unchecked")
			List<Orders> orderList = query.list();
			return orderList;
		} finally {
			session.close();
		}
	}

	@Override
	public List<OrderDetail> getOrderDetail(int orderId) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "FROM OrderDetail WHERE order.orderId = :orderId";
		Query query = session.createQuery(hql);
		query.setParameter("orderId", orderId);
		@SuppressWarnings("unchecked")
		List<OrderDetail> list = query.list();
		return list;

	}

	@Override
	public boolean insertOrder(Orders order) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.save(order);
			t.commit();
			return true;

		} catch (Exception e) {
			System.out.println(e);
			t.rollback();
		} finally {
			ss.close();
		}
		return false;
	}

	@Override
	public boolean insertOrderDetail(OrderDetail orderDetail) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {

			ss.save(orderDetail);
			t.commit();
			return true;

		} catch (Exception e) {
			System.out.println(e.getMessage());
			t.rollback();
		} finally {
			ss.close();

		}
		return false;
	}

	@Override
	public boolean updateOrder(Orders order) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {

			ss.saveOrUpdate(order);
			t.commit();
			return true;

		} catch (Exception e) {
			System.out.println(e);
			t.rollback();
		} finally {
			ss.close();

		}
		return false;
	}

	@Override
	public List<Orders> getUnresolveOrders() {
		String hql = "From Orders where status = 0 ORDER BY orderId DESC";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Orders> listOrders = query.list();
		return listOrders;
	}

	@Override
	public List<Orders> getMovingOrders() {
		String hql = "From Orders where status = 1 ORDER BY orderId DESC";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Orders> listOrders = query.list();
		return listOrders;
	}

	@Override
	public List<Orders> getResolveOrders() {
		String hql = "From Orders where StatusOrder = 2 ORDER BY orderId DESC";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Orders> listOrders = query.list();
		return listOrders;
	}

	@Override
	public List<Orders> getCancelOrders() {
		String hql = "From Orders where StatusOrder = 3 ORDER BY orderId DESC";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Orders> listOrders = query.list();
		return listOrders;
	}

	@Override
	public boolean update(Orders order) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {

			session.update(order);
			t.commit();
			return true;

		} catch (Exception e) {
			System.out.println(e);
			t.rollback();
		} finally {
			session.close();

		}
		return false;
	}

	@Override
	public Orders fetchOrderDetail(Orders order) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Orders tOrder = null;
		try {
			tOrder = (Orders) session.get(Orders.class, order.getOrderId());
			Hibernate.initialize(tOrder.getOrderDetails());
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			System.out.println("Fetch Order occur error");
			e.printStackTrace();
		} finally {
			session.close();
		}
		return tOrder == null ? order : tOrder;
	}

	@Override
	public Orders findOrder(int id) {
		String hql = "FROM Orders WHERE orderId = :id";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("id", id);
		Orders order = null;
		try {
			order = (Orders) query.uniqueResult();
		} catch (Exception e) {
			System.out.println(e);
		}
		return order;
	}

	@Override
	public List<Orders> getOrders() {
		try {
			Session session = sessionFactory.getCurrentSession();
			String hql = "From Orders ORDER BY orderId DESC";
			Query query = session.createQuery(hql);
			@SuppressWarnings("unchecked")
			List<Orders> list = query.list();
			return list;
		} catch (HibernateException ex) {
			throw ex;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Orders> getOrderFromAccountId(int accountId) {
		Session session = sessionFactory.openSession();
		List<Orders> orders = null;
		try {
			String hql = "FROM Orders WHERE AccountID = :accountId ORDER BY orderId DESC";
			Query query = session.createQuery(hql);
			query.setParameter("accountId", accountId);
			orders = query.list();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			session.close();
		}
		return orders;
	}

	@Override
	public int getLastestOrderID() {
		try {
			Session ss = sessionFactory.getCurrentSession();
			Query query = ss.createQuery("SELECT MAX(orderId) FROM Orders");
			int max = (int) query.uniqueResult();
			return max;
		} catch (HibernateException ex) {
			throw ex;
		}
	}
}
