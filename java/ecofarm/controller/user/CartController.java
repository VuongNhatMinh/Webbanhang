package ecofarm.controller.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.ICartDAO;
import ecofarm.bean.Company;
import ecofarm.entity.Account;
import ecofarm.entity.Cart;
import ecofarm.entity.Product;

@Controller
public class CartController {
	@Autowired
	private IAccountDAO accountDAO;
	@Autowired
	private ICartDAO cartDAO;
	@Autowired
	@Qualifier("ecofarm")
	Company company;

	@RequestMapping("cart")
	public String Index(HttpServletRequest request, HttpSession session,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {

			Account account = accountDAO.getAccountByEmail(userEmail);
			if (account != null) {
				List<Cart> list = cartDAO.getCartByAccountID(account.getAccountId());
				session.setAttribute("carts", list);
				session.setAttribute("totalPrice", cartDAO.getTotalPrice(list));
			}
			return "user/cart";
	}



	@RequestMapping(value = { "/AddCart" }, method = RequestMethod.POST)
	public String AddToCartQuantity(@RequestParam(value = "productId", required = true) int productId,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			@RequestParam(value="quantity",required = false) String quantity, HttpSession session, HttpServletRequest request, ModelMap model) {


		Account account = accountDAO.getAccountByEmail(userEmail);
		if (account != null) {
			if (quantity == null) {
				boolean res = cartDAO.addToCart(productId, account.getAccountId());
				if(res) {
					List<Cart> list = cartDAO.getCartByAccountID(account.getAccountId());
					session.setAttribute("carts", list);
					session.setAttribute("totalPrice", cartDAO.getTotalPrice(list));
					model.addAttribute("message","Thêm sản phẩm thành công");
				}else {
					session.setAttribute("message","Thêm sản phẩm thất bại");
					session.setAttribute("errorStatus", 400);
					return "redirect:error.htm"; 
				}
			} else {
				boolean res = cartDAO.addToCart(productId, account.getAccountId(), Integer.parseInt(quantity));
				if(res) {
					List<Cart> list = cartDAO.getCartByAccountID(account.getAccountId());
					session.setAttribute("carts", list);
					session.setAttribute("totalPrice", cartDAO.getTotalPrice(list));
					model.addAttribute("message","Thêm sản phẩm thành công");
				}else {
					session.setAttribute("message","Thêm sản phẩm thất bại");
					session.setAttribute("errorStatus", 400);
					return "redirect:error.htm"; 
				}
			}

		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/DeleteCart", method = RequestMethod.POST)
	public String DeleteFromCart(@RequestParam(value = "productId", required = true) int productId,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, HttpServletRequest request) {
		Account account = accountDAO.getAccountByEmail(userEmail);
		cartDAO.deleteCart(productId, account.getAccountId());
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping("/EditCart")
	public String EditCartQnt(@RequestParam(value = "productId", required = true) int productId,
			@RequestParam(value = "qty", required = true) int quantity,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, HttpServletRequest request,ModelMap model) {

		Account account = accountDAO.getAccountByEmail(userEmail);
		boolean res = cartDAO.editCart(productId, account.getAccountId(), quantity);
		if(res) {
			model.addAttribute("message","Chỉnh sửa số lượng sản phẩm thành công");
		}else {
			session.setAttribute("message","Chỉnh sửa số lượng sản phẩm không thành công");
			session.setAttribute("errorStatus", 400);
			return "redirect:error.htm"; 
		}
		return "redirect:" + request.getHeader("Referer");
	}
}
