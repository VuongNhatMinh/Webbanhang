package ecofarm.controller.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.ICategoryDAO;
import ecofarm.bean.Company;
import ecofarm.bean.ContactBean;
import ecofarm.entity.Category;
import ecofarm.utility.Mailer;

@RequestMapping("contact")
@Controller
public class ContactController {
	@Autowired
	Mailer mailer;
	@Autowired
	@Qualifier("ecofarm")
	Company company;
	@Autowired
	private ICategoryDAO categoryDAO;

	@ModelAttribute("contactBean")
	public ContactBean getContactBean() {
		return new ContactBean();
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String ContactForm(HttpServletRequest request, ModelMap model) {
		List<Category> cates = categoryDAO.getAllCategories();
		request.setAttribute("categories", cates);
		request.setAttribute("company", company);
		model.addAttribute("contactBean", new ContactBean());
		return "user/contact";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String submitContactForm(@Valid @ModelAttribute("contactBean") ContactBean contactBean,
            BindingResult bindingResult, ModelMap model) {
		try {
			if (bindingResult.hasErrors()) {
				model.addAttribute("message", "Gửi mail thất bại");
	            return "user/contact";
	        }
			mailer.sendForUs(contactBean.getEmailContact(), contactBean.getTitle(), contactBean.getComments());
			model.addAttribute("message", "Gửi mail thành công");
			return "redirect:/index.htm";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println(e.getMessage());
			model.addAttribute("message", "Gửi mail thất bại");
			return "user/contact";
		}
	}
}
