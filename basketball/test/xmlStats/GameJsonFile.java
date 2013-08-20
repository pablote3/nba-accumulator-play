package xmlStats;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.Status;
import models.XmlStats;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import utils.Utilities;

public class GameJsonFile {
    
	public static void main(String[] args) {
	   InputStream baseJson; 
	   try {
		   	String path = Utilities.getPropertyPath("config.basketball");
	        baseJson = new FileInputStream(path + "//test//GameJson.txt");
	        
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            XmlStats xmlStats = mapper.readValue(baseJson, XmlStats.class);
            System.out.println("away team =" + xmlStats.away_team.toString());
            System.out.println("home team =" + xmlStats.home_team.toString());
            xmlStats.event_information.setStatus(Status.completed);
            System.out.println("game info =" + xmlStats.event_information);  
	        for(int i = 0; i < xmlStats.away_period_scores.length; i++) {
	        	System.out.println("away box score" + i + " =" + xmlStats.away_period_scores[i]);
	        }
	        for(int i = 0; i < xmlStats.home_period_scores.length; i++) {
	        	System.out.println("home box score" + i + " =" + xmlStats.home_period_scores[i]);
	        }
            xmlStats.away_totals.setLocation(Location.away);
            xmlStats.home_totals.setLocation(Location.home);
	        if (xmlStats.away_totals.getPoints() > xmlStats.home_totals.getPoints()) {
	        	xmlStats.away_totals.setResult(Result.win);
	        	xmlStats.home_totals.setResult(Result.loss);
	        }
	        else {
	        	xmlStats.away_totals.setResult(Result.loss);
	        	xmlStats.home_totals.setResult(Result.win);
	        }
	        System.out.println("away box score =" + xmlStats.away_totals.toString());
	        System.out.println("home box score =" + xmlStats.home_totals.toString());
	        for(int i = 0; i < xmlStats.officials.length; i++) {
	        	System.out.println("official" + i + " =" + xmlStats.officials[i].toString());
	        }
	        baseJson.close(); 
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }

}