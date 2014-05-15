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
}
