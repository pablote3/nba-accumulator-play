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
    
    @Test
    public void displayDateLong() {
    	Date date = parseDate("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayDateLong(date)).isEqualTo("Saturday, March 30, 2013");
    }
    
    @Test
    public void displayDateMiddle() {
    	Date date = parseDate("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayDateMiddle(date)).isEqualTo("March 30, 2013");
    }
    
    @Test
    public void displayDateShort() {
    	Date date = parseDate("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayDateShort(date)).isEqualTo("03-30-2013");
    }
    
    @Test
    public void displayDateNaked() {
    	Date date = parseDate("2013-03-30 19:00:00");
        assertThat(DateTime.getFindDateNaked(date)).isEqualTo("20130330");
    }   
    
    @Test
    public void createDateMaxTime() {
    	Date date = parseDate("2013-03-30 00:00:00");
        assertThat(DateTime.createDateMaxTime(date)).isEqualTo(parseDate("2013-03-30 23:59:59"));
    }
    
    @Test
    public void createDateMinSeason_Min() {
    	Date date = parseDate("2013-07-01 00:00:00");
        assertThat(DateTime.getDateMinSeason(date)).isEqualTo(parseDate("2013-07-01 00:00:00"));
    }
    
    @Test
    public void createDateMinSeason_Max() {
    	Date date = parseDate("2013-06-30 23:59:59");
        assertThat(DateTime.getDateMinSeason(date)).isEqualTo(parseDate("2012-07-01 00:00:00"));
    }
    
    @Test
    public void createDateMaxSeason_Min() {
    	Date date = parseDate("2013-07-01 00:00:00");
        assertThat(DateTime.getDateMaxSeason(date)).isEqualTo(parseDate("2014-06-30 23:59:59"));
    }
    
    @Test
    public void createDateMaxSeason_Max() {
    	Date date = parseDate("2013-06-30 23:59:59");
        assertThat(DateTime.getDateMaxSeason(date)).isEqualTo(parseDate("2013-06-30 23:59:59"));
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
