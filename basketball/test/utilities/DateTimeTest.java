package utilities;

import static org.fest.assertions.Assertions.assertThat;
import util.DateTimeUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

public class DateTimeTest {
	private static final String MIN_DATE = "2013-07-01";
	private static final String MAX_DATE = "2014-06-30";
	
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
    	LocalDate date = DateTimeUtil.getDateMinSeason(new LocalDate(MIN_DATE));
        assertThat(date).isEqualTo(new LocalDate(MIN_DATE));
    }
    
    @Test
    public void createDateMinSeason_Max() {
    	LocalDate date = DateTimeUtil.getDateMinSeason(new LocalDate(MAX_DATE));
    	assertThat(date).isEqualTo(new LocalDate(MIN_DATE));
    }
    
    @Test
    public void createDateMaxSeason_Min() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(MIN_DATE));
    	assertThat(date).isEqualTo(new LocalDate(MAX_DATE));
    }
    
    @Test
    public void createDateMaxSeason_Max() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(MAX_DATE));
    	assertThat(date).isEqualTo(new LocalDate(MAX_DATE));
    }
    
    @Test
    public void createSeasonFromDate_Max() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(MAX_DATE));
        assertThat(DateTimeUtil.getSeason(date)).isEqualTo("2013-14");
    }
    
    @Test
    public void createSeasonFromDate_Min() {
    	LocalDate date = DateTimeUtil.getDateMaxSeason(new LocalDate(MIN_DATE));
        assertThat(DateTimeUtil.getSeason(date)).isEqualTo("2013-14");
    }
    
    @Test
    public void createDateMinusOneDay_EndOfMonth() {
    	LocalDate date = DateTimeUtil.getDateMinusOneDay(new LocalDate(MIN_DATE));
        assertThat(date).isEqualTo(new LocalDate(2013, 6, 30));
    }
    
    @Test
    public void createDateMinusOneDay_BeginingOfMonth() {
    	LocalDate date = DateTimeUtil.getDateMinusOneDay(new LocalDate(MIN_DATE));
        assertThat(date).isEqualTo(new LocalDate(2013, 6, 30));
    }
    
    @Test
    public void calculateDateDiff_23Hours() {
    	DateTime minDate = new DateTime(2013, 3, 31, 19, 0, 0);
    	DateTime maxDate = new DateTime(2013, 4, 01, 18, 0, 0);
    	long days = DateTimeUtil.getDaysBetweenTwoDateTimes(minDate, maxDate);
        assertThat(days).isEqualTo(new Long(1));
    }
    
    @Test
    public void calculateDateDiff_25Hours() {
    	DateTime minDate = new DateTime(2013, 3, 31, 19, 0, 0);
    	DateTime maxDate = new DateTime(2013, 4, 01, 20, 0, 0);
    	long days = DateTimeUtil.getDaysBetweenTwoDateTimes(minDate, maxDate);
        assertThat(days).isEqualTo(new Long(1));
    }
    
    @Test
    public void calculateDateDiff_Over30Days() {
    	DateTime minDate = new DateTime(2013, 3, 31, 19, 0, 0);
    	DateTime maxDate = new DateTime(2013, 6, 01, 20, 0, 0);
    	long days = DateTimeUtil.getDaysBetweenTwoDateTimes(minDate, maxDate);
        assertThat(days).isEqualTo(new Long(0));
    }
    
    @Test
    public void calculateDateDiff_NullMinDate() {
    	DateTime minDate = null;
    	DateTime maxDate = new DateTime(2013, 6, 01, 20, 0, 0);
    	long days = DateTimeUtil.getDaysBetweenTwoDateTimes(minDate, maxDate);
        assertThat(days).isEqualTo(new Long(0));
    }
}
