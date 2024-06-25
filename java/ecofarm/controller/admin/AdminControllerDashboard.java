package ecofarm.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.IAccountDAO.EnumRole;
import ecofarm.DAO.IOrderDAO;
import ecofarm.entity.Account;
import ecofarm.entity.OrderDetail;
import ecofarm.entity.Orders;
import ecofarm.utility.FilterUtil;

@Controller
@RequestMapping(value = "/admin/dashboard")
public class AdminControllerDashboard {
	@Autowired
	private IAccountDAO accountDAO;
	@Autowired
	private IOrderDAO orderDAO;

	@RequestMapping()
	public String index(ModelMap model) {
		double totalEarn = 0;
		double totalAmount = 0;
		List<Orders> orders = orderDAO.getOrders();
		int ordersThisMonth = FilterUtil.filterOrdersInCurrentMonth(orders).size();
		for (Orders o : orders) {
			if (o.getStatus() == 2) {
				totalAmount += o.getPrice();
				for (OrderDetail od : o.getOrderDetails()) {
					totalEarn += od.getQuantity() * od.getPrice();
				}
			}
		}
		double totalFee = totalAmount - totalEarn;
		List<Account> client = accountDAO.listAccountWithRole(EnumRole.GUEST, null);
		model.addAttribute("totalOrder", orders.size());
		model.addAttribute("totalEarning", totalEarn);
		model.addAttribute("totalFee", totalFee);
		model.addAttribute("totalClient", client.size());
		model.addAttribute("ordersThisMonth", ordersThisMonth);

		return "admin/dashboard";

	}
}
