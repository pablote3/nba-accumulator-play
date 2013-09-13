package xmlStats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.Status;
import models.XmlStats;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import util.Utilities;

public class GameJsonUrl {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
    static final String ISO_8601_FMT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    static final SimpleDateFormat XMLSTATS_DATE = new SimpleDateFormat(ISO_8601_FMT);
    
	public static void main(String[] args) {
        InputStream in = null;
        try {
        	Properties props = Utilities.loadProperties("config.basketball");
        	if (props != null) {
            	String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
            	String event = "20120621-oklahoma-city-thunder-at-miami-heat.json";
	            URL url = new URL(urlBoxScore + event);
	            URLConnection connection = url.openConnection();
	            String accessToken = props.getProperty("xmlstats.accessToken");
	            String bearer = "Bearer " + accessToken;
	            String userAgentName = props.getProperty("xmlstats.userAgentName");
	            connection.setRequestProperty(AUTHORIZATION, bearer);
	            connection.setRequestProperty(USER_AGENT, userAgentName);
	            connection.setRequestProperty(ACCEPT_ENCODING, GZIP);
	            in = connection.getInputStream();
	            String encoding = connection.getContentEncoding();
	            if (GZIP.equals(encoding)) {
	                in = new GZIPInputStream(in);
	            }
	
	            if (in != null) {
	            	ObjectMapper mapper = new ObjectMapper();
	    	        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    	          
	    	        XmlStats xmlStats = mapper.readValue(in, XmlStats.class);
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
	    		    in.close(); 
	    		} 
        	}
        	else { 
        		//TODO improve error and exception handling
        	}
        }
        catch (FileNotFoundException e) {
    	    e.printStackTrace();
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 }
