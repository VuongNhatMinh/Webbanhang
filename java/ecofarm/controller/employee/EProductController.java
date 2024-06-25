package ecofarm.controller.employee;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ecofarm.DAO.ICategoryDAO;
import ecofarm.DAO.IProductDAO;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.bean.ProductBean;
import ecofarm.bean.UploadFile;
import ecofarm.entity.Account;
import ecofarm.entity.Category;
import ecofarm.entity.Product;
import ecofarm.utility.Paginate;
import ecofarm.utility.TimeUtil;

@Controller
@RequestMapping("employee/products")
public class EProductController {
	private final int PROD_PER_PAGE = 5;
	@Autowired
	private ICategoryDAO categoryDAO;
	@Autowired
	private IProductDAO productDAO;
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();

	@RequestMapping()
	public String getListProduct(ModelMap model,
			@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
			@RequestParam(value = "sort", required = false, defaultValue = "none") String sort,
			@RequestParam(required = false, value = "search") String search) {
		List<Product> products = new ArrayList<Product>();
		if (search != null && !search.isEmpty()) {
			products = productDAO.searchProducts(search);
		}
		else {
			products = productDAO.getAllProducts();
		}
		
		int totalProducts = products.size();
		
		if ("name".equals(sort)) {
	        Collections.sort(products, new Comparator<Product>() {
	            public int compare(Product p1, Product p2) {
	                return p1.getProductName().compareTo(p2.getProductName());
	            }
	        });
	    }
		
		if ("price".equals(sort)) {
		    Collections.sort(products, new Comparator<Product>() {
		        public int compare(Product p1, Product p2) {
		          
		            BigDecimal price1 = BigDecimal.valueOf(p1.getPrice());
		            BigDecimal price2 = BigDecimal.valueOf(p2.getPrice());
		         
		            return price1.compareTo(price2);
		        }
		    });
		}

		Paginate paginate = paginateDAO.getInfoPaginate(totalProducts, PROD_PER_PAGE, crrPage);
		List<Product> prods = products.subList(paginate.getStart(), paginate.getEnd());
		model.addAttribute("paginate", paginate);
		model.addAttribute("products", prods);
		model.addAttribute("sort", sort);
		return "employee/product/product-list";
	}


	@Autowired
	@Qualifier("productImgDir")
	UploadFile productImgUpload;

	@RequestMapping(value = "create-product", method = RequestMethod.GET)
	public String getAddProduct(ModelMap model) {

		ProductBean productBean = new ProductBean();
		List<Category> cates = categoryDAO.getAllCategories();
		model.addAttribute("addProdBean", productBean);
		model.addAttribute("categories", cates);
		return "employee/product/product-form";
	}

