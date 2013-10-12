package util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTime {
	
	static public String getDisplayTime(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		
		String time = new StringBuffer()
				.append(calendar.get(Calendar.HOUR_OF_DAY) + ":")
				.append(calendar.get(Calendar.MINUTE) + " ")
				.append(DateTime.getTwelveHourClock(calendar.get(Calendar.AM_PM)))
				.toString();
		
		System.out.println("time = " + time);
		return time;
	}
	
	static public String getTwelveHourClock(int period) {
		if (period == 0)
			return "AM";
		else
			return "PM";
	}
}
