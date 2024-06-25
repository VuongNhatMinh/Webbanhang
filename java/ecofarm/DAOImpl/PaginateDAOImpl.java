package ecofarm.DAOImpl;

import ecofarm.DAO.IPaginateDAO;
import ecofarm.utility.Paginate;

public class PaginateDAOImpl implements IPaginateDAO {

	@Override
	public Paginate getInfoPaginate(int totalData, int limit, int currentPage) {
		Paginate paginate = new Paginate();
		paginate.setCurrentPage(checkCurrentPage(currentPage, getTotalPages(totalData, limit)));
		paginate.setLimit(limit);
		paginate.setTotalPage(getTotalPages(totalData, limit));
		paginate.setStart(getStart(currentPage, limit));
		paginate.setEnd(getEnd(currentPage, limit, totalData));
		return paginate;
	}
	private int checkCurrentPage(int currentPage, int totalPages) {
		if(currentPage < 1) {
			return 1;
		}
		if(currentPage > totalPages) {
			return totalPages;
		}
		return currentPage;
	}
	
	private int getStart(int currentPage,int limit) {
		return (currentPage-1)*limit;
	}
	private int getTotalPages(int totalData,int limit) {
		int totalPages = totalData / limit;
		return totalData > totalPages * limit ? totalPages+1:totalPages;
	}
	private int getEnd(int currentPage, int limit, int totalData) {
	    int end = currentPage * limit;
	    if (end > totalData) {
	        end = totalData;
	    }
	    return end;
	}
}
