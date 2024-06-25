package ecofarm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ecofarm.entity.Account;


public class EmployeeInterceptor extends HandlerInterceptorAdapter {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        boolean isLoggedIn = false;
        boolean isEmployee = false;
        HttpSession session = request.getSession();
        Account userInfo = (Account) session.getAttribute("userInfo");
        if(userInfo!= null) {
        	isLoggedIn = true;
        	if (userInfo.getRole().getRoleId().toUpperCase().equals("EMPLOYEE")) {
            	isEmployee = true;	
            }
        }
        // Nếu đã đăng nhập và là Employee, cho phép truy cập
        if (isLoggedIn && isEmployee) {
            return true;
        } else if (isLoggedIn && !isEmployee) {
            // Nếu đã đăng nhập nhưng không phải là Employee, chuyển hướng đến trang chính (home page)
            response.sendRedirect(request.getContextPath() + "/index.htm");
            return false;
        } else {
            // Nếu chưa đăng nhập, chuyển hướng đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login.htm");
            return false;
        }
    }
}


