package ecofarm.controller.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ecofarm.DAO.ICategoryDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.bean.Company;
import ecofarm.entity.Category;
import ecofarm.entity.Product;
import ecofarm.utility.Paginate;

@Controller
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private IProductDAO productDAO;
	@Autowired
	private ICategoryDAO categoryDAO;
	
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();
	@Autowired
	@Qualifier("ecofarm")
	Company company;

	@RequestMapping()
	public ModelAndView Product(@RequestParam(value = "categoryId", required = false, defaultValue = "0") int id,
			@RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage,
			HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<Product> products = productDAO.getProductsByCategoryID(id);
		List<Category> cates = categoryDAO.getAllCategories();
		int maxPri = this.getMaxPrice(products);
		mv.addObject("maxprice",maxPri);
		mv.addObject("categories", cates);
		mv.addObject("productsByCategory", products);
		mv.addObject("latestProducts", productDAO.getLatestProductsByCaID(id));
		mv.addObject("categoryID", id);
		mv.addObject("paginateInfo", paginateDAO.getInfoPaginate(products.size(), 5, currentPage));
		mv.setViewName("user/product/product");
		request.setAttribute("company", company);
		return mv;
	}
	
	@RequestMapping(value="search", method = RequestMethod.GET)
	public String getListProduct(ModelMap model,
			@RequestParam(value = "currentPage", required = false, defaultValue = "1") int crrPage,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(required = true, value = "search") String search,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
	        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,HttpServletRequest request) {
		request.setAttribute("company", company);
		List<Product> products = productDAO.searchProducts(search);
		int maxPri = this.getMaxPrice(products);
		List<Category> cates = categoryDAO.getAllCategories();
		
		if (minPrice != null && maxPrice != null) {
			if (!(minPrice.compareTo(BigDecimal.ZERO) >= 0 && minPrice.compareTo(new BigDecimal(1000)) < 0 && maxPrice.compareTo(new BigDecimal(maxPri)) == 0)) {
		        products = filterByPriceRange(products, minPrice, maxPrice);
		    }
	    }

	    if ("name".equals(sort)) {
	        Collections.sort(products, Comparator.comparing(Product::getProductName));
	    }

	    if ("price".equals(sort)) {
	        Collections.sort(products, Comparator.comparing(Product::getPrice));
	    }
	    
	    int totalProducts = products.size();
	    		
		Paginate paginate = paginateDAO.getInfoPaginate(totalProducts, 6, crrPage);
		List<Product> prods = products.subList(paginate.getStart(), paginate.getEnd());
		model.addAttribute("categories", cates);
		model.addAttribute("latestProducts", productDAO.getLatestProduct());
		model.addAttribute("paginateInfo", paginate);
		model.addAttribute("total", totalProducts);
		model.addAttribute("products", prods);
		model.addAttribute("sort", sort);
		model.addAttribute("maxprice", maxPri);
		model.addAttribute("search", search);
		//System.out.println(maxPri);
		return "user/product/searchProduct";
	}
	
	private List<Product> filterByPriceRange(List<Product> products, BigDecimal minPrice, BigDecimal maxPrice) {
	    return products.stream()
	            .filter(product -> {
	                BigDecimal price = BigDecimal.valueOf(product.getPrice());
	                return price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0;
	            })
	            .collect(Collectors.toList());
	}
	
	private int getMaxPrice(List<Product> products) {
        if (products.isEmpty()) {
            return 1000; // Trả về 1000 (giá mặc định)
        }

        return (int) products.stream()
                .mapToDouble(Product::getPrice) // Chuyển đổi danh sách thành danh sách giá
                .max() // Tìm giá lớn nhất
                .orElse(1000);
    }
	
	@RequestMapping(value="filterBy", method = RequestMethod.GET)
	public String getProductCategorySearch(ModelMap model,
			@RequestParam(value = "categoryId", required = false, defaultValue = "0") int id,
			@RequestParam(value = "currentPage", required = false, defaultValue = "1") int crrPage,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
	        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
		List<Category> cates = categoryDAO.getAllCategories();
		List<Product> productsByCategory = productDAO.getProductsByCategoryID(id);
		int maxPri = this.getMaxPrice(productsByCategory);
		
		if (minPrice != null && maxPrice != null) {
			if (!(minPrice.compareTo(BigDecimal.ZERO) >= 0 && minPrice.compareTo(new BigDecimal(1000)) < 0 && maxPrice.compareTo(new BigDecimal(maxPri)) == 0)) {
		        productsByCategory = filterByPriceRange(productsByCategory, minPrice, maxPrice);
		    }
	    }

	    if ("name".equals(sort)) {
	        Collections.sort(productsByCategory, Comparator.comparing(Product::getProductName));
	    }

	    if ("price".equals(sort)) {
	        Collections.sort(productsByCategory, Comparator.comparing(Product::getPrice));
	    }

	    int totalProducts = productsByCategory.size();

		model.addAttribute("categories", cates);
		model.addAttribute("latestProducts", productDAO.getLatestProduct());
		model.addAttribute("productsByCategory",productsByCategory);
		model.addAttribute("paginateInfo", paginateDAO.getInfoPaginate(productsByCategory.size(), 5, crrPage));
		model.addAttribute("total", totalProducts);
		model.addAttribute("sort", sort);
		model.addAttribute("maxprice", maxPri);

		return "user/product/product";
	}
}
