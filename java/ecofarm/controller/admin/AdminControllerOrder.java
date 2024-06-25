package ecofarm.controller.admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.IOrderDAO;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.entity.OrderDetail;
import ecofarm.entity.Orders;
import ecofarm.utility.Paginate;

@Controller
@RequestMapping("admin/orders")
public class AdminControllerOrder {
	private final int ORDER_PER_PAGE = 10;
	@Autowired
	private IOrderDAO orderDAO;
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();

	@RequestMapping()
	public String index(@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
			@RequestParam(value = "filter", required = false, defaultValue = "0") int filter, ModelMap model) {
		List<Orders> orders = null;
		switch (filter) {
		case 1:
			orders = orderDAO.getUnresolveOrders();
			break;
		case 2:
			orders = orderDAO.getMovingOrders();
			break;
		case 3:
			orders = orderDAO.getResolveOrders();
			break;
		case 4:
			orders = orderDAO.getCancelOrders();
			break;
		default:
			orders = orderDAO.getOrders();
			break;
		}

		// Tính toán tổng số lượng dựa trên danh sách
		int totalCategories = orders.size();
		// Lấy thông tin phân trang
		Paginate paginate = paginateDAO.getInfoPaginate(totalCategories, ORDER_PER_PAGE, crrPage);

		// Lấy danh sách cho trang hiện tại
		List<Orders> os = orders.subList(paginate.getStart(), paginate.getEnd());

		model.addAttribute("paginate", paginate);
		model.addAttribute("orders", os);
		model.addAttribute("filter", filter);

		return "admin/order/order-list";
	}

	@RequestMapping(value = "update-order", method=RequestMethod.POST)
	public String updateOrder(@RequestParam("id") int id, @RequestParam("status") String status) {
		Orders order = orderDAO.findOrder(id);

		if (order != null) {
			short shortValue = Short.parseShort(status);
			order.setStatus(shortValue);
			orderDAO.update(order);

		}
		return "redirect:/admin/orders.htm";
	}

	@RequestMapping(value = "order-detail")
	public String orderDetail(@RequestParam("orderId") int orderId, ModelMap model) {
		Orders order = orderDAO.findOrder(orderId);
		List<OrderDetail> orderDetail = orderDAO.getOrderDetail(orderId);

		model.addAttribute("order", order);
		model.addAttribute("orderDetail", orderDetail);

		return "admin/order/order-detail";
	}

}
