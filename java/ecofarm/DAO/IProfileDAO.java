package ecofarm.DAO;

import java.util.List;

import ecofarm.entity.Account;
import ecofarm.entity.Address;
import ecofarm.entity.District;
import ecofarm.entity.Feedback;
import ecofarm.entity.OrderDetail;
import ecofarm.entity.Province;
import ecofarm.entity.Ward;



public interface IProfileDAO {
	public Account getProfileInfo(Account account);
	public Account getAccountByID(int accountID);
	public boolean changePassword(Account account, String newPassword);
	public boolean changeProfileInfo(int accountID, Account changeInfo);
	public List<Address> getAllAddressInfo(Account account);
	public boolean deleteAddress(int deletedAddressId);
	public List<Feedback> getAllFeedbackDetail(Account account);
	public List<Province> getAllProvince();
	public boolean addNewAddress (Address newAddress);
	public Ward getWard(int wardId);
	public boolean chooseDefaultAddress(Account account,int addressId);
	public int defaultAddressId(int accountId);
	public boolean removeDefalutAddress(Account account);
}
