package util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
	static public String getDisplayTime(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("hh:mm a z");
		return date.toString(dateTimeFormatter);
	}
	static public String getDisplayDateLong(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEEEEEEEE, MMMMMMMMM d, y");
		return date.toString(dateTimeFormatter);
	}
	static public String getDisplayDateMiddle(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MMMMMMMMM d, y");
		return date.toString(dateTimeFormatter);
	}
	static public String getDisplayDateShort(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM-dd-yyyy");
		return date.toString(dateTimeFormatter);
	}
	static public String getFindDateShort(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		return date.toString(dateTimeFormatter);
	}
	static public String getFindDateTimeShort(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		return date.toString(dateTimeFormatter);
	}
	static public String getFindDateNaked(DateTime date) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
		return date.toString(dateTimeFormatter);
	}
	static public String getSeason(DateTime date) {
		DateTime minDate = getDateMinSeason(date);
		DateTimeFormatter sdfMin = DateTimeFormat.forPattern("yyyy");
		String minYear = minDate.toString(sdfMin);
		
		DateTime  maxDate = getDateMaxSeason(date);
		DateTimeFormatter sdfMax = DateTimeFormat.forPattern("yy");
		String maxYear = maxDate.toString(sdfMax);
		
		return minYear + "-" + maxYear; 
	}
	static public boolean isDate(String strDate)  {
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
			dateTimeFormatter.parseDateTime(strDate);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	static public DateTime createDateFromStringDate(String strDate) {
		try {
			DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
			return dateTimeFormatter.parseDateTime(strDate);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    static public DateTime createDateFromStringDateTime(String strDate) {
    	DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd kk:mm:ss"); 
    	DateTime date = null;
		try {
			return dateTimeFormatter.parseDateTime(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
    }
	static public DateTime createDateMaxTime(DateTime date) {
		return date.withTime(23, 59, 59, 0);
	}
	static public DateTime createDateMinTime(DateTime date) {
		return date.withTime(0, 0, 0, 0);
	}
	static public DateTime getDateMinSeason(DateTime date) {
		if (date.getMonthOfYear() <= 6 && date.getDayOfMonth() <= 30) {
			return new DateTime(date.getYear() - 1, 7, 1, 0, 0, 0);
		}
		else {
			return new DateTime(date.getYear(), 7, 1, 0, 0, 0);
		}
	}	
	static public DateTime getDateMaxSeason(DateTime date) {
		if (date.getMonthOfYear() >= 7 && date.getDayOfMonth() >= 1) {
			return new DateTime(date.getYear() + 1, 6, 30, 23, 59, 59);
		}
		else {
			return new DateTime(date.getYear(), 6, 30, 23, 59, 59);
		}
	}
	static public DateTime getDateMinusOneDay(DateTime date) {
		return date.minusDays(1);
	}
}
