package utilities;

import static org.fest.assertions.Assertions.assertThat;
import models.RosterPlayer;
import models.RosterPlayer.Position;

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
    
    @Test
    public void convertStringToEnum() {
    	Position position = RosterPlayer.Position.valueOf("C");
    	assertThat(position.equals(Position.C));
    }
    
    @Test
    public void standardizeMinutes_230() {
   		assertThat(Utilities.standardizeMinutes((short)230)).isEqualTo((short)240);
    }
    
    @Test
    public void standardizeMinutes_250() {
   		assertThat(Utilities.standardizeMinutes((short)250)).isEqualTo((short)240);
    }
    
    @Test
    public void standardizeMinutes_255() {
   		assertThat(Utilities.standardizeMinutes((short)255)).isEqualTo((short)265);
    }
    
    @Test
    public void standardizeMinutes_275() {
   		assertThat(Utilities.standardizeMinutes((short)275)).isEqualTo((short)265);
    }
    
    @Test
    public void standardizeMinutes_280() {
   		assertThat(Utilities.standardizeMinutes((short)280)).isEqualTo((short)290);
    }
    
    @Test
    public void standardizeMinutes_300() {
   		assertThat(Utilities.standardizeMinutes((short)300)).isEqualTo((short)290);
    }
    
    @Test
    public void standardizeMinutes_305() {
   		assertThat(Utilities.standardizeMinutes((short)305)).isEqualTo((short)315);
    }
    
    @Test
    public void standardizeMinutes_325() {
   		assertThat(Utilities.standardizeMinutes((short)325)).isEqualTo((short)315);
    }
    
    @Test
    public void standardizeMinutes_330() {
   		assertThat(Utilities.standardizeMinutes((short)330)).isEqualTo((short)340);
    }
    
    @Test
    public void standardizeMinutes_350() {
   		assertThat(Utilities.standardizeMinutes((short)350)).isEqualTo((short)340);
    }
    
    @Test
    public void standardizeMinutes_360() {
   		assertThat(Utilities.standardizeMinutes((short)0)).isEqualTo((short)00);
    }
}
