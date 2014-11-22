package actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.BoxScore;
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
			Standing awayStanding = Standing.findByDateTeam(gameDate, awayTeamKey, processingType);
			List<Game> awayCompleteGames = Game.findCompletedByDateTeamSeason(gameDate, awayTeamKey, processingType);
			
			
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			String homeTeamKey = homeBoxScore.getTeam().getKey();
			Standing homeStanding = Standing.findByDateTeam(gameDate, homeTeamKey, processingType);
			
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