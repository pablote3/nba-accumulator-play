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
    public void displayTimeHalfHourAM() {
    	Date date = parseDate("2013-03-30 09:30:00");
    	assertThat(DateTime.getDisplayTime(date)).isEqualTo("09:30 AM PDT");
    }
    
    @Test
    public void displayTimeFullHourAM() {
    	Date date = parseDate("2013-03-30 09:00:00");
        assertThat(DateTime.getDisplayTime(date)).isEqualTo("09:00 AM PDT");
    }
    
    @Test
    public void displayTimeHalfHourPM() {
    	Date date = parseDate("2013-03-30 19:30:00");
    	assertThat(DateTime.getDisplayTime(date)).isEqualTo("07:30 PM PDT");
    }
    
    @Test
    public void displayTimeFullHourPM() {
    	Date date = parseDate("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayTime(date)).isEqualTo("07:00 PM PDT");
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
