package ecofarm.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class UserInterceptor extends HandlerInterceptorAdapter {
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
		Cookie[] cookies = request.getCookies();
		boolean isLogined = false;
		if(cookies != null) {
			for(Cookie cookie: cookies) {
				if("userEmail".equals(cookie.getName())) {
					isLogined = true;
				}
			}
		}
		//Nếu chưa đăng nhập thì yêu cầu người dùng vào trang đăng nhập
		if(!isLogined) {
			response.sendRedirect(request.getContextPath() + "/login.htm");
			return false;
		}else {
			return true;
		}
	}
}
