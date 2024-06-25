package ecofarm.controller.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ecofarm.DAO.ICategoryDAO;
import ecofarm.DAO.IFeedbackDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.bean.Company;
import ecofarm.entity.Product;

@Controller
@RequestMapping("/product-detail")
public class ProductDetailController {
	@Autowired
	private IProductDAO productDAO;
	@Autowired
	private ICategoryDAO categoryDAO;
	@Autowired
	private IFeedbackDAO feedbackDAO;
	@Autowired
	@Qualifier("ecofarm")
	Company company;
	
	@RequestMapping(params = {"productId"})
	public String Index(@RequestParam("productId") String productId,HttpServletRequest request) {
		request.setAttribute("company", company);
		Product product = productDAO.getProductByID(Integer.parseInt(productId));
		if(product!=null) {
			productDAO.setRatingStar(product);
			productDAO.setReviews(product);			
			request.setAttribute("product",product);
			request.setAttribute("relatedProducts",productDAO.getProductsByCategoryID(product.getCategory().getCategoryId()));
			request.setAttribute("categories", categoryDAO.getAllCategories());
			request.setAttribute("feedbacks", feedbackDAO.getFeedbackByProduct(product.getProductId()));
		}
		return "user/product/productDetails";
	}
}
