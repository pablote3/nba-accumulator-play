package utilities;

import static org.fest.assertions.Assertions.assertThat;
import util.DateTimeUtil;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateTimeTest {	
    @Test
    public void displayTimeHalfHourAM() {
    	String s = DateTimeUtil.getDisplayTime(new DateTime(2013, 3, 30, 9, 30, 0));
    	assertThat(s).isEqualTo("09:30 AM PDT");
    }
    
    @Test
    public void displayTimeFullHourAM() {
    	String s = DateTimeUtil.getDisplayTime(new DateTime(2013, 3, 30, 9, 0, 0));
    	assertThat(s).isEqualTo("09:00 AM PDT");
    }
    
    @Test
    public void displayTimeHalfHourPM() {
    	String s = DateTimeUtil.getDisplayTime(new DateTime(2013, 3, 30, 19, 30, 0));
    	assertThat(s).isEqualTo("07:30 PM PDT");
    }
    
    @Test
    public void displayTimeFullHourPM() {
    	String s = DateTimeUtil.getDisplayTime(new DateTime(2013, 3, 30, 19, 0, 0));
    	assertThat(s).isEqualTo("07:00 PM PDT");
    }
    
    @Test
    public void displayDateLong() {
    	String s = DateTimeUtil.getDisplayDateLong(new DateTime(2013, 3, 30, 19, 0, 0));
        assertThat(s).isEqualTo("Saturday, March 30, 2013");
    }
    
    @Test
    public void displayDateMiddle() {
    	String s = DateTimeUtil.getDisplayDateMiddle(new DateTime(2013, 3, 30, 19, 0, 0));
        assertThat(s).isEqualTo("March 30, 2013");
    }
    
    @Test
    public void displayDateShort() {
    	String s = DateTimeUtil.getDisplayDateShort(new DateTime(2013, 3, 30, 19, 0, 0));
        assertThat(s).isEqualTo("03-30-2013");
    }
    
    @Test
    public void displayDateNaked() {
    	String s = DateTimeUtil.getFindDateNaked(new DateTime(2013, 3, 30, 19, 0, 0));
        assertThat(s).isEqualTo("20130330");
    }   
    
    @Test
    public void createDateMaxTime() {
    	DateTime date = DateTimeUtil.createDateMaxTime(new DateTime(2013, 3, 30, 0, 0, 0));
        assertThat(date).isEqualTo(new DateTime(2013, 03, 30, 23, 59, 59));
    }
    
    @Test
    public void createDateMinSeason_Min() {
    	DateTime date = DateTimeUtil.getDateMinSeason(new DateTime(2013, 7, 1, 0, 0, 0));
        assertThat(date).isEqualTo(new DateTime(2013, 7, 1, 0, 0, 0));
    }
    
    @Test
    public void createDateMinSeason_Max() {
    	DateTime date = DateTimeUtil.getDateMinSeason(new DateTime(2013, 6, 30, 23, 59, 59));
    	assertThat(date).isEqualTo(new DateTime(2012, 7, 1, 0, 0, 0));
    }
    
    @Test
    public void createDateMaxSeason_Min() {
    	DateTime date = DateTimeUtil.getDateMaxSeason(new DateTime(2013, 7, 1, 0, 0, 0));
    	assertThat(date).isEqualTo(new DateTime(2014, 6, 30, 23, 59, 59));
    }
    
    @Test
    public void createDateMaxSeason_Max() {
    	DateTime date = DateTimeUtil.getDateMaxSeason(new DateTime(2014, 6, 30, 23, 59, 59));
    	assertThat(date).isEqualTo(new DateTime(2014, 6, 30, 23, 59, 59));
    }
    
    @Test
    public void createSeasonFromDate_Max() {
    	DateTime date = DateTimeUtil.getDateMaxSeason(new DateTime(2013, 6, 30, 23, 59, 59));
        assertThat(DateTimeUtil.getSeason(date)).isEqualTo("2012-13");
    }
    
    @Test
    public void createSeasonFromDate_Min() {
    	DateTime date = DateTimeUtil.getDateMaxSeason(new DateTime(2013, 7, 1, 0, 0, 0));
        assertThat(DateTimeUtil.getSeason(date)).isEqualTo("2013-14");
    }
    
    @Test
    public void createDateMinusOneDay_EndOfMonth() {
    	DateTime date = DateTimeUtil.getDateMinusOneDay(new DateTime(2013, 7, 31, 0, 0, 0));
        assertThat(date).isEqualTo(new DateTime(2013, 7, 30, 0, 0, 0));
    }
    
    @Test
    public void createDateMinusOneDay_BeginingOfMonth() {
    	DateTime date = DateTimeUtil.getDateMinusOneDay(new DateTime(2013, 7, 1, 0, 0, 0));
        assertThat(date).isEqualTo(new DateTime(2013, 6, 30, 0, 0, 0));
    }
}