	@RequestMapping(value = "create-product", method = RequestMethod.POST)
	public String postAddProduct(@ModelAttribute("addProdBean") ProductBean product, HttpSession session, BindingResult errors,
			RedirectAttributes re, ModelMap model) {

		Account acc = (Account) session.getAttribute("userInfo");
		if (acc == null) {
			System.out.print("Không có tài khoản đang đăng nhập");
			return "redirect:/logout.htm";
		}
		
		String productName = product.getProductName();
		if (productName.isEmpty() || !productName.matches("^[a-zA-Z0-9\\p{L} \\-'\",.;!?]+$")) {
			errors.rejectValue("productName", "product", "Không được bỏ trống và không chấp nhận kí tự đặc biệt");
	    }
		
		 // Kiểm tra quantity
	    int quantity = product.getQuantity();
	    if (quantity < 0) {
	    	errors.rejectValue("quantity", "product", "Số lượng tối thiểu là 0");
	    }

	    // Kiểm tra price
	    double price = product.getPrice();
	    if (price <= 0) {
	    	errors.rejectValue("price", "product", "Giá tối thiểu phải lớn hơn 0");
	    }
	    
	    if (errors.hasErrors()) {
	    	model.addAttribute("mess", "Thêm mới thất bại! ");
	    	List<Category> cates = categoryDAO.getAllCategories();
	        model.addAttribute("categories", cates);
	    	return "employee/product/product-form";
	    }
		Product newProduct = new Product();
		newProduct.setAccount(acc);
		Category category = categoryDAO.getCategory(product.getCategoryId());
		if (category != null) {
			newProduct.setCategory(category);
		} else {
			re.addFlashAttribute("mess", "Không tìm thấy category");
			return "redirect:/employee/products.htm";
		}
		if (!product.getImageFile().isEmpty()) {
			String photoName = productImgUpload.uploadImage(product.getImageFile());
			newProduct.setImage(photoName);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		newProduct.setUnit(product.getUnit());
		newProduct.setPostingDate(TimeUtil.getCurrentTime());
		newProduct.setProductName(product.getProductName());
		newProduct.setPrice(product.getPrice());
		newProduct.setQuantity(product.getQuantity());
		newProduct.setDetail(product.getDetail());
		newProduct.setPostingDate(product.getPostingDate());

		boolean done = productDAO.insertProduct(newProduct);
		if (!done) {
			model.addAttribute("mess", "Thêm product thất bại");
			return "employee/product/product-form";
		}
		re.addFlashAttribute("mess", "Thêm thành công");
		return "redirect:/employee/products.htm";
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String deleteProduct(@RequestParam("id") int id, RedirectAttributes re) {
		Product prod = productDAO.getProductByID(id);
		if (productDAO.deleteProduct(prod)) {
			try {
				if (prod.getImage() != null) {
					File oldImage = new File(productImgUpload.getBasePath() + prod.getImage());
					if (oldImage.exists()) {
						oldImage.delete();
					}
				}
			} catch (Exception e) {
				re.addFlashAttribute("mess", "Vui lòng kiểm tra lại ảnh sản phẩm");
			}
			re.addFlashAttribute("mess", "Xóa thành công");
		} else {
			re.addFlashAttribute("mess", "Không thể xóa, sản phẩm có thể đã được khách hàng sử dụng");
		}
		return "redirect:/employee/products.htm";
	}

	@RequestMapping(value = "update_product/{id}", method = RequestMethod.GET)
	public String getUpdateProduct(@PathVariable("id") int id, ModelMap model) {
		Product prod = productDAO.getProductByID(id);
		List<Category> cates = categoryDAO.getAllCategories();
		ProductBean productBean = new ProductBean(prod);
		model.addAttribute("categories", cates);
		model.addAttribute("updateProdBean", productBean);
		return "employee/product/product-form";
	}

	@RequestMapping(value = "update_product/{id}", method = RequestMethod.POST)
	public String postUpdateProduct(@ModelAttribute("updateProdBean") ProductBean product, RedirectAttributes re, BindingResult errors,
			ModelMap model) {
		Product foundProd = productDAO.getProductByID(product.getProductId());
		String productName = product.getProductName();
		if (productName.isEmpty() || !productName.matches("^[a-zA-Z0-9\\p{L} \\-'\",.;!?]+$")) {
			errors.rejectValue("productName", "product", "Không được bỏ trống và không chấp nhận kí tự đặc biệt");
	    }
		
		 // Kiểm tra quantity
	    int quantity = product.getQuantity();
	    if (quantity < 0) {
	    	errors.rejectValue("quantity", "product", "Số lượng tối thiểu là 0");
	    }

	    // Kiểm tra price
	    double price = product.getPrice();
	    if (price <= 0) {
	    	errors.rejectValue("price", "product", "Giá tối thiểu phải lớn hơn 0");
	    }
	    
	    if (errors.hasErrors()) {
	    	model.addAttribute("mess", "Cập nhật thất bại! ");
	    	List<Category> cates = categoryDAO.getAllCategories();
	        model.addAttribute("categories", cates);
	        product.setImage(foundProd.getImage());
	        model.addAttribute("updateProdBean", product);
	    	return "employee/product/product-form";
	    }
		if (foundProd != null) {
			Category category = categoryDAO.getCategory(product.getCategoryId());
			if (category != null) {
				foundProd.setCategory(category);
			}
			try {
				// nếu có cập nhật ảnh mới
				if (!product.getImageFile().isEmpty()) {
					String newImage = productImgUpload.uploadImage(product.getImageFile());
					// nếu có ảnh cũ, xóa nó đi
					if (foundProd.getImage() != null) {
						File oldImage = new File(productImgUpload.getBasePath() + foundProd.getImage());
						if (oldImage.exists()) {
							oldImage.delete();
						}
					}
					// Cập nhật ảnh mới cho product
					foundProd.setImage(newImage);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("mess", "Cập nhật thất bại");
				product.setImage(foundProd.getImage());
				model.addAttribute("updateProdBean", product);
				return "employee/product/product-form";
			}
			foundProd.setUnit(product.getUnit());
			foundProd.setProductName(product.getProductName());
			foundProd.setPrice(product.getPrice());
			foundProd.setQuantity(product.getQuantity());
			productDAO.updateProduct(foundProd);
			re.addFlashAttribute("mess", "Cập nhật thành công");
		}
		return "redirect:/employee/products.htm";
	}
}

