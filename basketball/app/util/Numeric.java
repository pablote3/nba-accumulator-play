package util;


public class Numeric {
	static public boolean isNumber(String number)  {
		try {
			Integer.parseInt(number);
			return true;
		} 
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	static public int createIntFromString(String number) {
		try {
			return Integer.parseInt(number);
		} 
		catch (NumberFormatException e) {
			return 0;
		}
	}
}
