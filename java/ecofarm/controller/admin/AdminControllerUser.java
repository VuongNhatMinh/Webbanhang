package ecofarm.controller.admin;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecofarm.DAO.IAccountDAO;
import ecofarm.DAO.IAccountDAO.EnumRole;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.bean.UploadFile;
import ecofarm.bean.UserBean;
import ecofarm.entity.Account;
import ecofarm.entity.Role;
import ecofarm.utility.Paginate;

@Controller
@RequestMapping("admin/user")
public class AdminControllerUser {
	private final int USER_PER_PAGE = 10;
	@Autowired
	private IAccountDAO accountDAO;
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();

	@RequestMapping()
	public String index() {
		return "redirect:get-employee.htm";
	}

	@RequestMapping("get-employee")
	public String getListEmployee(ModelMap model, @RequestParam(required = false, value = "search") String search,
			@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage) {
		List<Account> accounts = accountDAO.listAccountWithRole(EnumRole.EMPLOYEE, search);

		int totalFeedbacks = accounts.size();
		Paginate paginate = paginateDAO.getInfoPaginate(totalFeedbacks, USER_PER_PAGE, crrPage);
		List<Account> accs = accounts.subList(paginate.getStart(), paginate.getEnd());
		model.addAttribute("paginate", paginate);
		model.addAttribute("accounts", accs);
		model.addAttribute("source", "get-employee.htm");
		return "admin/user/user-list";
	}

	@RequestMapping("get-guest")
	public String getListGuest(ModelMap model,
			@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
			@RequestParam(required = false, value = "search") String search) {
		List<Account> accounts = accountDAO.listAccountWithRole(EnumRole.GUEST, search);

		int totalFeedbacks = accounts.size();
		Paginate paginate = paginateDAO.getInfoPaginate(totalFeedbacks, USER_PER_PAGE, crrPage);
		List<Account> accs = accounts.subList(paginate.getStart(), paginate.getEnd());
		model.addAttribute("paginate", paginate);
		model.addAttribute("accounts", accs);
		model.addAttribute("source", "get-guest.htm");
		return "admin/user/user-list";
	}

	@RequestMapping(value="changestatus", method=RequestMethod.POST)
	public String modifyStatus(@RequestParam("id") int id, RedirectAttributes re) {
		Account acc = accountDAO.getAccountByID(id);
		String role = acc.getRole().getRoleId().toLowerCase();
		if (acc != null) {
			// System.out.println(acc.getStatus());
			if (acc.getStatus() == 1)
				acc.setStatus(0);
			else if (acc.getStatus() == 0)
				acc.setStatus(1);
			boolean completed = accountDAO.updateAccount(acc);
			if (completed) {
				re.addFlashAttribute("mess", "Sửa trạng thái thành công");
			} else {
				re.addFlashAttribute("mess", "Sửa trạng thái thất bại");
			}
		}
		return String.format("redirect:/admin/user/get-%s.htm", role);
	}

	@RequestMapping(value = { "create-guest", "create-employee" }, method = RequestMethod.GET)
	public String getCreateUser(HttpServletRequest request, ModelMap model) {
		UserBean acc = new UserBean();
		String uri = request.getRequestURI();
		String role = uri.contains("guest") ? "Guest" : uri.contains("employee") ? "Employee" : "";
		model.addAttribute("role", role);
		model.addAttribute("adduser", acc);
		return "admin/user/user-form";
	}

	@Autowired
	@Qualifier("accountImgDir")
	UploadFile accountImgUpload;

