package ecofarm.controller.employee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("employee")
public class EmployeeController{
	@RequestMapping("index")
	public String index() {
		return "redirect:/employee/dashboard.htm";
	}
}