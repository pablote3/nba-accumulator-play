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

import json.xmlStats.BoxScorePlayerDTO;
import json.xmlStats.JsonHelper;
import json.xmlStats.NBABoxScore;
import models.BoxScore;
import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;
import models.Player;
import models.RosterPlayer;
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
        			homeBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.home_period_scores));
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
        			awayBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.away_period_scores));
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
	              
        			game.addBoxScore(homeBoxScore);
        			game.addBoxScore(awayBoxScore);
	              
        			Game.create(game, processingType);
        			Long gameId = game.getId();

        			Game createGame = Game.findById(gameId, processingType);
			    
        			assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
       				assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
       				assertThat(createGame.getGameOfficials().get(1).getCount()).isEqualTo((short)2);
	              	for (int i = 0; i < createGame.getBoxScores().size(); i++) {
	              		BoxScore boxScore = createGame.getBoxScores().get(i);
	              		if (boxScore.getLocation().equals(Location.away)) {
	              			assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)36);
	              			assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)26);
	              			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("OKC");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getRosterPlayer().getPlayer().getLastName()).isEqualTo("Durant");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getPoints()).isEqualTo((short)32);
	              		}
	              		else {
	              			assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)40);
	              			assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)31);
	              			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("MIA");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getRosterPlayer().getPlayer().getLastName()).isEqualTo("James");
	              			assertThat(boxScore.getBoxScorePlayers().get(0).getPoints()).isEqualTo((short)26);
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