package ecofarm.utility;

import java.util.Random;

public class CaptchaGenerator {
	public static String generateCaptchaCode(int length) {
	    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    Random random = new Random();
	    StringBuilder captchaCode = new StringBuilder(length);

	    for (int i = 0; i < length; i++) {
	        captchaCode.append("<span>").append(characters.charAt(random.nextInt(characters.length()))).append("</span>");
	    }

	    return captchaCode.toString();
	}
	public static String convertToHtmlSpan(String captcha) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < captcha.length(); i++) {
            sb.append("<span>").append(captcha.charAt(i)).append("</span>");
        }
        return sb.toString();
    }
}
