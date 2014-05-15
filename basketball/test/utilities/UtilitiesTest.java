package utilities;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import util.Utilities;

public class UtilitiesTest {	
    @Test
    public void isValidNumber() {
    	assertThat(Utilities.isValidNumber("10")).isTrue();
    	assertThat(Utilities.isValidNumber("ten")).isFalse();
    }
    
    @Test
    public void padString() {
   		assertThat(Utilities.padString("Jump", 10)).isEqualTo("Jump      ");
    }
}
