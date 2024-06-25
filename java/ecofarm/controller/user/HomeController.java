package ecofarm.controller.user;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.ICartDAO;
import ecofarm.DAO.ICategoryDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.DAO.IWishlistDAO;
import ecofarm.bean.Company;
import ecofarm.entity.Account;
import ecofarm.entity.Cart;
import ecofarm.entity.Wishlist;


@Controller
public class HomeController {
	@Autowired
	private ICategoryDAO categoryDAO;
	@Autowired
	private IProductDAO productDAO;
	@Autowired
	private IAccountDAO accountDAO;
	@Autowired
	private ICartDAO cartDAO;
	@Autowired
	private IWishlistDAO wishlistDAO;
	@Autowired
	@Qualifier("ecofarm")
	Company company;
	
	@RequestMapping(value={"/index"},method=RequestMethod.GET)
	public String Index(@CookieValue(value = "userEmail",defaultValue = "",required = false) String userEmail, 
			HttpServletRequest request,HttpSession session,HttpServletResponse response) {
		request.setAttribute("company", company);
		if(!userEmail.equals("")) {
			Account account =accountDAO.getAccountByEmail(userEmail);
			session.setAttribute("userInfo", account);
			if(account!=null) {
				List<Cart> list = cartDAO.getCartByAccountID(account.getAccountId());
				session.setAttribute("carts", list);
				List<Wishlist> wishlist = wishlistDAO.getWishlistByAccountID(account.getAccountId());
				session.setAttribute("wishlist", wishlist);
			}
		}else {
			Account account =(Account)session.getAttribute("userInfo");
			if(account!=null) {
				Cookie cookie = new Cookie("userEmail", account.getEmail());
				cookie.setMaxAge(-1);
				response.addCookie(cookie);
				List<Cart> list = cartDAO.getCartByAccountID(account.getAccountId());
				session.setAttribute("carts", list);
				//Tính số lượng wishlist khi đăng nhập
				List<Wishlist> wishlist = wishlistDAO.getWishlistByAccountID(account.getAccountId());
				session.setAttribute("wishlist", wishlist);
				}
		}
		
		request.setAttribute("categories", categoryDAO.getAllCategories());
		request.setAttribute("latestProducts",productDAO.getLatestProduct());
		request.setAttribute("products",productDAO.getAllProducts());
		request.setAttribute("reviewProducts", productDAO.getReviewProduct());
		request.setAttribute("ratedProducts", productDAO.getRatedProduct());
		return "user/index";
	}
}
