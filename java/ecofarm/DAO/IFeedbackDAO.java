package ecofarm.DAO;

import java.util.List;

import ecofarm.entity.Feedback;

public interface IFeedbackDAO {
	public List<Feedback> getFeedbackByProduct(int productId);
	public boolean addFeedback(int productId,int accountId);
	public List<Feedback> getAllFeedbacks();
	public List<Feedback> searchFeedback(String search);
	public Feedback getFeedBackById(int fid);
	public boolean updateFeedback(Feedback feedback);
	public boolean addFeedback(Feedback feedback);
	public List<Feedback> getFeedbacksByAccout(int accountId);
	public Feedback getFeedback(int productId,int accountId);
}
