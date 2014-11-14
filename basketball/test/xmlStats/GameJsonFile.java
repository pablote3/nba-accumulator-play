package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import json.xmlStats.BoxScorePlayerDTO;
import json.xmlStats.JsonHelper;
import json.xmlStats.NBABoxScore;
import json.xmlStats.Standings;
import models.BoxScore;
import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;
import models.Player;
import models.RosterPlayer;
import models.Standing;
import models.Team;

import org.joda.time.LocalDate;
import org.junit.Test;

import util.DateTimeUtil;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class GameJsonFile {

    @Test
    public void createGame() {
        running(fakeApplication(), new Runnable() {
        	public void run() {
        		InputStream baseJson; 
        		try {
        			Path path =  Paths.get(System.getProperty("config.test")).resolve("20020621-oklahoma-city-thunder-at-miami-heat.json");
        			File file = path.toFile();
        			baseJson = new FileInputStream(file);
		        
        			ObjectMapper mapper = new ObjectMapper();
        			mapper.registerModule(new JodaModule());        			
        			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);       			
        			NBABoxScore xmlStats = mapper.readValue(baseJson, NBABoxScore.class);
	              
        			ProcessingType processingType = ProcessingType.online;
             
        			Game game = new Game();
        			game.setDate(xmlStats.event_information.getDate());
        			game.setStatus(Status.completed);
        			game.setSeasonType(xmlStats.event_information.getSeasonType());	              
        			game.setGameOfficials(JsonHelper.getGameOfficials(xmlStats.officials, processingType));
        			
        			LocalDate gameDate = DateTimeUtil.getLocalDateFromDateTime(game.getDate());
	              
        			BoxScorePlayerDTO homeBoxScorePlayerDTO = xmlStats.home_stats[0];
        			Team homeTeam = Team.findByAbbr(homeBoxScorePlayerDTO.getTeam_abbreviation(), processingType);
        			Player homePlayer = new Player();
        			homePlayer.setLastName(homeBoxScorePlayerDTO.getLast_name());
        			homePlayer.setFirstName(homeBoxScorePlayerDTO.getFirst_name());
        			homePlayer.setDisplayName(homeBoxScorePlayerDTO.getFirst_name() + " " + homeBoxScorePlayerDTO.getLast_name());
        			Player.create(homePlayer, processingType);	

        			RosterPlayer homeRosterPlayer = new RosterPlayer();
        			homeRosterPlayer.setPlayer(homePlayer);
        			homeRosterPlayer.setTeam(homeTeam);
        			homeRosterPlayer.setFromDate(gameDate);
        			homeRosterPlayer.setToDate(DateTimeUtil.getDateMaxSeason(gameDate));
        			homeRosterPlayer.setPosition(RosterPlayer.Position.valueOf(homeBoxScorePlayerDTO.getPosition()));
					RosterPlayer.create(homeRosterPlayer, processingType);
	              
        			BoxScore homeBoxScore = new BoxScore();
        			homeBoxScore.setLocation(Location.home);
        			homeBoxScore.setTeam(homeTeam);
        			
		    	    int[] homePeriodScores = xmlStats.home_period_scores;
		    	    homeBoxScore.setPointsPeriod1((short)homePeriodScores[0]);
		    	    homeBoxScore.setPointsPeriod2((short)homePeriodScores[1]);
		    	    homeBoxScore.setPointsPeriod3((short)homePeriodScores[2]);
		    	    homeBoxScore.setPointsPeriod4((short)homePeriodScores[3]);
		    	    if(homePeriodScores.length > 4)
		    	    	homeBoxScore.setPointsPeriod5((short)homePeriodScores[4]);
		    	    if(homePeriodScores.length > 5)
		    	    	homeBoxScore.setPointsPeriod6((short)homePeriodScores[5]);
		    	    if(homePeriodScores.length > 6)
		    	    	homeBoxScore.setPointsPeriod7((short)homePeriodScores[6]);
		    	    if(homePeriodScores.length > 7)
		    	    	homeBoxScore.setPointsPeriod8((short)homePeriodScores[7]);
        			
        			JsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);
        			homeBoxScore.setBoxScorePlayers(JsonHelper.getBoxScorePlayers(xmlStats.home_stats, DateTimeUtil.getFindDateShort(gameDate), processingType));
        			
        			BoxScorePlayerDTO awayBoxScorePlayerDTO = xmlStats.away_stats[0];
        			Team awayTeam = Team.findByAbbr(awayBoxScorePlayerDTO.getTeam_abbreviation(), processingType);
        			Player awayPlayer = new Player();
        			awayPlayer.setLastName(awayBoxScorePlayerDTO.getLast_name());
        			awayPlayer.setFirstName(awayBoxScorePlayerDTO.getFirst_name());
        			awayPlayer.setDisplayName(awayBoxScorePlayerDTO.getFirst_name() + " " + awayBoxScorePlayerDTO.getLast_name());
        			Player.create(awayPlayer, processingType);	

        			RosterPlayer awayRosterPlayer = new RosterPlayer();
        			awayRosterPlayer.setPlayer(awayPlayer);
        			awayRosterPlayer.setTeam(awayTeam);
        			awayRosterPlayer.setFromDate(gameDate);
        			awayRosterPlayer.setToDate(DateTimeUtil.getDateMaxSeason(gameDate));
        			awayRosterPlayer.setPosition(RosterPlayer.Position.valueOf(awayBoxScorePlayerDTO.getPosition()));
					RosterPlayer.create(awayRosterPlayer, processingType);
	              
        			BoxScore awayBoxScore = new BoxScore();
        			awayBoxScore.setLocation(Location.away);
        			awayBoxScore.setTeam(awayTeam);
        			
		    	    int[] awayPeriodScores = xmlStats.away_period_scores;
		    	    awayBoxScore.setPointsPeriod1((short)awayPeriodScores[0]);
		    	    awayBoxScore.setPointsPeriod2((short)awayPeriodScores[1]);
		    	    awayBoxScore.setPointsPeriod3((short)awayPeriodScores[2]);
		    	    awayBoxScore.setPointsPeriod4((short)awayPeriodScores[3]);
		    	    if(awayPeriodScores.length > 4)
		    	    	awayBoxScore.setPointsPeriod5((short)awayPeriodScores[4]);
		    	    if(awayPeriodScores.length > 5)
		    	    	awayBoxScore.setPointsPeriod6((short)awayPeriodScores[5]);
		    	    if(awayPeriodScores.length > 6)
		    	    	awayBoxScore.setPointsPeriod7((short)awayPeriodScores[6]);
		    	    if(awayPeriodScores.length > 7)
		    	    	awayBoxScore.setPointsPeriod8((short)awayPeriodScores[7]);
        			
        			JsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);
        			awayBoxScore.setBoxScorePlayers(JsonHelper.getBoxScorePlayers(xmlStats.away_stats, DateTimeUtil.getFindDateShort(gameDate), processingType));

        			if (xmlStats.away_totals.getPoints() > xmlStats.home_totals.getPoints()) {
        				homeBoxScore.setResult(Result.loss);
        				awayBoxScore.setResult(Result.win);
        			}
        			else {
        				homeBoxScore.setResult(Result.win);
        				awayBoxScore.setResult(Result.loss);
        			}
        			
        			path =  Paths.get(System.getProperty("config.test")).resolve("20020621-standings.json");
        			file = path.toFile();
        			baseJson = new FileInputStream(file);
        			
        			mapper = new ObjectMapper();
        			mapper.registerModule(new JodaModule());        			
        			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);       			
        			Standings xmlStandings = mapper.readValue(baseJson, Standings.class);
        			ArrayList<Standing> standings = new ArrayList<Standing>(Arrays.asList(xmlStandings.standing));
        			
        			for (int i = 0; i < standings.size(); i++)  {
        				String homeTeamKey = homeBoxScore.getTeam().getKey();
        				if (standings.get(i).getTeamKey().equals(homeTeamKey))  {
        					Standing standing = standings.get(i);
        					standing.setGameDate(game.getDate());
        					standing.setOpptOpptGamesPlayed(3);
        					standing.setOpptOpptWins(0);
        					standing.setAvgOpptOpptWinPercentage((float).75);
        					homeBoxScore.getStandings().add(standing);
        					break;
        				}
        			}
        			
        			for (int i = 0; i < standings.size(); i++)  {
        				String awayTeamKey = awayBoxScore.getTeam().getKey();
        				if (standings.get(i).getTeamKey().equals(awayTeamKey))  {
        					Standing standing = standings.get(i);
        					standing.setGameDate(game.getDate());
        					standing.setOpptOpptGamesPlayed(2);
        					standing.setOpptOpptWins(2);
        					standing.setAvgOpptOpptWinPercentage((float).35);
        					awayBoxScore.getStandings().add(standing);
        					break;
        				}
        			}
	              
        			game.addBoxScore(homeBoxScore);
        			game.addBoxScore(awayBoxScore);
	              
        			Game.create(game, processingType);
        			Long gameId = game.getId();

        			Game createGame = Game.findById(gameId, processingType);
			    
        			assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
       				assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
       				assertThat(createGame.getGameOfficials().get(1).getCounter()).isEqualTo((short)2);
	              	for (int i = 0; i < createGame.getBoxScores().size(); i++) {
	              		BoxScore boxScore = createGame.getBoxScores().get(i);
	              		if (boxScore.getLocation().equals(Location.away)) {
	              			assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)36);
	              			assertThat(boxScore.getPointsPeriod1()).isEqualTo((short)26);
	              			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("OKC");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getRosterPlayer().getPlayer().getLastName()).isEqualTo("Durant");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getPoints()).isEqualTo((short)32);
	              			assertThat(boxScore.getStandings().get(0).getGamesBack()).isEqualTo((float)0.5);
	              		}
	              		else {
	              			assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)40);
	              			assertThat(boxScore.getPointsPeriod1()).isEqualTo((short)31);
	              			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("MIA");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getRosterPlayer().getPlayer().getLastName()).isEqualTo("James");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getPoints()).isEqualTo((short)26);
	              			assertThat(boxScore.getStandings().get(0).getGamesBack()).isEqualTo((float)1.5);
	              		}
	              	}
	              
        			Game.delete(createGame.getId(), processingType);
	              
        			RosterPlayer.delete(awayRosterPlayer, processingType);	              
        			Player.delete(awayPlayer, processingType);
        			
        			RosterPlayer.delete(homeRosterPlayer, processingType);	              
        			Player.delete(homePlayer, processingType);

        		} catch (FileNotFoundException e) {
        			e.printStackTrace();
      	      	} catch (IOException e) {
      	      		e.printStackTrace();
      	      	}
        	}
        });
    }
}