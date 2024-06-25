package ecofarm.DAO;

import ecofarm.utility.Paginate;

public interface IPaginateDAO {
	public Paginate getInfoPaginate(int totalData, int limit, int currentPage);
}
