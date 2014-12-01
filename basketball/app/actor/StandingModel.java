package actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.BoxScore;
import models.BoxScore.Result;
import models.Game;
import models.Game.ProcessingType;
import models.Standing;
import util.DateTimeUtil;
import actor.ActorApi.ActiveStandings;
import actor.ActorApi.AdjustOpponent;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.LoadStandings;
import actor.ActorApi.RepeatGame;
import actor.ActorApi.RetrieveStandings;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class StandingModel extends UntypedActor {
	@SuppressWarnings("unused")
	private ActorRef listener;
	
	private final ActorRef standingXmlStats;
	private ActorRef controller;
	private Long gameId;
	private Map<String, Record> standingsMap;
	private ProcessingType processingType;
	
	public StandingModel(ActorRef listener) {
		this.listener = listener;
		standingXmlStats = getContext().actorOf(Props.create(StandingXmlStats.class, listener), "standingXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			standingXmlStats.tell(message, getSender());
		}
		else if(message instanceof LoadStandings) {
			controller = getSender();
			gameId = ((LoadStandings)message).gameId;
			String standingsDate = ((LoadStandings)message).date;
			RetrieveStandings rs = new RetrieveStandings(standingsDate);
			standingXmlStats.tell(rs, getSelf());
		}
		else if(message instanceof ActiveStandings) {
			ActiveStandings activeStandings = (ActiveStandings) message;
			List<Standing> standingsList = new ArrayList<Standing>(activeStandings.standings);
			List<Game> completeGames;
			standingsMap = new HashMap<String, Record>();
			Standing teamStanding;
			String opptTeamKey;
			Short opptGamesWon;
			Short opptGamesPlayed;
			Game game;
			
			for (int i = 0; i < standingsList.size(); i++) {
				teamStanding = standingsList.get(i);
				standingsMap.put(teamStanding.getTeam().getKey(), new Record(teamStanding.getGamesWon(), teamStanding.getGamesPlayed()));
			}
			
			String gameDate = DateTimeUtil.getFindDateNaked(standingsList.get(0).getDate());
			for (int j = 0; j < standingsList.size(); j++) {
				teamStanding = standingsList.get(j);
				opptGamesWon = (short)0;
				opptGamesPlayed = (short)0;
				completeGames = Game.findCompletedByDateTeamSeason(gameDate, teamStanding.getTeam().getKey(), processingType);
				for (int k = 0; k < completeGames.size(); k++) {
					game = completeGames.get(k);
					opptTeamKey = game.getBoxScores().get(0).getTeam().equals(teamStanding.getTeam()) ? game.getBoxScores().get(1).getTeam().getKey() : game.getBoxScores().get(0).getTeam().getKey();
					opptGamesWon = (short)(opptGamesWon + standingsMap.get(opptTeamKey).gamesWon);
					opptGamesPlayed = (short)(opptGamesPlayed + standingsMap.get(opptTeamKey).gamesPlayed);
				}
				teamStanding.setOpptGamesWon(opptGamesWon);
				teamStanding.setOpptGamesPlayed(opptGamesPlayed);
				Standing.create(teamStanding, processingType);
			}

			RepeatGame rg = new RepeatGame(gameId);
			controller.tell(rg, getSelf());
		}
		else if(message instanceof AdjustOpponent) {
			Game game = ((AdjustOpponent) message).game;
			String gameDate = DateTimeUtil.getFindDateNaked(game.getDate());

			BoxScore awayBoxScore = game.getBoxScores().get(0);
			String awayTeamKey = awayBoxScore.getTeam().getKey();
			BoxScore opptBoxScore;
			Short opptHeadToHead;
			Short opptGamesWon = (short)0;
			Short opptGamesPlayed = (short)0;
			Short opptOpptGamesWon = (short)0;
			Short opptOpptGamesPlayed = (short)0;
			List<Game> awayCompleteGames = Game.findCompletedByDateTeamSeason(gameDate, awayTeamKey, processingType);
			for (int i = 0; i < awayCompleteGames.size(); i++) {
				opptBoxScore = awayCompleteGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(awayTeamKey) ? awayCompleteGames.get(i).getBoxScores().get(1) : awayCompleteGames.get(i).getBoxScores().get(0);
				String opptTeamKey = opptBoxScore.getTeam().getKey();
				opptHeadToHead = opptBoxScore.getResult().equals(Result.win) ? (short)1 : (short)0;
				opptGamesWon = (short)(standingsMap.get(opptTeamKey).gamesWon - opptHeadToHead);
				opptGamesPlayed = (short)(standingsMap.get(opptTeamKey).gamesPlayed - 1);
				opptOpptGamesWon = (short)(opptOpptGamesWon - standingsMap.get(awayTeamKey).gamesWon);
				opptOpptGamesPlayed = (short)(opptOpptGamesPlayed - standingsMap.get(awayTeamKey).gamesPlayed);
			}
			awayBoxScore.setOpptGamesWon(opptGamesWon);
			awayBoxScore.setOpptGamesPlayed(opptGamesPlayed);
			awayBoxScore.setOpptOpptGamesWon(opptOpptGamesWon);
			awayBoxScore.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
			
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			String homeTeamKey = homeBoxScore.getTeam().getKey();
			opptGamesWon = (short)0;
			opptGamesPlayed = (short)0;
			opptOpptGamesWon = (short)0;
			opptOpptGamesPlayed = (short)0;
			List<Game> homeCompleteGames = Game.findCompletedByDateTeamSeason(gameDate, homeTeamKey, processingType);
			for (int i = 0; i < homeCompleteGames.size(); i++) {
				opptBoxScore = homeCompleteGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(homeTeamKey) ? homeCompleteGames.get(i).getBoxScores().get(1) : homeCompleteGames.get(i).getBoxScores().get(0);
				String opptTeamKey = opptBoxScore.getTeam().getKey();
				opptHeadToHead = opptBoxScore.getResult().equals(Result.win) ? (short)1 : (short)0;
				opptGamesWon = (short)(standingsMap.get(opptTeamKey).gamesWon - opptHeadToHead);
				opptGamesPlayed = (short)(standingsMap.get(opptTeamKey).gamesPlayed - 1);
				opptOpptGamesWon = (short)(opptOpptGamesWon - standingsMap.get(homeTeamKey).gamesWon);
				opptOpptGamesPlayed = (short)(opptOpptGamesPlayed - standingsMap.get(homeTeamKey).gamesPlayed);
			}
			homeBoxScore.setOpptGamesWon(opptGamesWon);
			homeBoxScore.setOpptGamesPlayed(opptGamesPlayed);
			homeBoxScore.setOpptOpptGamesWon(opptOpptGamesWon);
			homeBoxScore.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
			
			CompleteGame rs = new CompleteGame(game);
			controller.tell(rs, getSelf());
		}
		else {
			unhandled(message);
		}
	}
	private class Record {
		private Short gamesWon;
		private Short gamesPlayed;
		
		public Record(Short gamesWon, Short gamesPlayed) {
			this.gamesWon = gamesWon;
			this.gamesPlayed = gamesPlayed;
		}
	}
}