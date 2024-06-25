package ecofarm.controller.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import ecofarm.DAO.ICategoryDAO;
import ecofarm.DAOImpl.PaginateDAOImpl;
import ecofarm.bean.CategoryBean;
import ecofarm.bean.UploadFile;
import ecofarm.entity.Category;
import ecofarm.utility.Paginate;

@Controller
@RequestMapping("/admin/category")
public class AdminControllerCategory {
	private final int CATE_PER_PAGE = 5;
	@Autowired
	private ICategoryDAO categoryDAO;
	private PaginateDAOImpl paginateDAO = new PaginateDAOImpl();

	@RequestMapping()
	public String getCategoryList(ModelMap model, @RequestParam(required = false, value = "search") String search,
			@RequestParam(value = "crrPage", required = false, defaultValue = "1") int crrPage,
			@RequestParam(value = "filter", defaultValue = "0") int filter) {
		List<Category> categories = new ArrayList<Category>();
		if (search != null && !search.isEmpty()) {
			categories = categoryDAO.searchCategory(search);
		} else {
			// Lấy danh sách tất cả các categories
			categories = categoryDAO.getAllCategories();
		}

		// Áp dụng filter
		if (filter == 1) {
			categories = categories.stream().filter(r -> r.getProducts().size() > 0).collect(Collectors.toList());
		} else if (filter == 2) {
			categories = categories.stream().filter(r -> r.getProducts().size() <= 0).collect(Collectors.toList());
		}

		// Tính toán tổng số lượng danh mục dựa trên danh sách đã lọc
		int totalCategories = categories.size();
		// Lấy thông tin phân trang
		Paginate paginate = paginateDAO.getInfoPaginate(totalCategories, CATE_PER_PAGE, crrPage);

		// Lấy danh sách category cho trang hiện tại
		List<Category> cates = categories.subList(paginate.getStart(), paginate.getEnd());

		model.addAttribute("paginate", paginate);
		model.addAttribute("categories", cates);
		model.addAttribute("filter", filter);

		return "admin/category/category-list";
	}

	@Autowired
	@Qualifier("categoryImgDir")
	UploadFile baseUploadFile;

	@RequestMapping("addcategory")
	public String getCategoryAdd(ModelMap model) {
		CategoryBean categoryBean = new CategoryBean();
		model.addAttribute("addCate", categoryBean);
		return "admin/category/category-form";
	}

	@RequestMapping(value = "addcategory", method = RequestMethod.POST)
	public String addCategory(@Validated @ModelAttribute("addCate") CategoryBean categoryBean, BindingResult errors,
			ModelMap model, RedirectAttributes re) {
		if (categoryBean.getName() == null || categoryBean.getName().isEmpty()
			    || !categoryBean.getName().matches("^[a-zA-Z0-9\\p{L} \\-'\",.;!?]+$")) {
			    errors.rejectValue("name", "categoryBean", "Tên không được bỏ trống và không chấp nhận kí tự đặc biệt");
			}

		if (errors.hasErrors()) {
			model.addAttribute("mess", "Tên category không hợp lệ.");
			return "admin/category/category-form";
		} else {
			// Tiếp tục xử lý khi không có lỗi
			Category category = new Category();
			category.setName(categoryBean.getName());
			if (!categoryBean.getFileImage().isEmpty()) {
				String photoName = baseUploadFile.uploadImage(categoryBean.getFileImage());
				category.setImage(photoName);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			boolean done = categoryDAO.addCategory(category);
			if (!done) {
				re.addFlashAttribute("mess", "Thêm thất bại");
			}
			re.addFlashAttribute("mess", "Thêm thành công");
		}
		return "redirect:/admin/category.htm";
	}

	@RequestMapping("update_category")
	public String gUpdateCate(@RequestParam("id") int id, ModelMap model) {
		Category cate = categoryDAO.getCategory(id);
		CategoryBean categoryBean = new CategoryBean(cate);
		model.addAttribute("updateCate", categoryBean);
		return "admin/category/category-form";
	}

	@RequestMapping(value = "update_category", method = RequestMethod.POST)
	public String pUpdateCate(@ModelAttribute("updateCate") CategoryBean categoryBean, ModelMap model, BindingResult errors,
			RedirectAttributes re) {
		// Lấy cate được chọn
		Category category = categoryDAO.getCategory(categoryBean.getId());
		// nếu có cate trả về
		if (category != null) {
			if (categoryBean.getName() == null || categoryBean.getName().isEmpty()
				    || !categoryBean.getName().matches("^[a-zA-Z0-9\\p{L} \\-'\",.;!?]+$")) {
				    errors.rejectValue("name", "categoryBean", "Tên không được bỏ trống và không chấp nhận kí tự đặc biệt");
				}
			if (errors.hasErrors()) {
				categoryBean.setImage(category.getImage());
				model.addAttribute("mess", "Tên category không hợp lệ.");
				model.addAttribute("updateCate", categoryBean);
				return "admin/category/category-form";
			}
			// Đặt tên mới
			category.setName(categoryBean.getName());
			try {
				// nếu có cập nhật ảnh mới
				if (!categoryBean.getFileImage().isEmpty()) {
					String newImage = baseUploadFile.uploadImage(categoryBean.getFileImage());
					// nếu có ảnh cũ, xóa nó đi
					if (category.getImage() != null) {
						File oldImage = new File(baseUploadFile.getBasePath() + category.getImage());
						if (oldImage.exists()) {
							oldImage.delete();
						}
					}
					// Cập nhật ảnh mới cho cate
					category.setImage(newImage);
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				categoryBean.setImage(category.getImage());
				model.addAttribute("mess", "Có lỗi xảy ra");
				model.addAttribute("updateCate", categoryBean);
				return "admin/category/category-form";
			}
			categoryDAO.updateCategory(category);
			re.addFlashAttribute("mess", "Cập nhật thành công");
		}
		return "redirect:/admin/category.htm";
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String deleteCate(@RequestParam("id") int id, RedirectAttributes re) {
		Category category = categoryDAO.getCategory(id);
		if (categoryDAO.deleteCategory(category)) {
			try {
				// nếu có ảnh, xóa nó đi
				if (category.getImage() != null) {
					File oldImage = new File(baseUploadFile.getBasePath() + category.getImage());
					if (oldImage.exists()) {
						oldImage.delete();
					}
				}
			} catch (Exception e) {
				re.addFlashAttribute("mess", "Kiểm tra lại hình ảnh của bạn");
			}
			re.addFlashAttribute("mess", "Xóa thành công");
		} else {
			re.addFlashAttribute("mess", "Có lỗi xảy ra");
		}

		return "redirect:/admin/category.htm";
	}
}
