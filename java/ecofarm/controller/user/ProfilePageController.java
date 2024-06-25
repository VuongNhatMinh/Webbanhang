package ecofarm.controller.user;

import java.io.File;
import java.util.ArrayList;

import java.util.List;

import javax.persistence.criteria.Order;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.IProfileDAO;
import ecofarm.DAO.IAccountDAO.EnumRole;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.IOrderDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.bean.AddressBean;
import ecofarm.bean.AddressDatasBean;
import ecofarm.bean.AddressDatasBean.DistrictBean;
import ecofarm.bean.AddressDatasBean.ProvinceBean;
import ecofarm.bean.AddressDatasBean.WardBean;
import ecofarm.entity.Account;
import ecofarm.entity.Address;
import ecofarm.entity.OrderDetail;
import ecofarm.entity.Orders;
import ecofarm.entity.Province;
import ecofarm.entity.Role;
import ecofarm.entity.Ward;
import ecofarm.utility.Paginate;
import ecofarm.bean.AddressUserBean;
import ecofarm.bean.ChangePassword;
import ecofarm.bean.Company;
import ecofarm.bean.UploadFile;
import ecofarm.bean.UserBean;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfilePageController {
	@Autowired
	private IAccountDAO accountDAO;
	@Autowired
	private IProfileDAO profileDAO;
	@Autowired
	private IOrderDAO orderDAO;
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();
	@Autowired
	@Qualifier("ecofarm")
	Company company;

	@RequestMapping("/account/ProfilePage")
    public String profilePageIndex(
            @CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
            HttpSession session, ModelMap modelMap, HttpServletRequest request) {
        if (userEmail.equals("")) {
            request.setAttribute("user", new Account());
            return "redirect:/login.htm";
        }

        UserBean accountUser = new UserBean();
        Account account = accountDAO.getAccountByEmail(userEmail);
        accountUser.setFirstName(account.getFirstName());
        accountUser.setLastName(account.getLastName());
        accountUser.setEmail(account.getEmail());
        accountUser.setPhoneNumber(account.getPhoneNumber());
        accountUser.setAvatarDir(account.getAvatar());

        if ((UserBean) modelMap.get("profileInfo") == null) {
            modelMap.addAttribute("profileInfo", accountUser);
        }

        List<Address> allAdress = profileDAO.getAllAddressInfo(account);
        modelMap.addAttribute("allAdress", allAdress);

        List<Province> allProvince = profileDAO.getAllProvince();
        ArrayList<Province> province = (ArrayList<Province>) profileDAO.getAllProvince();
        AddressBean addressBean = new AddressDatasBean().ConvertToDataAddressBean(province);
        modelMap.addAttribute("allProvince", allProvince);
        modelMap.addAttribute("address", addressBean);
        AddressUserBean userAddress = new AddressUserBean();
        modelMap.addAttribute("userAddress", userAddress);

        return "user/account/profilePage";
    }

	@RequestMapping(value = "/account/RemoveDefaultAddress")
	public String removeDefaultAddress(
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpServletRequest request) {
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/login.htm";
		}
		Account account = accountDAO.getAccountByEmail(userEmail);
		profileDAO.removeDefalutAddress(account);
		return "redirect:/account/ProfilePage.htm";

	}

	@Autowired
	@Qualifier("accountImgDir")
	UploadFile accountImgUpload;

	@RequestMapping(value = "account/UploadAvatar", method = RequestMethod.POST)
	public String uploadAvartar(@RequestParam("userAvatar") MultipartFile file,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, HttpServletRequest request, ModelMap model) {
		String photoName = null;
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/login.htm";
		}
		Account account = accountDAO.getAccountByEmail(userEmail);
		try {
			if (!file.isEmpty()) {
				String newImage = accountImgUpload.uploadImage(file);
				System.out.println("Account photo: " + newImage);
				if (account.getAvatar() != null) {
					File oldImage = new File(accountImgUpload.getBasePath() + account.getAvatar());
					if (oldImage.exists()) {
						oldImage.delete();
					}
				}
				// Cập nhật ảnh mới
				account.setAvatar(newImage);
				Thread.sleep(6000);
				accountDAO.updateAccount(account);
				session.removeAttribute("userInfo");
				session.setAttribute("userInfo", account);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "redirect:/account/ProfilePage.htm";
	}

	@RequestMapping("account/DeleteAddress")
	public String deleteAdress(@RequestParam(value = "addressId", required = true) int addressId,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, HttpServletRequest request, ModelMap model) {
		boolean s = profileDAO.deleteAddress(addressId);
		if (s) {
			session.setAttribute("deleteAddressMessage", 1);
		} else {
			session.setAttribute("deleteAddressMessage", 0);
		}

		return "redirect:/account/ProfilePage.htm";

	}

	@RequestMapping("account/ChooseDefaultAddress.htm")
	public String chooseDefaultAddress(@RequestParam(value = "addressId", required = true) int addressId,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		Account account = accountDAO.getAccountByEmail(userEmail);
		profileDAO.chooseDefaultAddress(account, addressId);
		return "redirect:/account/ProfilePage.htm";
	}

	@RequestMapping("/UpdateProfileInfo")
	public String updateProfileInfo(
	@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
	HttpSession session, ModelMap model, HttpServletRequest request,
	@Validated @ModelAttribute("profileInfo") UserBean userBean, BindingResult errors, RedirectAttributes ra,
	HttpServletResponse response) {
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/login.htm";
		}
		
		Account account = accountDAO.getAccountByEmail(userEmail);
		int accountID = account.getAccountId();
		

	    if (errors.hasErrors()) {
	        // Add errors and other attributes to redirectAttributes
	        ra.addFlashAttribute("org.springframework.validation.BindingResult.profileInfo", errors);
	        ra.addFlashAttribute("profileInfo", userBean);
			if (accountDAO.isEmailUsedByOtherAccounts(userBean.getEmail(), accountID)) {
				ra.addFlashAttribute("mess", "Email đã tồn tại trên hệ thống");
				return "redirect:/account/ProfilePage.htm";
			}
	        return "redirect:/account/ProfilePage.htm";
	    }
		account.setFirstName(userBean.getFirstName());
		account.setLastName(userBean.getLastName());
		account.setEmail(userBean.getEmail());
		account.setPhoneNumber(userBean.getPhoneNumber());
		if (profileDAO.changeProfileInfo(accountID, account)) {
			Account loggedInUser = accountDAO.getAccountByEmail(account.getEmail());
//			Xóa thông tin gmail cũ trong cookie
			Cookie cookie = new Cookie("userEmail", userEmail);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
//			Thêm thông tin gmail mới vào cookie
			cookie = new Cookie("userEmail", loggedInUser.getEmail());
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
//			Sửa thông tin trong session
			session.removeAttribute("userInfo");
			session.setAttribute("userInfo", accountDAO.getAccountByEmail(account.getEmail()));
		}
		return "redirect:/account/ProfilePage.htm";
//		return "redirect:/account/ProfilePage.htm";
	}

	@ModelAttribute
	void unchangeArrtibute(ModelMap modelMap,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		Account account = accountDAO.getAccountByEmail(userEmail);
		modelMap.addAttribute("userAccount", account);
	}

	@RequestMapping(value = "/account/AddNewAddress", method = RequestMethod.POST)
	public String addNewAddress(@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			HttpSession session, ModelMap modelMap, HttpServletRequest request,
			@ModelAttribute("userAddress") AddressUserBean userAddress, HttpServletRequest reques,
			HttpServletResponse response) {
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/login.htm";
		}
		Ward ward = profileDAO.getWard(userAddress.getWardId());
		Account account = accountDAO.getAccountByEmail(userEmail);
		Address newAddress = new Address(ward, account, userAddress.getAddressLine());
		boolean _f = profileDAO.addNewAddress(newAddress);
		return "redirect:/account/ProfilePage.htm";
	}

	@RequestMapping(value = "/account/ChangePassword", method = RequestMethod.GET)
	public String changePassword(
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			@ModelAttribute("password") ChangePassword password, HttpServletRequest request) {
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/login.htm";
		}
		return "user/account/changePassword";
	}

	@RequestMapping(value = "/account/ChangePassword", method = RequestMethod.POST)
	public String savePassword(@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			ModelMap model, @ModelAttribute("password") ChangePassword password, BindingResult errors,
			HttpSession session, HttpServletRequest request) {
		if (userEmail.equals("")) {
			request.setAttribute("user", new Account());
			return "redirect:/index.htm";
		}
		Account account = accountDAO.getAccountByEmail(userEmail);
		if (!BCrypt.checkpw(password.getOldPass(), account.getPassword())) {
			errors.rejectValue("oldPass", "password", "Mật khẩu hiện tại không đúng!");
		}
		if (BCrypt.checkpw(password.getNewPass(), account.getPassword())) {
			errors.rejectValue("newPass", "password", "Mật khẩu mới trùng với mật khẩu cũ!");
		}
		if (!password.getConfirmPass().equalsIgnoreCase(password.getNewPass())) {
			errors.rejectValue("confirmPass", "password", "Mật khẩu xác nhận không đúng!");
		}
		if (password.getNewPass().length() < 6) {
			errors.rejectValue("newPass", "password", "Mật khẩu quá ngắn cần > 6 ký tự");
		}

		if (errors.hasErrors()) {
			model.addAttribute("message", 0);
			return "user/account/changePassword";
		}

		account.setPassword(BCrypt.hashpw(password.getNewPass(), BCrypt.gensalt(12)));
		boolean s = accountDAO.updateAccount(account);
		if (s) {
			session.setAttribute("account", account);
			model.addAttribute("message", 1);
		} else {
			model.addAttribute("message", 0);
		}

		return "user/account/changePassword";
	}

	@RequestMapping(value = "account/OrderHistory")
	public String orderHistory(@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail,
			@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
			ModelMap modelMap) {
		Account account = accountDAO.getAccountByEmail(userEmail);
		if (account != null) {
			List<Orders> orders = orderDAO.getOrderFromAccountId(account.getAccountId());
			// Tính toán tổng số lượng dựa trên danh sách
			int totalCategories = orders.size();
			// Lấy thông tin phân trang
			Paginate paginate = paginateDAO.getInfoPaginate(totalCategories, 5, crrPage);

			// Lấy danh sách cho trang hiện tại
			List<Orders> os = orders.subList(paginate.getStart(), paginate.getEnd());

			modelMap.addAttribute("paginate", paginate);
			modelMap.addAttribute("orders", os);

			return "user/account/orderHistory";
		}
		return "redirect:/index.htm";
	}

	/*
	 * @ModelAttribute void chooseAddress(ModelMap modelMap) { List<Province>
	 * allProvince = profileDAO.getAllProvince(); ArrayList<Province> province =
	 * (ArrayList<Province>) profileDAO.getAllProvince(); AddressBean addressBean =
	 * new AddressDatasBean().ConvertToDataAddressBean(province);
	 * modelMap.addAttribute("allProvince", allProvince);
	 * modelMap.addAttribute("address", addressBean); AddressUserBean userAddress =
	 * new AddressUserBean(); modelMap.addAttribute("userAddress", userAddress); }
	 */

	@ModelAttribute
	void defaultAddress(HttpSession session,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		Account account = accountDAO.getAccountByEmail(userEmail);
		if (account != null)
			session.setAttribute("defaultAddressNumber", profileDAO.defaultAddressId(account.getAccountId()));
	}

	@RequestMapping(value = "order/myorder.htm", method = RequestMethod.GET)
	public String orderDetail(@RequestParam("orderId") int orderId, ModelMap model,
			@CookieValue(value = "userEmail", defaultValue = "", required = false) String userEmail) {
		Orders order = orderDAO.findOrder(orderId);

		if (userEmail.isEmpty()) {
			return "redirect:/login.htm";
		}
		if (!order.getAccount().getEmail().equals(userEmail)) {
			model.addAttribute("violate", "Yêu cầu của bạn không khả dụng. Bạn chỉ được xem đơn hàng của mình");
			return "user/account/order_detail";
		}
		List<OrderDetail> orderDetail = orderDAO.getOrderDetail(orderId);
		model.addAttribute("order", order);
		model.addAttribute("orderDetail", orderDetail);
		return "user/account/order_detail";
	}

}
