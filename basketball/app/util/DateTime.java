package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime {
	static public String getDisplayTime(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a z", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	static public String getDisplayDateLong(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEEEEEEE, MMMMMMMMM d, y", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	static public String getDisplayDateMiddle(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMMMMMM d, y", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	static public String getDisplayDateShort(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	static public String getFindDateShort(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	static public String getFindDateNaked(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
	static public boolean isDate(String date)  {
		try {
			new SimpleDateFormat("yyyy-MM-dd").parse(date);
			return true;
		} 
		catch (ParseException e) {
			return false;
		}
	}
	static public Date createDateFromString(String date) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
		} 
		catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	static public Date createDateMaxTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, 23);
		cal.add(Calendar.MINUTE, 59);
		cal.add(Calendar.SECOND, 59);
		return cal.getTime();
	}
	static public Date getDateMinSeason(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.MONTH) <= 5 && cal.get(Calendar.DATE) <= 30)
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);

		cal.set(Calendar.MONTH, 6);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}	
	static public Date getDateMaxSeason(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.MONTH) >= 6 && cal.get(Calendar.DATE) >= 1)
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);

		cal.set(Calendar.MONTH, 5);
		cal.set(Calendar.DATE, 30);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}
}
