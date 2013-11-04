package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	static public boolean isValidDate(String date)  {
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
}
