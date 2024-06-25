package ecofarm.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ecofarm.entity.Account;
import ecofarm.entity.Orders;

public class FilterUtil {
	public static List<Orders> filterOrdersInCurrentMonth(List<Orders> orders) {
		Calendar calendar = Calendar.getInstance();

		// Lấy ngày đầu tháng
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date startDate = calendar.getTime();

		// Lấy ngày cuối tháng
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date endDate = calendar.getTime();

		return orders.stream()
				.filter(order -> order.getOrderTime().after(startDate) && order.getOrderTime().before(endDate))
				.collect(Collectors.toList());
	}

}
