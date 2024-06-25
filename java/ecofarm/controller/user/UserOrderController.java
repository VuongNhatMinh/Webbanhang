package ecofarm.controller.user;

import java.util.ArrayList;
import java.util.Date;
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

import ecofarm.entity.Account;
import ecofarm.entity.Address;
import ecofarm.entity.Cart;
import ecofarm.entity.Category;
import ecofarm.entity.OrderDetail;
import ecofarm.entity.OrderDetailId;
import ecofarm.entity.Orders;
import ecofarm.entity.Product;
import ecofarm.utility.Mailer;
import ecofarm.utility.TimeUtil;
import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.ICartDAO;
import ecofarm.DAO.ICategoryDAO;
import ecofarm.DAO.IOrderDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.bean.Company;

@Controller
@RequestMapping(value = "/order")
public class UserOrderController {
	
	@Autowired
	Mailer mailer;
	
	@Autowired
	private ICartDAO cartDAO;
	@Autowired
	private IAccountDAO accountDAO;
	@Autowired
	private IOrderDAO orderDAO;
	@Autowired
	private IProductDAO productDAO;
	@Autowired
	@Qualifier("ecofarm")
	Company company;

	@RequestMapping(value = "checkout", method=RequestMethod.POST)
	public String detail(ModelMap model,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		if (userEmail != null && !userEmail.isEmpty()) {
			Account account = accountDAO.getAccountByEmail(userEmail);
			List<Cart> cart = cartDAO.getCartByAccountID(account.getAccountId());
			if (cart.size() <= 0) {
				return "redirect:/index.htm";
			}
			model.addAttribute("cart", cart);
			model.addAttribute("user", account);
			return "user/order/checkout";
		}
		return "redirect:/login.htm";
	}

	@RequestMapping(value = "checkout_success.htm", method = RequestMethod.POST)
	public String success(@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			ModelMap model, @RequestParam(value = "paymentMethod", required = true) String paymentMethod, HttpSession ss) {
		if (userEmail != null && !userEmail.isEmpty()) {
			String pm = "";
			Account account = accountDAO.getAccountByEmail(userEmail);
			List<Cart> cart = cartDAO.getCartByAccountID(account.getAccountId());
			if (cart.size() <= 0) {
				return "redirect:/index.htm";
			}
			Orders orders = new Orders();
			Date in = new Date();
			orders.setAccount(account);
			orders.setOrderTime(in);
			orders.setDeliveryTime(TimeUtil.getDelayedTime(1));
			orders.setStatus(0);
			orders.setPrice(cartDAO.getTotalPrice(cart) + 15000);
			if ("cod".equals(paymentMethod)) {
				orders.setPaymentMethod("COD");
				pm = "Thanh toán khi nhận hàng";
			} else if ("banking".equals(paymentMethod)) {
				orders.setPaymentMethod("BANKING");
				pm = "Chuyển khoản";
			}
			model.addAttribute("orders", orders);
			if (account.getDefaultAddress() != null)
				orders.setDefaultAddress(account.getDefaultAddress().getFullAddress());

			orderDAO.insertOrder(orders);

			for (Cart c : cart) {
				OrderDetail orderDetail = new OrderDetail();
				orderDetail.setId(new OrderDetailId(orders.getOrderId(), c.getProduct().getProductId()));
				orderDetail.setProduct(c.getProduct());
				orderDetail.setOrder(orders);
				orderDetail.setQuantity(c.getQuantity());
				orderDetail.setPrice(c.getProduct().getPrice());
				// Xoa so luong ton sp
				Product product = orderDetail.getProduct();
				product.setQuantity(product.getQuantity() - orderDetail.getQuantity());
				productDAO.updateProduct(product);
				orderDAO.insertOrderDetail(orderDetail);
			}
			cartDAO.removeAllProductinCart(account.getAccountId());
			//System.out.println("Đơn hàng số: " + orders.getOrderId());
			mailer.sendOrder(userEmail, orders.getOrderId(), orders.getOrderTime(), (int) Math.floor(orders.getPrice()) , pm);
			model.addAttribute("orders", orders);
			ss.setAttribute("carts", new ArrayList<Cart>());
			return "user/order/success";
		}
		return "redirect:/login.htm";
	}
	@RequestMapping(value = "checkout_banking.htm", method = RequestMethod.POST)
	public String bankingPayment(ModelMap model, @CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		if (userEmail != null && !userEmail.isEmpty()) {
			Account account = accountDAO.getAccountByEmail(userEmail);
			List<Cart> cart = cartDAO.getCartByAccountID(account.getAccountId());
			if (cart.size() <= 0) {
				return "redirect:/index.htm";
			}
			model.addAttribute("cart", cart);
			model.addAttribute("orderID", orderDAO.getLastestOrderID() + 1);
			model.addAttribute("total", cartDAO.getTotalPrice(cart));
			return "user/order/banking";		
		}
		return "redirect:/login.htm";
	}
	
	@RequestMapping(value = "cancelRequest", method = RequestMethod.POST)
	public String cancelRequest(HttpSession session, HttpServletRequest request,
			@RequestParam(value = "orderId") int orderId) {
		Orders orders = orderDAO.findOrder(orderId);
		for (OrderDetail d : orders.getOrderDetails()) {
			Product product = d.getProduct();
			product.setQuantity(product.getQuantity() + d.getQuantity());
			productDAO.updateProduct(product);
		}
		orders.setStatus(3);
		orderDAO.update(orders);
		return "redirect:" + request.getHeader("Referer");
	}
}
