package ecofarm.utility;

import java.util.Date;
import java.util.Random;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("mailer")
public class Mailer {
	@Autowired
	JavaMailSender mailer;
	private String from = "noreply_webnongsan@gmail.com";

	private String validateCodeGenerater(int length) {
		String validateCodeFP = "";
		while (validateCodeFP.length() < length) {
			Random rand = new Random();
			validateCodeFP += rand.nextInt(10) + "";
		}
		return validateCodeFP;
	}

	public String send(String to) {
		try {
			MimeMessage mail = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail);
			String validateCodeFP = validateCodeGenerater(6);
			String body = """
					<h1 style="font-size:13px">Xin chào <span style="color:green; font-weight:bold">%s</span> </h1>
					<h2 style="font-size:13px">Hãy xác thực email của bạn bằng mã xác thực dưới đây:</h2>
					<div style="font-size:20px;padding:5px 10px;background-color:rgba(222,222,222,0.6);display: inline-block;margin-top:5px;margin-left:5px">
					<span style="color:rgba(25, 135, 84);font-weight:bold">%s</span></div>
					"""
					.formatted(to, validateCodeFP);
			helper.setFrom(from, from);
			helper.setTo(to);
			helper.setReplyTo(from, from);
			helper.setSubject("Hãy xác thực tài khoản của bạn");
			helper.setText(body, true);
			mailer.send(mail);
			return validateCodeFP;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean sendForUs(String to, String subject, String body) {
		try {
			MimeMessage mail = mailer.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail);

			helper.setFrom(to, to);
			helper.setTo(from);
			helper.setReplyTo(to, to);
			helper.setSubject(subject);
			helper.setText(body, true);
			mailer.send(mail);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean sendOrder(String to, int orderCode, Date orderDate, int totalPrice, String paymentMethod) {
	    try {
	        MimeMessage mail = mailer.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mail);
	        
	        String body = 
	            "<div style=\"font-family: 'Arial', sans-serif; background-color: #f2f2f2; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0;\">" +
	            "  <div class=\"container\" style=\"background-color: #fff; padding: 40px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); max-width: 600px; text-align: center;\">" +
	            "      <h1 style=\"color: #333; margin-bottom: 20px;\">Cảm ơn bạn đã đặt hàng!</h1>" +
	            "      <p style=\"line-height: 1.6; color: #666; margin-bottom: 30px;\">Đơn hàng của bạn đã được đặt thành công và đang được xử lý.</p>" +
	            "      <div class=\"order-details\" style=\"display: flex; flex-direction: column; gap: 20px;\">" +
	            "          <h2 style=\"color: #333; font-size: 1.2rem; margin-bottom: 10px;\">Thông tin đơn hàng</h2>" +
	            "          <ul style=\"list-style: none; padding: 0;\">" +
	            "              <li style=\"display: flex; justify-content: space-between; font-size: 1rem; color: #666;\">Mã đơn hàng: <span style=\"font-weight: bold; color: #333;\">" + orderCode + "</span></li>" +
	            "              <li style=\"display: flex; justify-content: space-between; font-size: 1rem; color: #666;\">Ngày đặt hàng: <span style=\"font-weight: bold; color: #333;\">" + orderDate + "</span></li>" +
	            "              <li style=\"display: flex; justify-content: space-between; font-size: 1rem; color: #666;\">Tổng tiền: <span style=\"font-weight: bold; color: #333;\">" + totalPrice + " đ</span></li>" +
	            "              <li style=\"display: flex; justify-content: space-between; font-size: 1rem; color: #666;\">Phương thức thanh toán: <span style=\"font-weight: bold; color: #333;\">" + paymentMethod + "</span></li>" +
	            "          </ul>" +
	            "      </div>" +
	            "      <a href=\"#\" class=\"button\" style=\"display: inline-block; padding: 12px 25px; background-color: #7fad39; color: #fff; border: none; border-radius: 5px; cursor: pointer; text-decoration: none; font-weight: bold; transition: background-color 0.3s;\">Xem chi tiết đơn hàng</a>" +
	            "  </div>" +
	            "</div>";

	        helper.setFrom(from, from);
	        helper.setTo(to);
	        helper.setSubject("Xác nhận đặt hàng");
	        helper.setText(body, true);
	        mailer.send(mail);
	        return true;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
