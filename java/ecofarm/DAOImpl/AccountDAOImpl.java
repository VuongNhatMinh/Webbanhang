package ecofarm.DAOImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ecofarm.DAO.IAccountDAO;
import ecofarm.entity.Account;
import ecofarm.entity.Role;



@Transactional
public class AccountDAOImpl implements IAccountDAO {
	private static final Logger logger = LoggerFactory.getLogger(IAccountDAO.class);
	
	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	@Override
	public boolean createAccount(Account account) {
		Session session = sessionFactory.openSession();
		account.setPassword(BCrypt.hashpw(account.getPassword(), BCrypt.gensalt(12)));
		boolean isCreated = false;
		Transaction tr = session.beginTransaction();
		try {
			session.save(account);
			tr.commit();
			isCreated = true;
		} catch (Exception e) {
			tr.rollback();
			logger.error("Error creating account: " + e.getMessage(), e);
			throw new RuntimeException("Error creating account", e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close(); 
			}
		}
		return isCreated;
	}

	@Override
	public Account getAccountByEmail(String email) {
	    Session session = sessionFactory.getCurrentSession();
	    try {
	        String hql = "FROM Account WHERE email = :email";
	        Query query = session.createQuery(hql);
	        query.setParameter("email", email);
	        Account account = (Account) query.uniqueResult(); 
	        return account; 
	    } catch (Exception e) {
	        logger.error("Error getting account by email: " + e.getMessage(), e);
	        throw new RuntimeException("Error getting account by email", e);
	    }
	}


	@Override
	public boolean checkAccountRegister(Account account) {
		List<Account> allAccounts = getAllAccounts();
		if (account.getEmail() == null)
			return false;
		if (account.getFirstName() == null)
			return false;
		if (account.getLastName() == null)
			return false;
		if (account.getPhoneNumber() == null)
			return false;

		for (Account el : allAccounts) {
			if (account.getEmail().equals(el.getEmail()))
				return false;
			if (account.getPhoneNumber().equals(el.getPhoneNumber()))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<Account> getAllAccounts() {
		Session session = sessionFactory.getCurrentSession();
		List<Account> list = new ArrayList<>();
		try {
			String hql = "FROM Account";
			list = session.createQuery(hql).list();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean checkAccountLogin(Account account) {
		String pass = account.getPassword();
		Account user = getAccountByEmail(account.getEmail());
		if (user != null) {
			if (BCrypt.checkpw(pass, user.getPassword()))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Account getAccountByID(int accountID) {
		List<Account> accounts = new ArrayList<>();
		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "FROM Account WHERE accountId =:accountID";
			Query query = session.createQuery(hql);
			query.setParameter("accountID", accountID);
			accounts = query.list();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		if (accounts.size() > 0) {
			return accounts.get(0);
		}
		return null;

	}

	@Override
	public boolean forgotPassword(String username, String password) {
		Session session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			Account account = getAccountByEmail(username);
			if (account != null) {
				account.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12)));
				session.update(account);
			}
			tr.commit();
			return true;
		} catch (Exception e) {
			tr.rollback();
			System.out.println(e.getMessage());
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Account> listAccountWithRole(EnumRole roleID, String search) {
		Session session = sessionFactory.getCurrentSession();
		List<Account> list = null;
		try {
			String hql = "FROM Account WHERE role.roleId = :roleID";
			if (search != null && !search.isEmpty()) {
				hql += " AND (CONCAT(lastName, ' ', firstName) LIKE :search OR email LIKE :search)";
			}
			Query query = session.createQuery(hql);
			query.setParameter("roleID", roleID.toString());
			if (search != null && !search.isEmpty()) {
				query.setParameter("search", "%" + search + "%");
			}
			list = query.list();
		} catch (HibernateException e) {
			e.printStackTrace(); // Xử lý hoặc ghi log lỗi
		}
		return list;
	}

	@Override
	public List<Account> listAccounts() {
		Session ss = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Account> list = ss.createQuery("FROM Account").list();
		return list;
	}

	@Override
	public boolean updateAccount(Account account) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.update(account);
			t.commit();
			return true;
		} catch (HibernateException e) {
			System.out.println("Update Account - error: " + e.getMessage());
			t.rollback();
		}

		finally {
			ss.close();
		}
		return false;
	}

	@Override
	public boolean deleteAccount(Account account) {
		Session ss = sessionFactory.openSession();
		Transaction t = ss.beginTransaction();
		try {
			ss.delete(account);
			t.commit();
			return true;
		} catch (Exception e) {
			System.out.println("Delete Account - error: " + e.getMessage());
			t.rollback();
		} finally {
			ss.close();
		}
		return false;
	}

	@Override
	public Role getRoleByEnum(EnumRole role) {

		Role _role = null;

		Session session = sessionFactory.getCurrentSession();
		try {
			String hql = "From Role Where role.roleId = :roleID";
			Query query = session.createQuery(hql);
			query.setString("roleID", role.toString());
			_role = (Role) query.uniqueResult();
			
		} catch (HibernateException e) {
			
			e.printStackTrace(); // Xử lý hoặc ghi log lỗi
		}
		return _role;
	}
	
	@Override
	public boolean isEmailUsedByOtherAccounts(String email, int accountId) {
	    Session session = sessionFactory.getCurrentSession();
	    try {
	        String hql = "SELECT COUNT(*) FROM Account WHERE email = :email AND accountId != :accountId";
	        Query query = session.createQuery(hql);
	        query.setParameter("email", email);
	        query.setParameter("accountId", accountId);
	        Long count = (Long) query.uniqueResult(); 
	        return count > 0; 
	    } catch (Exception e) {
	        logger.error("Error checking if email is used by other accounts: " + e.getMessage(), e);
	        throw new RuntimeException("Error checking if email is used by other accounts", e);
	    }
	}
	@SuppressWarnings("unchecked")
	@Override
	public Account getAccountByPhoneNumber(String phoneNumber) {
		Session session = sessionFactory.getCurrentSession();
		List<Account> list = new ArrayList<Account>();
	    try {
	        String hql = "FROM Account WHERE phoneNumber = :pn";
	        Query query = session.createQuery(hql);
	        query.setParameter("pn", phoneNumber);
	        list = query.list();
	        if(list.size() == 0) return null;
	        else return list.get(0); 
	    } catch (Exception e) {
	        logger.error("Error getting account by phoneNumber: " + e.getMessage(), e);
	        throw new RuntimeException("Error getting account by phoneNumber", e);
	    }
	}


}
