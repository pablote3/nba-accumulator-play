package utilities;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;

import util.DateTime;

public class DateTimeTest {	
    @Test
    public void displayTimeHalfHourAM() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 09:30:00");
    	assertThat(DateTime.getDisplayTime(date)).isEqualTo("09:30 AM PDT");
    }
    
    @Test
    public void displayTimeFullHourAM() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 09:00:00");
        assertThat(DateTime.getDisplayTime(date)).isEqualTo("09:00 AM PDT");
    }
    
    @Test
    public void displayTimeHalfHourPM() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 19:30:00");
    	assertThat(DateTime.getDisplayTime(date)).isEqualTo("07:30 PM PDT");
    }
    
    @Test
    public void displayTimeFullHourPM() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayTime(date)).isEqualTo("07:00 PM PDT");
    }
    
    @Test
    public void displayDateLong() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayDateLong(date)).isEqualTo("Saturday, March 30, 2013");
    }
    
    @Test
    public void displayDateMiddle() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayDateMiddle(date)).isEqualTo("March 30, 2013");
    }
    
    @Test
    public void displayDateShort() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 19:00:00");
        assertThat(DateTime.getDisplayDateShort(date)).isEqualTo("03-30-2013");
    }
    
    @Test
    public void displayDateNaked() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 19:00:00");
        assertThat(DateTime.getFindDateNaked(date)).isEqualTo("20130330");
    }   
    
    @Test
    public void createDateMaxTime() {
    	Date date = DateTime.createDateFromStringDateTime("2013-03-30 00:00:00");
        assertThat(DateTime.createDateMaxTime(date)).isEqualTo(DateTime.createDateFromStringDateTime("2013-03-30 23:59:59"));
    }
    
    @Test
    public void createDateMinSeason_Min() {
    	Date date = DateTime.createDateFromStringDateTime("2013-07-01 00:00:00");
        assertThat(DateTime.getDateMinSeason(date)).isEqualTo(DateTime.createDateFromStringDateTime("2013-07-01 00:00:00"));
    }
    
    @Test
    public void createDateMinSeason_Max() {
    	Date date = DateTime.createDateFromStringDateTime("2013-06-30 23:59:59");
        assertThat(DateTime.getDateMinSeason(date)).isEqualTo(DateTime.createDateFromStringDateTime("2012-07-01 00:00:00"));
    }
    
    @Test
    public void createDateMaxSeason_Min() {
    	Date date = DateTime.createDateFromStringDate("2013-07-01 00:00:00");
    	Date minDate = DateTime.getDateMaxSeason(date);
        assertThat(minDate).isEqualTo(DateTime.createDateFromStringDateTime("2014-06-30 23:59:59"));
    }
    
    @Test
    public void createDateMaxSeason_Max() {
    	Date date = DateTime.createDateFromStringDate("2013-06-30");
    	Date maxDate = DateTime.getDateMaxSeason(date);
        assertThat(maxDate).isEqualTo(DateTime.createDateFromStringDateTime("2013-06-30 23:59:59"));
    }
}