	@RequestMapping(value = { "create-guest", "create-employee" }, method = RequestMethod.POST)
	public String createEmployee(HttpServletRequest request, @Validated @ModelAttribute("adduser") UserBean user,
			BindingResult errors, RedirectAttributes ra, ModelMap model) {
		String uri = request.getRequestURI();
		String _role = uri.contains("create-guest") ? "Guest" : uri.contains("create-employee") ? "Employee" : "";
		Role role = _role.equals("Employee") ? accountDAO.getRoleByEnum(EnumRole.EMPLOYEE)
				: accountDAO.getRoleByEnum(EnumRole.GUEST);
		Account account = null;
		String photoName = null;
		if (user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 50) {
	        errors.rejectValue("password", "userbean", "Mật khẩu phải từ 6 đến 50 kí tự");
	    }
		if (!errors.hasErrors()) {
			if (!user.getAvatar().isEmpty()) {
				photoName = accountImgUpload.uploadImage(user.getAvatar());
				System.out.println("Account photo: " + photoName);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			account = new Account(role, user.getLastName(), user.getFirstName(), user.getEmail(), user.getPhoneNumber(),
					photoName, user.getPassword());
			if (!user.getAvatarDir().isEmpty()) {
				account.setAvatar(user.getAvatarDir());
			}

			if (accountDAO.getAccountByEmail(user.getEmail()) != null) {
				model.addAttribute("adduser", user);
				model.addAttribute("role", _role);
				model.addAttribute("mess", "Thông tin đã tồn tại trên hệ thống");
				return "admin/user/user-form";
			}

			if (accountDAO.createAccount(account)) {
				ra.addFlashAttribute("mess", "Tạo tài khoản thành công");
				return String.format("redirect:/admin/user/get-%s.htm", _role.toLowerCase());
			}
		}
		model.addAttribute("adduser", user);
		model.addAttribute("role", _role);
		return "admin/user/user-form";
	}

	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public String editUser(ModelMap model, @RequestParam("id") int id) {
		Account account = accountDAO.getAccountByID(id);
		String role = "";
		if (account.getRole().getRoleId().equals("GUEST")) {
			role = "Guest";
		} else if (account.getRole().getRoleId().equals("EMPLOYEE")) {
			role = "Employee";
		}
		UserBean userBean = new UserBean(account.getEmail(), account.getFirstName(), account.getLastName(),
				account.getPhoneNumber(), account.getAvatar());
		model.addAttribute("updateuser", userBean);
		model.addAttribute("id", id);
		//System.out.print("id:" + id);
		model.addAttribute("role", role);
		return "admin/user/edit-user";
	}

	@RequestMapping(value = "edit", method = RequestMethod.POST)
	public String postEditUser(RedirectAttributes ra, @Validated @ModelAttribute("updateuser") UserBean user,
			BindingResult errors, ModelMap model, @RequestParam("id") int id) {
		Account account = accountDAO.getAccountByID(id);
		String role = "";
		if (account.getRole().getRoleId().equals("GUEST")) {
			role = "Guest";
		} else if (account.getRole().getRoleId().equals("EMPLOYEE")) {
			role = "Employee";
		}
		
		if (!user.getPassword().trim().isEmpty() && (user.getPassword().length() < 6 || user.getPassword().length() > 50)) {
	        errors.rejectValue("password", "userbean", "Mật khẩu phải từ 6 đến 50 kí tự");
	    }
		
		if (errors.hasErrors()) {
			model.addAttribute("mess", "Update failed");
			model.addAttribute("id", id);
			model.addAttribute("role", role);
			return "admin/user/edit-user";
		}
		
		if (accountDAO.isEmailUsedByOtherAccounts(user.getEmail(), id)) {
			model.addAttribute("updateuser", user);
			model.addAttribute("id", id);
			model.addAttribute("role", role);
			model.addAttribute("mess", "Email đã tồn tại trên hệ thống");
			return "admin/user/edit-user";
		}
		if (!user.getPassword().trim().isEmpty() && user.getPassword().length() >= 6 && user.getPassword().length() <= 50) {
			account.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12)));
		}
		account.setLastName(user.getLastName());
		account.setFirstName(user.getFirstName());
		account.setPhoneNumber(user.getPhoneNumber());
		account.setEmail(user.getEmail());
		try {
			// nếu có cập nhật ảnh mới
			if (!user.getAvatar().isEmpty()) {
				String newImage = accountImgUpload.uploadImage(user.getAvatar());
				// nếu có ảnh cũ, xóa nó đi
				if (account.getAvatar() != null) {
					File oldImage = new File(accountImgUpload.getBasePath() + account.getAvatar());
					if (oldImage.exists()) {
						oldImage.delete();
					}
				}
				// Cập nhật ảnh mới
				account.setAvatar(newImage);
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			user.setAvatarDir(account.getAvatar());
			model.addAttribute("mess", "Có lỗi khi xử lý ảnh");
			model.addAttribute("updateuser", user);
			return "admin/user/edit-user";
		}

		if (accountDAO.updateAccount(account)) {
			ra.addFlashAttribute("mess", "Cập nhật thành công");
			if (account.getRole().getRoleName().equals("Guest"))

				return "redirect:/admin/user/get-guest.htm";

			else
				return "redirect:/admin/user/get-employee.htm";

		}
		model.addAttribute("mess", "Cập nhật thất bại");
		model.addAttribute("updateuser", user);
		return "admin/user/edit-user";
	}

}
