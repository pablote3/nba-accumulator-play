package util;


public class Utilities {
	static public boolean isValidNumber(String number)  {
		try {
			if (Integer.parseInt(number) >= 0)
				return true;
			else
				return false;
		} 
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	static public String padString(String text, int length)  {
		return String.format("%1$-" + length + "s", text);
	}
	
	static public Short standardizeMinutes(Short minutes) {
		if (minutes >= 230 && minutes <= 250)
			return (short)240;
		else if (minutes >= 255 && minutes <= 275)
			return (short)265;
		else if (minutes >= 280 && minutes <= 300)
			return (short)290;
		else if (minutes >= 305 && minutes <= 325)
			return (short)315;
		else if (minutes >= 330 && minutes <= 350)
			return (short)340;
		else
			return (short)0;
	}
}
