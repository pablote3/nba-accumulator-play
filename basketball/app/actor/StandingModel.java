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
			
			String gameDate = DateTimeUtil.getFindDateShort(standingsList.get(0).getDate());
			for (int j = 0; j < standingsList.size(); j++) {
				teamStanding = standingsList.get(j);
				String teamKey = teamStanding.getTeam().getKey();
				opptGamesWon = (short)0;
				opptGamesPlayed = (short)0;
				completeGames = Game.findByDateTeamSeason(gameDate, teamKey, processingType);
				for (int k = 0; k < completeGames.size(); k++) {
					game = completeGames.get(k);
					opptTeamKey = game.getBoxScores().get(0).getTeam().equals(teamStanding.getTeam()) ? game.getBoxScores().get(1).getTeam().getKey() : game.getBoxScores().get(0).getTeam().getKey();
					opptGamesWon = (short)(opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon());
					opptGamesPlayed = (short)(opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed());
					System.out.println("StandingsMap teamKey = " + teamKey + " opptTeam = " + opptTeamKey + " Games Won/Played: " + standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed());
				}
				
				standingsMap.get(teamKey).setOpptGamesWon(opptGamesWon);
				standingsMap.get(teamKey).setOpptGamesPlayed(opptGamesPlayed);
				
				//do I still need these fields on standings table???
				teamStanding.setOpptGamesWon(opptGamesWon);
				teamStanding.setOpptGamesPlayed(opptGamesPlayed);
				Standing.create(teamStanding, processingType);
			}

			RepeatGame rg = new RepeatGame(gameId);
			controller.tell(rg, getSelf());
		}
		else if(message instanceof AdjustOpponent) {
			Game game = ((AdjustOpponent) message).game;
			String gameDate = DateTimeUtil.getFindDateShort(game.getDate());

			BoxScore awayBoxScore = game.getBoxScores().get(0);
			String awayTeamKey = awayBoxScore.getTeam().getKey();
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			String homeTeamKey = homeBoxScore.getTeam().getKey();
			BoxScore opptBoxScore;
			Short opptHeadToHead;
			Short opptGamesWon = (short)0;
			Short opptGamesPlayed = (short)0;
			Short opptOpptGamesWon = (short)0;
			Short opptOpptGamesPlayed = (short)0;
			List<Game> awayCompleteGames = Game.findByDateTeamSeason(gameDate, awayTeamKey, processingType);
			for (int i = 0; i < awayCompleteGames.size(); i++) {
				opptBoxScore = awayCompleteGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(awayTeamKey) ? awayCompleteGames.get(i).getBoxScores().get(1) : awayCompleteGames.get(i).getBoxScores().get(0);
				String opptTeamKey = opptBoxScore.getTeam().getKey();
				opptHeadToHead = opptBoxScore.getResult().equals(Result.win) ? (short)1 : (short)0;
				opptGamesWon = (short)(opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon() - opptHeadToHead);
				opptGamesPlayed = (short)(opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed() - 1);
				opptOpptGamesWon = (short)(opptOpptGamesWon + standingsMap.get(opptTeamKey).getOpptGamesWon() - standingsMap.get(awayTeamKey).getGamesWon());
				opptOpptGamesPlayed = (short)(opptOpptGamesPlayed + standingsMap.get(opptTeamKey).getOpptGamesPlayed() - standingsMap.get(awayTeamKey).getGamesPlayed());

				System.out.println("    OpptTeamStanding " + opptTeamKey);
				System.out.println("      Opponent Games Won/Played Sum: " + opptGamesWon + " - " + opptGamesPlayed + " = " + 
											standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed() + " minus " + 
											opptHeadToHead + " - 1");
				System.out.println("      OpptOppt Games Won/Played Sum: " + opptOpptGamesWon + " - " + opptOpptGamesPlayed + " = " + 
											standingsMap.get(opptTeamKey).getOpptGamesWon() + " - " + standingsMap.get(opptTeamKey).getOpptGamesPlayed() + " minus " + 
											standingsMap.get(awayTeamKey).getGamesWon() + " - " + standingsMap.get(awayTeamKey).getGamesPlayed());
			}

			awayBoxScore.setOpptGamesWon(opptGamesWon);
			awayBoxScore.setOpptGamesPlayed(opptGamesPlayed);
			awayBoxScore.setOpptOpptGamesWon(opptOpptGamesWon);
			awayBoxScore.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
			
			System.out.println("  AwayTeamStanding " + awayTeamKey);
			System.out.println("    Opponent Games Won/Played = " + opptGamesWon + "-" + opptGamesPlayed);
			System.out.println("    Opponent Opponent Games Won/Played = " + opptOpptGamesWon + "-" + opptOpptGamesPlayed);
			
			opptGamesWon = (short)0;
			opptGamesPlayed = (short)0;
			opptOpptGamesWon = (short)0;
			opptOpptGamesPlayed = (short)0;
			List<Game> homeCompleteGames = Game.findByDateTeamSeason(gameDate, homeTeamKey, processingType);
			for (int i = 0; i < homeCompleteGames.size(); i++) {
				opptBoxScore = homeCompleteGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(homeTeamKey) ? homeCompleteGames.get(i).getBoxScores().get(1) : homeCompleteGames.get(i).getBoxScores().get(0);
				String opptTeamKey = opptBoxScore.getTeam().getKey();
				opptHeadToHead = opptBoxScore.getResult().equals(Result.win) ? (short)1 : (short)0;
				opptGamesWon = (short)(standingsMap.get(opptTeamKey).getGamesWon() - opptHeadToHead);
				opptGamesPlayed = (short)(standingsMap.get(opptTeamKey).getGamesPlayed() - 1);
				opptOpptGamesWon = (short)(opptOpptGamesWon + standingsMap.get(opptTeamKey).getOpptGamesWon() - standingsMap.get(homeTeamKey).getGamesWon());
				opptOpptGamesPlayed = (short)(opptOpptGamesPlayed + standingsMap.get(opptTeamKey).getOpptGamesPlayed() - standingsMap.get(homeTeamKey).getGamesPlayed());
				
				System.out.println("    OpptTeamStanding " + opptTeamKey);
				System.out.println("      Opponent Games Won/Played Sum: " + opptGamesWon + " - " + opptGamesPlayed + " = " + 
						standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed() + " minus " + 
						opptHeadToHead + " - 1");
				System.out.println("      OpptOppt Games Won/Played Sum: " + opptOpptGamesWon + " - " + opptOpptGamesPlayed + " = " + 
						standingsMap.get(opptTeamKey).getOpptGamesWon() + " - " + standingsMap.get(opptTeamKey).getOpptGamesPlayed() + " minus " + 
						standingsMap.get(homeTeamKey).getGamesWon() + " - " + standingsMap.get(homeTeamKey).getGamesPlayed());
			}
			
			homeBoxScore.setOpptGamesWon(opptGamesWon);
			homeBoxScore.setOpptGamesPlayed(opptGamesPlayed);
			homeBoxScore.setOpptOpptGamesWon(opptOpptGamesWon);
			homeBoxScore.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
			
			System.out.println("  HomeTeamStanding " + homeTeamKey);
			System.out.println("    Opponent Games Won/Played = " + opptGamesWon + "-" + opptGamesPlayed);
			System.out.println("    Opponent Opponent Games Won/Played = " + opptOpptGamesWon + "-" + opptOpptGamesPlayed);

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
		private Short opptGamesWon;
		private Short opptGamesPlayed;
		
		private Record(Short gamesWon, Short gamesPlayed) {
			this.gamesWon = gamesWon;
			this.gamesPlayed = gamesPlayed;
		}

		private Short getGamesWon() {
			return gamesWon;
		}

		private Short getGamesPlayed() {
			return gamesPlayed;
		}

		private Short getOpptGamesWon() {
			return opptGamesWon;
		}
		private void setOpptGamesWon(Short opptGamesWon) {
			this.opptGamesWon = opptGamesWon;
		}

		private Short getOpptGamesPlayed() {
			return opptGamesPlayed;
		}
		private void setOpptGamesPlayed(Short opptGamesPlayed) {
			this.opptGamesPlayed = opptGamesPlayed;
		}
		
	}
}