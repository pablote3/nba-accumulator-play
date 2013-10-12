package utilities;

import static org.fest.assertions.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import org.junit.Test;

import util.DateTime;

public class DateTimeTest {

    @Test
    public void displayTime() {
    	String stringDate = "2013-03-30 19:30:00";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);    	
    	
    	Date date = null;
		try {
			date = simpleDateFormat.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
        String displayTime = DateTime.getDisplayTime(date);
        System.out.println(displayTime);

        assertThat(displayTime.equalsIgnoreCase("19:30 PM"));
    }
}
