package model;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.entity.Official;

import org.junit.Test;

import com.avaje.ebean.Page;
import com.avaje.ebean.ValidationException;

public class ModelOfficialTest {    
    @Test
    public void findOfficialsAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Official> officials = Official.findAll();
        	  assertThat(officials.size()).isEqualTo(61);
          }
        });
    }
    
	@Test
    public void findOfficialsActive() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Official> officials = Official.findActive(true);
        	  assertThat(officials.size()).isEqualTo(61);
          }
        });
    }
	
    @Test
    public void findOfficialByName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Official official = Official.findByName("Garretson", "Ron");
              assertThat(official.getNumber()).isEqualTo("10");
              assertThat(official.getActive()).isTrue();
          }
        });
    }
    
    @Test
    public void findOfficialFinderByNumber() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Official official = Official.find.where().eq("number", "10").findUnique();
              assertThat(official.getFirstName()).isEqualTo("Ron");
              assertThat(official.getLastName()).isEqualTo("Garretson");
              assertThat(official.getActive()).isTrue();
          }
        });
    }
    
    @Test
    public void createOfficial() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Official official = new Official();
        	  official.setNumber("99");
        	  official.setLastName("Hansen");
        	  official.setFirstName("Chris");
        	  
              Date date = null;
              try {
            	  date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2012-11-05");
              } catch (ParseException e) {
            	  e.printStackTrace();
              }
        	  official.setFirstGame(date);
        	  official.setActive(false);
              
        	  Official.create(official);
              
        	  Official createOfficial = Official.findByName("Hansen", "Chris");
              assertThat(createOfficial.getNumber()).isEqualTo("99");
              assertThat(createOfficial.getActive()).isFalse();
              Official.delete(createOfficial.getId());
          }
        });
    }
    
    @Test
    public void updateOfficial() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Official official = Official.findByName("Palmer", "Violet");
        	  official.setActive(false);
        	  official.update();
              
        	  Official updateOfficial = Official.findByName("Palmer", "Violet");
              assertThat(updateOfficial.getActive()).isFalse();
              updateOfficial.setActive(true);
              updateOfficial.update();
          }
        });
    }
    
    @Test
    public void updateOfficialValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  try {
        		  Official official = Official.findByName("Palmer", "Violet");
        		  official.setFirstName(null);
        		  official.update();
        	  } catch (ValidationException e) {
        		  assertThat(e.getInvalid().getChildren()[0].getPropertyName().equalsIgnoreCase("firstName"));
        		  assertThat(e.getInvalid().getChildren()[0].getValidatorKey().equalsIgnoreCase("notnull"));
        	  }
          }
        });
    }

    @Test
    public void paginationOfficials() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Official> officials = Official.page(0, 15, "firstName", "ASC", "");
               assertThat(officials.getTotalRowCount()).isEqualTo(61);
               assertThat(officials.getList().size()).isEqualTo(15);
           }
        });
    }
    
    @Test
    public void pagnationOfficialsFilter() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Official> officials = Official.page(0, 15, "lastName", "ASC", "Crawford");
               assertThat(officials.getTotalRowCount()).isEqualTo(2);
               assertThat(officials.getList().size()).isEqualTo(2);
           }
        });
    }
}
