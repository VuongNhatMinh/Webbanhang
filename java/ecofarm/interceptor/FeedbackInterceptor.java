package ecofarm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.entity.Account;
import ecofarm.entity.Product;

public class FeedbackInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	IAccountDAO accountDAO;
	@Autowired
	IProductDAO productDAO;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
		Cookie[] cookies = request.getCookies();
		String userEmail = "";
		if (cookies != null) {
			for (Cookie cookie : cookies) {
		        if ("userEmail".equals(cookie.getName())) {
		            userEmail = cookie.getValue();
		            break; // thoát khỏi vòng lặp khi đã tìm thấy cookie
		        }
		    }
		}
		Account account = accountDAO.getAccountByEmail(userEmail);
		// Lấy productId từ URL
	    String productIdStr = request.getParameter("productId");
	    int productId = 0;
	    if (productIdStr != null) {
	        try {
	            productId = Integer.parseInt(productIdStr);
	        } catch (NumberFormatException e) {
	        	HttpSession session = request.getSession();
	        	session.setAttribute("errorStatus", 400);
	        	session.setAttribute("message", "productId không hợp lệ");
	            // Xử lý trường hợp productId không phải là số hợp lệ
	            response.sendRedirect("error.htm");
	            return false;
	        }
	    }else {
	    	HttpSession session = request.getSession();
	    	session.setAttribute("errorStatus", 404);
	    	session.setAttribute("message", "Không có tìm thấy sản phẩm bạn muốn đánh giá");
            // Xử lý trường hợp productId không phải là số hợp lệ
            response.sendRedirect("error.htm");
	    }
	    Product product = productDAO.getFeedbackProduct(productId, account.getAccountId());
	    System.out.println(productId);
	    if(product == null) {
	    	HttpSession session = request.getSession();
	    	session.setAttribute("errorStatus", 401);
	    	session.setAttribute("message", "Bạn không có quyền thêm hoặc chỉnh sửa feedback của sản phẩm này");
            // Xử lý trường hợp productId không phải là số hợp lệ
            response.sendRedirect("error.htm");
	    	return false;
	    }
	    return true;
	}
}
