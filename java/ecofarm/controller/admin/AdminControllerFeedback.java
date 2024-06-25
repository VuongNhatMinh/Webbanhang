package ecofarm.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecofarm.DAO.IFeedbackDAO;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.entity.Feedback;
import ecofarm.utility.Paginate;

@Controller
@RequestMapping("admin/feedback")
public class AdminControllerFeedback {
	private final int FB_PER_PAGE = 10;
	@Autowired
	private IFeedbackDAO feedbackDAO;
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();
	
	@RequestMapping()
	public String index(ModelMap model,
	                    @RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
	                    @RequestParam(value = "star", required = false) Integer star,
	                    @RequestParam(value = "search", required = false) String search) {
	    List<Feedback> fbs = new ArrayList<Feedback>();
	    if (search != null && !search.isEmpty()) {
	    	fbs = feedbackDAO.searchFeedback(search);
	    }
	    else {
	    	fbs = feedbackDAO.getAllFeedbacks();
	    }
	    // Kiểm tra xem star có tồn tại không
	    // Nếu có, lọc số sao để hiển thị
	    if (star != null) {
	        fbs = fbs.stream().filter(r -> r.getRatingStar() == star).collect(Collectors.toList());
	    }
	    
	    int totalFeedbacks = fbs.size();
	    Paginate paginate = paginateDAO.getInfoPaginate(totalFeedbacks, FB_PER_PAGE, crrPage);
		List<Feedback> feedbacks = fbs.subList(paginate.getStart(), paginate.getEnd());
		model.addAttribute("paginate", paginate);
		model.addAttribute("feedbacks", feedbacks);
		model.addAttribute("star", star);
		return "admin/feedback/feedback-list";
	}
	
	@RequestMapping(value="changestatus", method=RequestMethod.POST)
	public String modifyStatus(@RequestParam("id") int id, RedirectAttributes re) {
		Feedback feedback = feedbackDAO.getFeedBackById(id);
		if (feedback != null) {
			//System.out.println(feedback.getStatus());
			if (feedback.getStatus() == 1)
				feedback.setStatus(0);
			else if (feedback.getStatus() == 0)
				feedback.setStatus(1);
			boolean completed = feedbackDAO.updateFeedback(feedback);
			if (completed) {
				re.addFlashAttribute("mess", "Sửa trạng thái thành công");
			}
			else {
				re.addFlashAttribute("mess", "Sửa trạng thái thất bại");
			}
		}
		return "redirect:/admin/feedback.htm";
	}

}
