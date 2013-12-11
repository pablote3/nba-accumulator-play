package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Validation {
	static public boolean isDate(String date)  {
		try {
			new SimpleDateFormat("yyyy-MM-dd").parse(date);
			return true;
		} 
		catch (ParseException e) {
			return false;
		}
	}
	
	static public boolean isNumeric(String number)  {
		try {
			Integer.parseInt(number);
			return true;
		} 
		catch (NumberFormatException e) {
			return false;
		}
	}

}
