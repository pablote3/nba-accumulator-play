package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTime {
	static public String getDisplayTime(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm a z", Locale.ENGLISH);
		return simpleDateFormat.format(date);
	}
}
