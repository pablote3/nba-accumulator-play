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
    public void displayTimeHalfHour() {
    	Date date = parseDate("2013-03-30 19:30:00");
    	System.out.println(DateTime.getDisplayTime(date));
    	assertThat(DateTime.getDisplayTime(date)).isEqualTo("19:30 PM PDT");
    }
    
    @Test
    public void displayTimeFullHour() {
    	Date date = parseDate("2013-03-30 19:00:00");
    	System.out.println(DateTime.getDisplayTime(date));
        assertThat(DateTime.getDisplayTime(date)).isEqualTo("19:00 PM PDT");
    }
    
    public Date parseDate(String stringDate) {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH); 
    	Date date = null;
		try {
			date = simpleDateFormat.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
    }
}
