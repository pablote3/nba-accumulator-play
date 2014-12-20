package actor;

import static actor.ActorApi.StandingsComplete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.BoxScore;
import models.BoxScore.Result;
import models.Game;
import models.Game.ProcessingType;
import models.Standing;
import util.DateTimeUtil;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.StandingsLoad;
import actor.ActorApi.StandingsRetrieve;
import actor.ActorApi.StandingsActive;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class StandingModel extends UntypedActor {
	@SuppressWarnings("unused")
	private ActorRef listener;
	
	private final ActorRef standingXmlStats;
	private ActorRef controller;
	private Map<String, Record> standingsMap;
	private ProcessingType processingType;
	Standing teamStanding;
	String standingsDate;
	
	public StandingModel(ActorRef listener) {
		this.listener = listener;
		standingXmlStats = getContext().actorOf(Props.create(StandingXmlStats.class, listener), "standingXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			standingXmlStats.tell(message, getSender());
		}
		else if(message instanceof StandingsLoad) {
			controller = getSender();
			standingsDate = ((StandingsLoad)message).date;
			List<Standing> standingsList = Standing.findByDate(standingsDate, processingType);
			if (!standingsList.isEmpty() && standingsList.size() > 0) {
				System.out.println("Deleteing standings for " + standingsDate);
				for (int i = 0; i < standingsList.size(); i++) {
					teamStanding = standingsList.get(i);
					Standing.delete(teamStanding, processingType);
				}
			}
			StandingsRetrieve sr = new StandingsRetrieve(standingsDate);
			standingXmlStats.tell(sr, getSelf());
		}
		else if(message instanceof StandingsActive) {
			StandingsActive activeStandings = (StandingsActive) message;
			List<Standing> standingsActive = new ArrayList<Standing>(activeStandings.standings);
			List<Game> completeGames;
			standingsMap = new HashMap<String, Record>();
			String opptTeamKey;
			Integer opptGamesWon;
			Integer opptGamesPlayed;
			Game game;
	
			Iterator<Standing> mapIt = standingsActive.iterator();
			while (mapIt.hasNext()) {
				teamStanding = mapIt.next();
				standingsMap.put(teamStanding.getTeam().getKey(), new Record((int)teamStanding.getGamesWon(), (int)teamStanding.getGamesPlayed(), 0, 0));
			}
			
			String standingDate = DateTimeUtil.getFindDateShort(standingsActive.get(0).getDate());
			Iterator<Standing> createIt = standingsActive.iterator();
			while (createIt.hasNext()) {
				teamStanding = createIt.next();
				String teamKey = teamStanding.getTeam().getKey();
				opptGamesWon = 0;
				opptGamesPlayed = 0;
				completeGames = Game.findByDateTeamSeason(standingsDate, teamKey, processingType);
				for (int k = 0; k < completeGames.size(); k++) {
					game = completeGames.get(k);
					int opptBoxScoreId = game.getBoxScores().get(0).getTeam().equals(teamStanding.getTeam()) ? 1 : 0;
					opptTeamKey = game.getBoxScores().get(opptBoxScoreId).getTeam().getKey();
					opptGamesWon = opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon();
					opptGamesPlayed = opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed();
//					String opptGameDate = DateTimeUtil.getFindDateShort(game.getDate());
//					System.out.println("  StandingsMap " + teamKey + " " + opptGameDate + " " + opptTeamKey + 
//										" Games Won/Played: " + standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed());
				}
				standingsMap.get(teamKey).setOpptGamesWon(opptGamesWon);
				standingsMap.get(teamKey).setOpptGamesPlayed(opptGamesPlayed);
				Standing.create(teamStanding, processingType);
			}

			Iterator<Standing> updateIt = standingsActive.iterator();
			while (updateIt.hasNext()) {
				String standingTeam = updateIt.next().getTeam().getKey();
				teamStanding = Standing.findByDateTeam(standingDate, standingTeam, processingType);			
				teamStanding = CalculateStrengthOfSchedule(standingDate, teamStanding, standingTeam);
				Standing.update(teamStanding, processingType);
			}
			controller.tell(StandingsComplete, getSelf());
		}
		else {
			unhandled(message);
		}
	}

	private Standing CalculateStrengthOfSchedule(String standingDate, Standing standing, String teamKey) {
		BoxScore opptBoxScore;
		Integer opptHeadToHead;
		Integer opptGamesWon = 0;
		Integer opptGamesPlayed = 0;
		Integer opptOpptGamesWon = 0;
		Integer opptOpptGamesPlayed = 0;
		List<Game> completeGames = Game.findByDateTeamSeason(standingDate, teamKey, processingType);
		
		Map<String, Record> headToHeadMap = new HashMap<String, Record>();
		for (int i = 0; i < completeGames.size(); i++) {
			int opptBoxScoreId = completeGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(teamKey) ? 1 : 0;
			opptBoxScore = completeGames.get(i).getBoxScores().get(opptBoxScoreId);
			String opptTeamKey = opptBoxScore.getTeam().getKey();
			opptHeadToHead = opptBoxScore.getResult() != null && opptBoxScore.getResult().equals(Result.win) ? 1 : 0;
			if (headToHeadMap.get(opptTeamKey) == null) {
				headToHeadMap.put(opptTeamKey, new Record(opptHeadToHead, 1, standingsMap.get(teamKey).getGamesWon(), standingsMap.get(teamKey).getGamesPlayed()));
			}
			else {
				headToHeadMap.get(opptTeamKey).setGamesWon(headToHeadMap.get(opptTeamKey).getGamesWon() + opptHeadToHead);
				headToHeadMap.get(opptTeamKey).setGamesPlayed(headToHeadMap.get(opptTeamKey).getGamesPlayed() + 1);
				headToHeadMap.get(opptTeamKey).setOpptGamesWon(headToHeadMap.get(opptTeamKey).getOpptGamesWon() + standingsMap.get(teamKey).getGamesWon());
				headToHeadMap.get(opptTeamKey).setOpptGamesPlayed(headToHeadMap.get(opptTeamKey).getOpptGamesPlayed() + standingsMap.get(teamKey).getGamesPlayed());
			}
		}
	
		for (int i = 0; i < completeGames.size(); i++) {
			int opptBoxScoreId = completeGames.get(i).getBoxScores().get(0).getTeam().getKey().equals(teamKey) ? 1 : 0;
			opptBoxScore = completeGames.get(i).getBoxScores().get(opptBoxScoreId);
			String opptTeamKey = opptBoxScore.getTeam().getKey();
			
			opptGamesWon = opptGamesWon + standingsMap.get(opptTeamKey).getGamesWon() - headToHeadMap.get(opptTeamKey).getGamesWon();
			opptGamesPlayed = opptGamesPlayed + standingsMap.get(opptTeamKey).getGamesPlayed() - headToHeadMap.get(opptTeamKey).getGamesPlayed();
			opptOpptGamesWon = opptOpptGamesWon + standingsMap.get(opptTeamKey).getOpptGamesWon() - headToHeadMap.get(opptTeamKey).getOpptGamesWon();
			opptOpptGamesPlayed = opptOpptGamesPlayed + standingsMap.get(opptTeamKey).getOpptGamesPlayed() - headToHeadMap.get(opptTeamKey).getOpptGamesPlayed();
	
//			System.out.println("  SubTeamStanding " + opptTeamKey);
//			System.out.println("    Opponent Games Won/Played: " + opptGamesWon + " - " + opptGamesPlayed + " = " + 
//									standingsMap.get(opptTeamKey).getGamesWon() + " - " + standingsMap.get(opptTeamKey).getGamesPlayed() + " minus " + 
//									headToHeadMap.get(opptTeamKey).getGamesWon() + " - " + headToHeadMap.get(opptTeamKey).getGamesPlayed());
//			System.out.println("    OpptOppt Games Won/Played: " + opptOpptGamesWon + " - " + opptOpptGamesPlayed + " = " + 
//									standingsMap.get(opptTeamKey).getOpptGamesWon() + " - " + standingsMap.get(opptTeamKey).getOpptGamesPlayed() + " minus " + 
//									headToHeadMap.get(opptTeamKey).getOpptGamesWon() + " - " + headToHeadMap.get(opptTeamKey).getOpptGamesPlayed());
		
			if (opptGamesWon > opptGamesPlayed)	 { 
				//head to head wins exceed opponent wins, should only occur until wins start to occur
				//observed occurrence when loading standings before entire day's games were loaded
				System.out.println("Paul - crazy opptGamesWon more than opptGamesPlayed!");
				opptGamesWon = opptGamesPlayed;
			}
		}
		
		standing.setOpptGamesWon(opptGamesWon);
		standing.setOpptGamesPlayed(opptGamesPlayed);
		standing.setOpptOpptGamesWon(opptOpptGamesWon);
		standing.setOpptOpptGamesPlayed(opptOpptGamesPlayed);
		
		BigDecimal opponentRecord = opptGamesPlayed == 0 ? new BigDecimal(0) : new BigDecimal(opptGamesWon).divide(new BigDecimal(opptGamesPlayed), 4, RoundingMode.HALF_UP);
		BigDecimal opponentOpponentRecord = opptOpptGamesWon == 0 ? new BigDecimal(0) : new BigDecimal(opptOpptGamesWon).divide(new BigDecimal(opptOpptGamesPlayed), 4, RoundingMode.HALF_UP);
//		System.out.println("    Opponent Games Won/Played = " + opptGamesWon + "-" + opptGamesPlayed);
//		System.out.println("    OpptOppt Games Won/Played = " + opptOpptGamesWon + "-" + opptOpptGamesPlayed);
//		System.out.println("    Opponent Record = " + opponentRecord);
//		System.out.println("    OpptOppt Record = " + opponentOpponentRecord);
		System.out.println("  Strenghth Of Schedule  " + teamKey + " " + opponentRecord.multiply(new BigDecimal(2)).add(opponentOpponentRecord).divide(new BigDecimal(3), 4, RoundingMode.HALF_UP));		
		return standing;
	}
	
	private class Record {
		private Integer gamesWon;
		private Integer gamesPlayed;
		private Integer opptGamesWon;
		private Integer opptGamesPlayed;
		
		private Record(Integer gamesWon, Integer gamesPlayed, Integer opptGamesWon, Integer opptGamesPlayed) {
			this.gamesWon = gamesWon;
			this.gamesPlayed = gamesPlayed;
			this.opptGamesWon = opptGamesWon;
			this.opptGamesPlayed = opptGamesPlayed;
		}

		private Integer getGamesWon() {
			return gamesWon;
		}
		private void setGamesWon(Integer gamesWon) {
			this.gamesWon = gamesWon;
		}

		private Integer getGamesPlayed() {
			return gamesPlayed;
		}
		private void setGamesPlayed(Integer gamesPlayed) {
			this.gamesPlayed = gamesPlayed;
		}	

		private Integer getOpptGamesWon() {
			return opptGamesWon;
		}
		private void setOpptGamesWon(Integer opptGamesWon) {
			this.opptGamesWon = opptGamesWon;
		}

		private Integer getOpptGamesPlayed() {
			return opptGamesPlayed;
		}
		private void setOpptGamesPlayed(Integer opptGamesPlayed) {
			this.opptGamesPlayed = opptGamesPlayed;
		}		
	}
}