package utilities;

import static org.fest.assertions.Assertions.assertThat;
import util.DateTimeUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
    	String s = DateTimeUtil.getDisplayDateShort(new LocalDate(2013, 3, 30));
        assertThat(s).isEqualTo("03-30-2013");
    }
    
    @Test
    public void displayDateNaked() {
    	String s = DateTimeUtil.getFindDateNaked(new DateTime(2013, 3, 30, 0, 0, 0));
        assertThat(s).isEqualTo("20130330");
    }
    
    @Test
    public void createDateMinSeason_Min() {
    	LocalDate date = DateTimeUtil.getDateMinSeason(new LocalDate(2013, 7, 1));
        assertThat(date).isEqualTo(new LocalDate(2013, 7, 1));
    }
    
    @Test
    public void createDateMinSeason_Max() {
    	LocalDate date = DateTimeUtil.getDateMinSeason(new LocalDate(2013, 6, 30));
    	assertThat(date).isEqualTo(new LocalDate(2012, 7, 1));
    }
    
    @Test
    public void createDateMaxSeason_Min() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(2013, 7, 1));
    	assertThat(date).isEqualTo(new LocalDate(2014, 6, 30));
    }
    
    @Test
    public void createDateMaxSeason_Max() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(2014, 6, 30));
    	assertThat(date).isEqualTo(new LocalDate(2014, 6, 30));
    }
    
    @Test
    public void createSeasonFromDate_Max() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(2013, 6, 30));
        assertThat(DateTimeUtil.getSeason(date)).isEqualTo("2012-13");
    }
    
    @Test
    public void createSeasonFromDate_Min() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(2013, 7, 1));
        assertThat(DateTimeUtil.getSeason(date)).isEqualTo("2013-14");
    }
    
    @Test
    public void createDateMinusOneDay_EndOfMonth() {
    	LocalDate date = DateTimeUtil.getDateMinusOneDay(new LocalDate(2013, 7, 31));
        assertThat(date).isEqualTo(new LocalDate(2013, 7, 30));
    }
    
    @Test
    public void createDateMinusOneDay_BeginingOfMonth() {
    	LocalDate date = DateTimeUtil.getDateMinusOneDay(new LocalDate(2013, 7, 1));
        assertThat(date).isEqualTo(new LocalDate(2013, 6, 30));
    }
}
