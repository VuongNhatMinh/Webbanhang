package ecofarm.controller.user;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.IAccountDAO;
import ecofarm.bean.Company;
import ecofarm.bean.LoginBean;
import ecofarm.bean.UploadFile;
import ecofarm.bean.UserBean;
import ecofarm.entity.Account;
import ecofarm.entity.Role;
import ecofarm.utility.CaptchaGenerator;
import ecofarm.utility.Mailer;

@Controller
public class UserController {

	@Autowired
	Mailer mailer;

	@Autowired
	@Qualifier("accountImgDir")
	UploadFile baseUploadFile;
	@Autowired
	@Qualifier("ecofarm")
	Company company;
	
	@Autowired
	private IAccountDAO accountDAO;
	private String validateCodeFP = "";
	private String emailValidate = "";
	private String validateCodeRegister = "";
	private String emailValidateRegister = "";
	private String captchaCode = "";
//	@ModelAttribute("userBean")
//    public UserBean getUserBean() {
//        return new UserBean();
//    }

	@RequestMapping(value = { "/register" }, method = RequestMethod.GET)
	public String Register(HttpServletRequest request,ModelMap model) {
//		request.setAttribute("user", new Account());
		model.addAttribute("userBean", new UserBean());
		return "user/login/register";
	}

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public String CreateAccount(@Valid @ModelAttribute("userBean") UserBean userBean, BindingResult bindingResult,
			HttpSession session, HttpServletRequest request,HttpServletResponse response) {
		if(accountDAO.getAccountByEmail(userBean.getEmail()) != null) {
			bindingResult.rejectValue("email","error.userBean", "Email đã tồn tại");
		}
		if(accountDAO.getAccountByPhoneNumber(userBean.getPhoneNumber()) != null) {
			bindingResult.rejectValue("phoneNumber","error.userBean", "Số điện thoại này đã được đăng ký cho một tài khoán khác");
		}

		if (bindingResult.hasErrors()) {
	        request.setAttribute("status", "Đăng ký tài khoản không thành công");
	        return "user/login/register";
	    }
		boolean isAdded = false;
		Account account = new Account();
		account.setFirstName(userBean.getFirstName());
		account.setLastName(userBean.getLastName());
		account.setEmail(userBean.getEmail());
		account.setPassword(userBean.getPassword());
		account.setPhoneNumber(userBean.getPhoneNumber());
		account.setRole(new Role("GUEST", "Guest"));
		account.setStatus(1);
		if (!userBean.getAvatar().isEmpty()) {
			try {
				account.setAvatar(baseUploadFile.uploadImage(userBean.getAvatar()));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		if (accountDAO.checkAccountRegister(account)) {
			
				emailValidateRegister = account.getEmail();
				isAdded = true;
		}
		try {
			if (isAdded) {
				request.setAttribute("status", "Đăng ký tài khoản thành công");
				validateCodeRegister = mailer.send(emailValidateRegister);
				session.setAttribute("account", account);
				return "redirect:/register/validateCode.htm";
			} else {
				request.setAttribute("status", "Đăng ký tài khoản không thành công");
				return "user/login/register";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return "user/login/register";
		}
	}

	@RequestMapping(value = { "/register/validateCode" }, method = RequestMethod.GET)
	public String registerValidateForm() {
		return "user/login/registerValidate";
	}

	@RequestMapping(value = { "/register/validateCode" }, method = RequestMethod.POST)
	public String registerValidate(@RequestParam("validateCodeRegister") String validateCode,
			HttpServletRequest request,@CookieValue(value = "userEmail",defaultValue = "",required = false) String userEmail,HttpSession session ) {
		if (validateCodeRegister.equals(validateCode)) {
			Account account = (Account) session.getAttribute("account");
			accountDAO.createAccount(account);
			session.setAttribute("userInfo", account);
			return "redirect:/index.htm";
		} else {
			request.setAttribute("wrongCode", "Mã sai vui lòng nhập lại");
		}
		return "user/login/registerValidate";
	}


	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String Login(HttpServletRequest request,ModelMap model) {

		captchaCode = CaptchaGenerator.generateCaptchaCode(6);
		request.setAttribute("captcha", captchaCode);
		model.addAttribute("loginBean", new LoginBean());
		return "user/login/login";
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.POST)
	public String Login(@Valid @ModelAttribute("loginBean") LoginBean loginBean, BindingResult bindingResult, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) {
		String resCaptcha = CaptchaGenerator.convertToHtmlSpan(loginBean.getCaptchaCode());
		if(!captchaCode.equals(resCaptcha) && !resCaptcha.equals("")) {
			bindingResult.rejectValue("captchaCode","error.loginBean", "Mã captcha đã nhập sai vui lòng nhập lại");
		}
		if (bindingResult.hasErrors()) {
			request.setAttribute("message", "Đăng nhập thất bại");
			captchaCode = CaptchaGenerator.generateCaptchaCode(6);
			request.setAttribute("captcha", captchaCode);
			return "user/login/login";
		}
		boolean isLogin = false;
		String checkRemember = loginBean.getIsRemember();
		Account account = new Account();
		account.setEmail(loginBean.getEmail());
		account.setPassword(loginBean.getPassword());
		if (accountDAO.checkAccountLogin(account)) {
			if (accountDAO.getAccountByEmail(account.getEmail()).getStatus() == 0) {
				request.setAttribute("message", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ admin để được hỗ trợ");
				captchaCode = CaptchaGenerator.generateCaptchaCode(6);
				request.setAttribute("captcha", captchaCode);
				return "user/login/login";
			}
			isLogin = true;
		}

		if (isLogin) {
			// Lưu thông tin người dùng vào phiên nếu đăng nhập thành công
			Account loggedInUser = accountDAO.getAccountByEmail(account.getEmail());
			session.setAttribute("userInfo", loggedInUser);
			if (checkRemember != null) {
				Cookie cookie = new Cookie("userEmail", loggedInUser.getEmail());
				cookie.setMaxAge(24 * 60 * 60);
				response.addCookie(cookie);
			} else {
				Cookie cookie = new Cookie("userEmail", loggedInUser.getEmail());
				cookie.setMaxAge(-1);
				response.addCookie(cookie);
			}
			request.setAttribute("status", "Đăng nhập tài khoản thành công");
			return "redirect:/index.htm";
		} else {
			captchaCode = CaptchaGenerator.generateCaptchaCode(6);
			request.setAttribute("captcha", captchaCode);
			request.setAttribute("message", "Email hoặc password của bạn không chính xác. Vui lòng kiểm tra lại.");	
			return "user/login/login";
		}
	}

	@RequestMapping(value = { "/logout" }, method = RequestMethod.GET)
	public String Logout(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		Cookie cookie = new Cookie("userEmail", userEmail);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		// Xóa thông tin người dùng khỏi phiên
		session.removeAttribute("userInfo");
		session.invalidate(); // Hủy phiên đăng nhập hiện tại
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public String ForgotPassword(HttpServletRequest request) {
		return "user/forgotPass/username";
	}

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public String Send(ModelMap model, @RequestParam("email") String email, HttpServletResponse response,
			HttpServletRequest request, HttpSession session) {

		try {
			Account account = accountDAO.getAccountByEmail(email);
			if (account != null) {
				validateCodeFP = mailer.send(email);
				emailValidate = email;

				model.addAttribute("messageEmail", "Gửi mail thành công");
				return "redirect:/forgotPassword/validateCode.htm";
			} else {
				request.setAttribute("message", "Email chưa được đăng ký tài khoản! Vui lòng kiểm tra lại");
				return "user/forgotPass/username";
			}
		} catch (Exception e) {
			model.addAttribute("messageEmail", "Gửi mail thất bại");
			return "user/forgotPass/username";
		}
	}

	@RequestMapping(value = "/forgotPassword/validateCode", method = RequestMethod.GET)
	public String validateCodeForm() {
		return "user/forgotPass/validateCode";
	}

	@RequestMapping(value = "/forgotPassword/validateCode", method = RequestMethod.POST)
	public String ValidateCode(HttpServletRequest request, @RequestParam("validateCode") String validateCode) {
		if (validateCode.equals(validateCodeFP)) {
			return "redirect:/forgotPassword/newPassword.htm";
		} else {
			request.setAttribute("wrongCode", "Mã sai vui lòng nhập lại");
		}
		return "user/forgotPass/validateCode";
	}

	@RequestMapping(value = "/forgotPassword/newPassword", method = RequestMethod.GET)
	public String NewPasswordForm(HttpServletResponse response) {
		Cookie cookie = new Cookie("userEmail", emailValidate);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "user/forgotPass/newPassword";
	}

	@RequestMapping(value = "/forgotPassword/newPassword", method = RequestMethod.POST)
	public String NewPassword(HttpServletRequest request, @RequestParam("confirmPass") String confirmPass,
			@RequestParam("newPass") String newPass, HttpServletResponse response, HttpSession session) {

		if (newPass.equals(confirmPass)) {
			accountDAO.forgotPassword(emailValidate, newPass);
			Account account = accountDAO.getAccountByEmail(emailValidate);

			session.setAttribute("userInfo", account);
			return "redirect:/index.htm";
		} else {

			request.setAttribute("message", "New Password và confirm password không giống nhau vui lòng nhập lại");
		}
		return "user/forgotPass/newPassword";
	}
}