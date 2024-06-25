package ecofarm.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public static Date getCurrentTime() {

		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = formatter.format(currentDate);

		try {
			return formatter.parse(formattedDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static Date getDelayedTime(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, +hour);
        return cal.getTime();
    }
}
