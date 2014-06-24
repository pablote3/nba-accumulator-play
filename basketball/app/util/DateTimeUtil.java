package util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
	static public String getDisplayTime(DateTime date) {
		return date.toString(DateTimeFormat.forPattern("hh:mm a z"));
	}
	
	static public String getDisplayDateLong(DateTime date) {
		return date.toString(DateTimeFormat.forPattern("EEEEEEEEE, MMMMMMMMM d, y"));
	}
	
	static public String getDisplayDateMiddle(DateTime date) {
		return date.toString(DateTimeFormat.forPattern("MMMMMMMMM d, y"));
	}
	
	static public String getDisplayDateShort(LocalDate date) {
		return date.toString(DateTimeFormat.forPattern("MM-dd-yyyy"));
	}
	
	static public String getFindDateShort(LocalDate date) {
		return date.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
	}
	
	static public String getFindDateShort(DateTime date) {
		return date.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
	}
	
	static public String getFindDateTimeShort(DateTime date) {
		return date.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
	static public String getFindDateNaked(DateTime date) {
		return date.toString(DateTimeFormat.forPattern("yyyyMMdd"));
	}
	
	static public String getFindDateNaked(LocalDate date) {
		return date.toString(DateTimeFormat.forPattern("yyyyMMdd"));
	}
	
	static public String getSeason(LocalDate date) {
		LocalDate minDate = getDateMinSeason(date);
		String minYear = minDate.toString(DateTimeFormat.forPattern("yyyy"));
		
		LocalDate  maxDate = getDateMaxSeason(date);
		String maxYear = maxDate.toString(DateTimeFormat.forPattern("yy"));
		
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
	static public LocalDate createDateFromStringDate(String strDate) {
		try {
			return LocalDate.parse(strDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
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
	static public LocalDate getDateMinSeason(LocalDate date) {
		if (date.getMonthOfYear() <= 6 && date.getDayOfMonth() <= 30) {
			return new LocalDate(date.getYear() - 1, 7, 1);
		}
		else {
			return new LocalDate(date.getYear(), 7, 1);
		}
	}	
	static public LocalDate getDateMaxSeason(LocalDate date) {
		if (date.getMonthOfYear() >= 7 && date.getDayOfMonth() >= 1) {
			return new LocalDate(date.getYear() + 1, 6, 30);
		}
		else {
			return new LocalDate(date.getYear(), 6, 30);
		}
	}
	static public LocalDate getDateMinusOneDay(LocalDate date) {
		return date.minusDays(1);
	}
	static public LocalDate getLocalDateFromDateTime(DateTime date) {
		return new LocalDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
	}
}
