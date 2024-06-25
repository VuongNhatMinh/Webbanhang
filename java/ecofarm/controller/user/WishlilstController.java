package ecofarm.controller.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.IWishlistDAO;
import ecofarm.bean.Company;
import ecofarm.entity.Account;
import ecofarm.entity.Wishlist;

@Controller
public class WishlilstController {
	@Autowired
	private IAccountDAO accountDAO;
	@Autowired
	private IWishlistDAO wishlistDAO;
	@Autowired
	@Qualifier("ecofarm")
	Company company;

	@RequestMapping("/wishlist")
	public String Index(HttpServletRequest request, HttpSession session,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		if (!userEmail.equals("")) {
			Account account = accountDAO.getAccountByEmail(userEmail);
			if(account!=null) {
			List<Wishlist> list = wishlistDAO.getWishlistByAccountID(account.getAccountId());
			session.setAttribute("wishlist", list);
			return "user/wishlist";
			}
		}
		return "redirect:/login.htm";
	}

	@RequestMapping(value= "/AddWishlist",method = RequestMethod.POST)
	public String AddToWishlist(@RequestParam(value = "productId", required = true) int productId,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, HttpServletRequest request) {
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/login.htm";
		}
		Account account = accountDAO.getAccountByEmail(userEmail);
		wishlistDAO.addToWishlist(productId, account.getAccountId());
		List<Wishlist> wishlist = wishlistDAO.getWishlistByAccountID(account.getAccountId());
		session.setAttribute("wishlist", wishlist);
		return "redirect:" + request.getHeader("Referer");
	}


	@RequestMapping(value = "/DeleteWishlist",method = RequestMethod.POST)
	public String DeleteFromWishlist(@RequestParam(value = "productId", required = true) int productId,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, HttpServletRequest request) {
		Account account = accountDAO.getAccountByEmail(userEmail);
		wishlistDAO.deleteFromWishlist(productId, account.getAccountId());
		return "redirect:" + request.getHeader("Referer");
	}
}
