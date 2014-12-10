package actor;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
				standingsMap.put(teamStanding.getTeam().getKey(), new Record(teamStanding.getGamesWon(), teamStanding.getGamesPlayed(), (short)0, (short)0));
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
					int boxScoreId = game.getBoxScores().get(0).getTeam().equals(teamStanding.getTeam()) ? 1 : 0;
					opptTeamKey = game.getBoxScores().get(boxScoreId).getTeam().getKey();
					opptGamesWon = (short)(opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon());
					opptGamesPlayed = (short)(opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed());
					String opptGameDate = DateTimeUtil.getFindDateShort(game.getDate());
					System.out.println("  StandingsMap " + teamKey + " " + opptGameDate + " " + opptTeamKey + 
										" Games Won/Played: " + standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed());
				}				
				standingsMap.get(teamKey).setOpptGamesWon(opptGamesWon);
				standingsMap.get(teamKey).setOpptGamesPlayed(opptGamesPlayed);
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
			
			awayBoxScore = CalculateStrengthOfSchedule(gameDate, awayBoxScore, awayTeamKey);
			homeBoxScore = CalculateStrengthOfSchedule(gameDate, homeBoxScore, homeTeamKey);		

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
		
		private Record(Short gamesWon, Short gamesPlayed, Short opptGamesWon, Short opptGamesPlayed) {
			this.gamesWon = gamesWon;
			this.gamesPlayed = gamesPlayed;
			this.opptGamesWon = opptGamesWon;
			this.opptGamesPlayed = opptGamesPlayed;
		}

		private Short getGamesWon() {
			return gamesWon;
		}
		private void setGamesWon(Short gamesWon) {
			this.gamesWon = gamesWon;
		}

		private Short getGamesPlayed() {
			return gamesPlayed;
		}
		private void setGamesPlayed(Short gamesPlayed) {
			this.gamesPlayed = gamesPlayed;
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
	
	private BoxScore CalculateStrengthOfSchedule(String gameDate, BoxScore boxScore, String teamKey) {
		BoxScore opptBoxScore;
		Record record;
		Short opptHeadToHead;
		Short opptGamesWon = (short)0;
		Short opptGamesPlayed = (short)0;
		Short opptOpptGamesWon = (short)0;
		Short opptOpptGamesPlayed = (short)0;
		List<Game> awayCompleteGames = Game.findByDateTeamSeason(gameDate, teamKey, processingType);
		
		Map<String, Record> headToHeadMap = new HashMap<String, Record>();
		for (int i = 0; i < awayCompleteGames.size(); i++) {
			opptBoxScore = awayCompleteGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(teamKey) ? awayCompleteGames.get(i).getBoxScores().get(1) : awayCompleteGames.get(i).getBoxScores().get(0);
			String opptTeamKey = opptBoxScore.getTeam().getKey();
			opptHeadToHead = opptBoxScore.getResult().equals(Result.win) ? (short)1 : (short)0;
			record = headToHeadMap.get(opptTeamKey);
			if (record == null) {
				headToHeadMap.put(opptTeamKey, new Record(opptHeadToHead, (short)1, standingsMap.get(teamKey).getGamesWon(), standingsMap.get(teamKey).getGamesPlayed()));
			}
			else {
				record = headToHeadMap.get(opptTeamKey);
				record.setGamesWon((short)(record.getGamesWon() + opptHeadToHead));
				record.setGamesPlayed((short)(record.getGamesPlayed() + 1));
				record.setOpptGamesWon((short)(record.getOpptGamesWon() + standingsMap.get(teamKey).getGamesWon()));
				record.setOpptGamesPlayed((short)(record.getOpptGamesPlayed() + standingsMap.get(teamKey).getGamesPlayed()));
			}
		}
	
		for (int i = 0; i < awayCompleteGames.size(); i++) {
			opptBoxScore = awayCompleteGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(teamKey) ? awayCompleteGames.get(i).getBoxScores().get(1) : awayCompleteGames.get(i).getBoxScores().get(0);
			String opptTeamKey = opptBoxScore.getTeam().getKey();
			record = headToHeadMap.get(opptTeamKey);
			
			opptGamesWon = (short)(opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon() - record.getGamesWon());
			opptGamesPlayed = (short)(opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed() - record.getGamesPlayed());
			opptOpptGamesWon = (short)(opptOpptGamesWon + standingsMap.get(opptTeamKey).getOpptGamesWon() - record.getOpptGamesWon());
			opptOpptGamesPlayed = (short)(opptOpptGamesPlayed + standingsMap.get(opptTeamKey).getOpptGamesPlayed() - record.getOpptGamesPlayed());
	
			System.out.println("    OpptTeamStanding " + opptTeamKey);
			System.out.println("      Opponent Games Won/Played Sum: " + opptGamesWon + " - " + opptGamesPlayed + " = " + 
										standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed() + " minus " + record.getGamesWon() + " - " + record.getGamesPlayed());
			System.out.println("      OpptOppt Games Won/Played Sum: " + opptOpptGamesWon + " - " + opptOpptGamesPlayed + " = " + 
										standingsMap.get(opptTeamKey).getOpptGamesWon() + " - " + standingsMap.get(opptTeamKey).getOpptGamesPlayed() + " minus " + 
										record.getOpptGamesWon() + " - " + record.getOpptGamesPlayed());
		}
		
		boxScore.setOpptGamesWon(opptGamesWon);
		boxScore.setOpptGamesPlayed(opptGamesPlayed);
		boxScore.setOpptOpptGamesWon(opptOpptGamesWon);
		boxScore.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
		
		System.out.println("  HomeTeamStanding " + teamKey);
		System.out.println("    Opponent Games Won/Played = " + opptGamesWon + "-" + opptGamesPlayed);
		System.out.println("    Opponent Opponent Games Won/Played = " + opptOpptGamesWon + "-" + opptOpptGamesPlayed);
		BigDecimal opponentRecord = new BigDecimal(opptGamesWon).divide(new BigDecimal(opptGamesPlayed), 4, RoundingMode.HALF_UP);
		BigDecimal opponentOpponentRecord = new BigDecimal(opptOpptGamesWon).divide(new BigDecimal(opptOpptGamesPlayed), 4, RoundingMode.HALF_UP);
		System.out.println("    OpponentRecord = " + opponentRecord);
		System.out.println("    OpponentOpponentRecord = " + opponentOpponentRecord);
		System.out.println("    Strenghth Of Schedule = " + opponentRecord.multiply(new BigDecimal(2)).add(opponentOpponentRecord).divide(new BigDecimal(3), 4, RoundingMode.HALF_UP) + '\n');
		
		return boxScore;
	}
}