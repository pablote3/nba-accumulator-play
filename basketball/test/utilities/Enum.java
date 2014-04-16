package utilities;

import static org.fest.assertions.Assertions.assertThat;
import models.RosterPlayer;
import models.RosterPlayer.Position;

import org.junit.Test;

public class Enum {	
    @Test
    public void convertStringToEnum() {
    	Position position = RosterPlayer.Position.valueOf("C");
    	assertThat(position.equals(Position.C));
    }
}
