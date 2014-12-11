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
					int opptBoxScoreId = game.getBoxScores().get(0).getTeam().equals(teamStanding.getTeam()) ? 1 : 0;
					opptTeamKey = game.getBoxScores().get(opptBoxScoreId).getTeam().getKey();
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
		Short opptHeadToHead;
		Short opptGamesWon = (short)0;
		Short opptGamesPlayed = (short)0;
		Short opptOpptGamesWon = (short)0;
		Short opptOpptGamesPlayed = (short)0;
		List<Game> completeGames = Game.findByDateTeamSeason(gameDate, teamKey, processingType);
		
		Map<String, Record> headToHeadMap = new HashMap<String, Record>();
		for (int i = 0; i < completeGames.size(); i++) {
			int opptBoxScoreId = completeGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(teamKey) ? 1 : 0;
			opptBoxScore = completeGames.get(i).getBoxScores().get(opptBoxScoreId);
			String opptTeamKey = opptBoxScore.getTeam().getKey();
			opptHeadToHead = opptBoxScore.getResult() != null && opptBoxScore.getResult().equals(Result.win) ? (short)1 : (short)0;
			if (headToHeadMap.get(opptTeamKey) == null) {
				headToHeadMap.put(opptTeamKey, new Record(opptHeadToHead, (short)1, standingsMap.get(teamKey).getGamesWon(), standingsMap.get(teamKey).getGamesPlayed()));
			}
			else {
				headToHeadMap.get(opptTeamKey).setGamesWon((short)(headToHeadMap.get(opptTeamKey).getGamesWon() + opptHeadToHead));
				headToHeadMap.get(opptTeamKey).setGamesPlayed((short)(headToHeadMap.get(opptTeamKey).getGamesPlayed() + 1));
				headToHeadMap.get(opptTeamKey).setOpptGamesWon((short)(headToHeadMap.get(opptTeamKey).getOpptGamesWon() + standingsMap.get(teamKey).getGamesWon()));
				headToHeadMap.get(opptTeamKey).setOpptGamesPlayed((short)(headToHeadMap.get(opptTeamKey).getOpptGamesPlayed() + standingsMap.get(teamKey).getGamesPlayed()));
			}
		}
	
		for (int i = 0; i < completeGames.size(); i++) {
			int opptBoxScoreId = completeGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(teamKey) ? 1 : 0;
			opptBoxScore = completeGames.get(i).getBoxScores().get(opptBoxScoreId);
			String opptTeamKey = opptBoxScore.getTeam().getKey();
			
			opptGamesWon = (short)(opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon() - headToHeadMap.get(opptTeamKey).getGamesWon());
			opptGamesPlayed = (short)(opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed() - headToHeadMap.get(opptTeamKey).getGamesPlayed());
			opptOpptGamesWon = (short)(opptOpptGamesWon + standingsMap.get(opptTeamKey).getOpptGamesWon() - headToHeadMap.get(opptTeamKey).getOpptGamesWon());
			opptOpptGamesPlayed = (short)(opptOpptGamesPlayed + standingsMap.get(opptTeamKey).getOpptGamesPlayed() - headToHeadMap.get(opptTeamKey).getOpptGamesPlayed());
	
			System.out.println("  SubTeamStanding " + opptTeamKey);
			System.out.println("    Opponent Games Won/Played: " + opptGamesWon + " - " + opptGamesPlayed + " = " + 
									standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed() + " minus " + 
									headToHeadMap.get(opptTeamKey).getGamesWon() + " - " + headToHeadMap.get(opptTeamKey).getGamesPlayed());
			System.out.println("    OpptOppt Games Won/Played: " + opptOpptGamesWon + " - " + opptOpptGamesPlayed + " = " + 
									standingsMap.get(opptTeamKey).getOpptGamesWon() + " - " + standingsMap.get(opptTeamKey).getOpptGamesPlayed() + " minus " + 
									headToHeadMap.get(opptTeamKey).getOpptGamesWon() + " - " + headToHeadMap.get(opptTeamKey).getOpptGamesPlayed());
		}
		
		if (opptGamesWon > opptGamesPlayed)	//head to head wins exceed opponent wins, should only occur until wins start to occur
			System.out.println("Paul - crazy opptGamesWon more than opptGamesPlayed!");
			opptGamesWon = opptGamesPlayed;
		
		boxScore.setOpptGamesWon(opptGamesWon);
		boxScore.setOpptGamesPlayed(opptGamesPlayed);
		boxScore.setOpptOpptGamesWon(opptOpptGamesWon);
		boxScore.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
		
		System.out.println("  SumTeamStanding " + teamKey);
		System.out.println("    Opponent Games Won/Played = " + opptGamesWon + "-" + opptGamesPlayed);
		System.out.println("    OpptOppt Games Won/Played = " + opptOpptGamesWon + "-" + opptOpptGamesPlayed);
		BigDecimal opponentRecord = opptGamesPlayed == (short)0 ? new BigDecimal(0) : new BigDecimal(opptGamesWon).divide(new BigDecimal(opptGamesPlayed), 4, RoundingMode.HALF_UP);
		BigDecimal opponentOpponentRecord = opptOpptGamesWon == (short)0 ? new BigDecimal(0) : new BigDecimal(opptOpptGamesWon).divide(new BigDecimal(opptOpptGamesPlayed), 4, RoundingMode.HALF_UP);
		System.out.println("    Opponent Record = " + opponentRecord);
		System.out.println("    OpptOppt Record = " + opponentOpponentRecord);
		System.out.println("    Strenghth Of Schedule = " + opponentRecord.multiply(new BigDecimal(2)).add(opponentOpponentRecord).divide(new BigDecimal(3), 4, RoundingMode.HALF_UP) + '\n');
		
		return boxScore;
	}
}