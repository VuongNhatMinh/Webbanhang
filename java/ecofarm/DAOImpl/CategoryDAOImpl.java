package ecofarm.DAOImpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import ecofarm.DAO.ICategoryDAO;
import ecofarm.entity.Category;

@Transactional
public class CategoryDAOImpl implements ICategoryDAO {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getAllCategories() {
		List<Category> list = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "from Category";
			Query query = session.createQuery(hql);
			list = query.list();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean deleteCategory(Category category) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.delete(category);
			t.commit();
			return true;
		} catch (Exception e) {
			System.out.println("Delete category failed: " + e.getMessage());
			t.rollback();
		} finally {
			ss.close();
		}
		return false;
	}

	@Override
	public boolean addCategory(Category newCategory) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.save(newCategory);
			t.commit();
			return true;
		} catch (Exception e) {
			System.out.println("Add category failed: " + e.getMessage());
			t.rollback();
		} finally {
			ss.close();
		}
		return false;
	}

	@Override
	public List<Category> getListCategoriesHasProduct() {
		try {
			Session session = sessionFactory.getCurrentSession();

			String hql = "FROM Category WHERE SIZE(products) > 0";
			Query query = session.createQuery(hql);
			@SuppressWarnings("unchecked")
			List<Category> list = query.list();

			return list;
		} catch (Exception e) {
			throw e;
		}

	}

	@Override
	public Category getCategory(int id) {
		String hql = "FROM Category WHERE id = :id";
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(hql);
		query.setParameter("id", id);
		Category category = null;
		try {
			category = (Category) query.uniqueResult();

		} catch (Exception e) {
			System.out.println(e);
		}
		return category;
	}

	@Override
	public boolean updateCategory(Category category) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.update(category);
			t.commit();
			return true;
		} catch (Exception e) {
			System.out.println("Update category failed: " + e.getMessage());
			t.rollback();
		} finally {
			ss.close();
		}
		return false;
	}

	@Override
	public List<Category> searchCategory(String alikeName) {
		Session ss = sessionFactory.getCurrentSession();
		alikeName = (alikeName == null) ? "%" : "%" + alikeName + "%";
		String hql = "FROM Category WHERE name LIKE :name";
		Query query = ss.createQuery(hql);
		query.setParameter("name", alikeName);
		@SuppressWarnings("unchecked")
		List<Category> list = query.list();
		return list;
	}

	@Override
	public Category fetchCategory(Category category) {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		Category fc = null;

		try {
			fc = (Category) session.get(Category.class, category.getCategoryId());
			Hibernate.initialize(fc.getProducts());
			t.commit();

		} catch (Exception e) {
			t.rollback();
			System.out.println("Fetch Category occur error");
			System.out.println(e);
		} finally {
			session.close();
		}

		return fc == null ? category : fc;
	}
}
