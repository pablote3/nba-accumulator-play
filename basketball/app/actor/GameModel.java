package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import models.BoxScore;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Status;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.Utilities;
import actor.ActorApi.CompleteBoxScore;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.GameIds;
import actor.ActorApi.IncompleteOfficialException;
import actor.ActorApi.IncompleteRosterException;
import actor.ActorApi.ModelException;
import actor.ActorApi.ScheduleGame;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.WorkGame;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef gameXmlStats;
	private ActorRef controller;
	private String propDate;
	private String propTeam;
	private String propSize;
	private ProcessingType processingType;
	
	public GameModel(ActorRef listener) {
		this.listener = listener;
		gameXmlStats = getContext().actorOf(Props.create(GameXmlStats.class, listener), "gameXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			propDate = ((ServiceProps) message).date == null || ((ServiceProps) message).date.isEmpty() ? DateTimeUtil.getFindDateShort(new DateTime()) : ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;
			propSize = ((ServiceProps) message).size == null || ((ServiceProps) message).size.isEmpty() ? propSize = "0" : ((ServiceProps) message).size;
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			gameXmlStats.tell(message, getSender());
		}
		else if (message.equals(WorkStart)) {					
			List<Long> games = null;
			controller = getSender();
			try {
				if (propTeam == null || propTeam.isEmpty()) {
					games = Game.findIdsByDateSize(propDate, propSize, processingType);
				}
				else {
					games = Game.findIdsByDateTeamSize(propDate, propTeam, propSize, processingType);
				}
				if (games == null) {
					throw new NullPointerException();
				}
			} catch (NullPointerException e) {
				getContext().stop(getSelf());
				ModelException me = new ModelException("NoGamesFound");
				listener.tell(me, getSelf());
			}
			GameIds ids = new GameIds(games);
			getSender().tell(ids, getSelf());
		}
		else if(message instanceof WorkGame) {
			WorkGame workGame = (WorkGame)message;			
			Game game = Game.findById(workGame.gameId, processingType);
			StringBuffer output;
			
			if (game.getStatus().equals(Status.scheduled) || game.getStatus().equals(Status.finished)) {
				output = new StringBuffer();
				output.append(Utilities.padString('\n' + "Finished Game Ready for Completion -", 40));
				output.append(" " + DateTimeUtil.getFindDateNaked(game.getDate()));
				output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
				output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
				System.out.println(output.toString());
				
				ScheduleGame sg = new ScheduleGame(game);
				gameXmlStats.tell(sg, getSelf());
			}
			else  {
				output = new StringBuffer();
				output.append(Utilities.padString('\n' + "" + game.getStatus() + " Not Eligible for Completion -", 40));
				output.append(" " + DateTimeUtil.getFindDateNaked(game.getDate()));
				output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
				output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
				System.out.println(output.toString());				
				controller.tell(NextGame, getSelf());
			}
		}
		else if(message instanceof CompleteBoxScore) {
			controller.tell(message, getSelf());
		}
		else if(message instanceof CompleteGame) {
			Game game = ((CompleteGame)message).game;
			String gameDate = DateTimeUtil.getFindDateShort(game.getDate());
			
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			Game homePreviousGame = Game.findPreviousByDateTeamSeason(gameDate, homeBoxScore.getTeam().getKey(), processingType);
			
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			Game awayPreviousGame = Game.findPreviousByDateTeamSeason(gameDate, awayBoxScore.getTeam().getKey(), processingType);

			Integer homeOpptPrevGamesWon = 0;
			Integer homeOpptPrevGamesPlayed = 0;
			Integer awayOpptPrevGamesWon = 0;
			Integer awayOpptPrevGamesPlayed = 0;
			
			Integer homeOpptOpptPrevGamesWon = 0;
			Integer homeOpptOpptPrevGamesPlayed = 0;
			Integer awayOpptOpptPrevGamesWon = 0;
			Integer awayOpptOpptPrevGamesPlayed = 0;
			
//			Short homeTeamPrevGamesWon = 0;
//			Short homeTeamPrevGamesPlayed = 0;
//			Short awayTeamPrevGamesWon = 0;
//			Short awayTeamPrevGamesPlayed = 0;
			
//			int homeResult = homeBoxScore.getResult() == Result.win ? 1 : 0;
//			int awayResult = awayBoxScore.getResult() == Result.win ? 1 : 0;

			if (homePreviousGame != null) {
				if (homePreviousGame.getBoxScores().get(0).getTeam().getKey().equals(homeBoxScore.getTeam().getKey())) {
//					homeTeamPrevGamesWon = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesWon();
//					homeTeamPrevGamesPlayed = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesPlayed();
					homeOpptPrevGamesWon = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptWins();
					homeOpptPrevGamesPlayed = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptGamesPlayed();
					homeOpptOpptPrevGamesWon = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptWins();
					homeOpptOpptPrevGamesPlayed = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptGamesPlayed();
				}
				else {
//					homeTeamPrevGamesWon = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesWon();
//					homeTeamPrevGamesPlayed = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesPlayed();
					homeOpptPrevGamesWon = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptWins();
					homeOpptPrevGamesPlayed = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptGamesPlayed();
					homeOpptOpptPrevGamesWon = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptWins();
					homeOpptOpptPrevGamesPlayed = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptGamesPlayed();
				}
			}
			
			if (awayPreviousGame != null) {
				if (awayPreviousGame.getBoxScores().get(0).getTeam().getKey().equals(awayBoxScore.getTeam().getKey())) {
//					awayTeamPrevGamesWon = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesWon();
//					awayTeamPrevGamesPlayed = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesPlayed();
					awayOpptPrevGamesWon = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptWins();
					awayOpptPrevGamesPlayed = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptGamesPlayed();
					awayOpptOpptPrevGamesWon = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptWins();
					awayOpptOpptPrevGamesPlayed = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptGamesPlayed();
				}
				else {
//					awayTeamPrevGamesWon = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesWon();
//					awayTeamPrevGamesPlayed = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesPlayed();
					awayOpptPrevGamesWon = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptWins();
					awayOpptPrevGamesPlayed = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptGamesPlayed();
					awayOpptOpptPrevGamesWon = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptWins();
					awayOpptOpptPrevGamesPlayed = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptGamesPlayed();
				}
			}
			
//			homeBoxScore.getStandings().get(0).setGameDate(game.getDate());
//			homeBoxScore.getStandings().get(0).setOpptWins(homeOpptPrevGamesWon + awayTeamPrevGamesWon + awayResult);
//			homeBoxScore.getStandings().get(0).setOpptGamesPlayed(homeOpptPrevGamesPlayed + awayTeamPrevGamesPlayed + 1);
//			homeBoxScore.getStandings().get(0).setOpptOpptWins(homeOpptOpptPrevGamesWon + awayOpptPrevGamesWon + homeResult);
//			homeBoxScore.getStandings().get(0).setOpptOpptGamesPlayed(homeOpptOpptPrevGamesPlayed + awayOpptPrevGamesPlayed + 1);
			
//			awayBoxScore.getStandings().get(0).setGameDate(game.getDate());
//			awayBoxScore.getStandings().get(0).setOpptWins(awayOpptPrevGamesWon + homeTeamPrevGamesWon + homeResult);
//			awayBoxScore.getStandings().get(0).setOpptGamesPlayed(awayOpptPrevGamesPlayed + homeTeamPrevGamesPlayed + 1);
//			awayBoxScore.getStandings().get(0).setOpptOpptWins(awayOpptOpptPrevGamesWon + homeOpptPrevGamesWon + awayResult);
//			awayBoxScore.getStandings().get(0).setOpptOpptGamesPlayed(awayOpptOpptPrevGamesPlayed + homeOpptPrevGamesPlayed + 1);

			homeBoxScore.getStandings().get(0).setGameDate(game.getDate());
			homeBoxScore.getStandings().get(0).setOpptWins(homeOpptPrevGamesWon + awayBoxScore.getStandings().get(0).getGamesWon());
			homeBoxScore.getStandings().get(0).setOpptGamesPlayed(homeOpptPrevGamesPlayed + awayBoxScore.getStandings().get(0).getGamesPlayed());
			homeBoxScore.getStandings().get(0).setOpptOpptWins(homeOpptOpptPrevGamesWon + homeBoxScore.getStandings().get(0).getGamesWon());
			homeBoxScore.getStandings().get(0).setOpptOpptGamesPlayed(homeOpptOpptPrevGamesPlayed + homeBoxScore.getStandings().get(0).getGamesPlayed());
			
			awayBoxScore.getStandings().get(0).setGameDate(game.getDate());
			awayBoxScore.getStandings().get(0).setOpptWins(awayOpptPrevGamesWon + homeBoxScore.getStandings().get(0).getGamesWon());
			awayBoxScore.getStandings().get(0).setOpptGamesPlayed(awayOpptPrevGamesPlayed + homeBoxScore.getStandings().get(0).getGamesPlayed());
			awayBoxScore.getStandings().get(0).setOpptOpptWins(awayOpptOpptPrevGamesWon + awayBoxScore.getStandings().get(0).getGamesWon());
			awayBoxScore.getStandings().get(0).setOpptOpptGamesPlayed(awayOpptOpptPrevGamesPlayed + awayBoxScore.getStandings().get(0).getGamesPlayed());
			
			BigDecimal homeOpptWinPercent = (homeBoxScore.getStandings().get(0).getOpptGamesPlayed() == 0) ? BigDecimal.ZERO : 
				new BigDecimal(homeBoxScore.getStandings().get(0).getOpptWins()).divide(new BigDecimal(homeBoxScore.getStandings().get(0).getOpptGamesPlayed()), 3, RoundingMode.HALF_UP);
			BigDecimal homeOpptOpptWinPercent = (homeBoxScore.getStandings().get(0).getOpptOpptGamesPlayed() == 0) ? BigDecimal.ZERO  : 
				new BigDecimal(homeBoxScore.getStandings().get(0).getOpptOpptWins()).divide(new BigDecimal(homeBoxScore.getStandings().get(0).getOpptOpptGamesPlayed()), 3, RoundingMode.HALF_UP);
			BigDecimal awayOpptWinPercent = (awayBoxScore.getStandings().get(0).getOpptGamesPlayed() == 0) ? BigDecimal.ZERO  : 
				new BigDecimal(awayBoxScore.getStandings().get(0).getOpptWins()).divide(new BigDecimal(awayBoxScore.getStandings().get(0).getOpptGamesPlayed()), 3, RoundingMode.HALF_UP);
			BigDecimal awayOpptOpptWinPercent = (awayBoxScore.getStandings().get(0).getOpptOpptGamesPlayed() == 0) ? BigDecimal.ZERO  : 
				new BigDecimal(awayBoxScore.getStandings().get(0).getOpptOpptWins()).divide(new BigDecimal(awayBoxScore.getStandings().get(0).getOpptOpptGamesPlayed()), 3, RoundingMode.HALF_UP);
			
			System.out.println("  HomeTeamStanding " + homeBoxScore.getTeam().getShortName());
			System.out.println("     OldOppt " + homeOpptPrevGamesWon + "-" + homeOpptPrevGamesPlayed + " + " + 
							   		"OpptRecord " + awayBoxScore.getStandings().get(0).getGamesWon() + "-" + awayBoxScore.getStandings().get(0).getGamesPlayed() + " = " +
									"NewOppt " + homeBoxScore.getStandings().get(0).getOpptWins() + "-" + homeBoxScore.getStandings().get(0).getOpptGamesPlayed());
			System.out.println("     OldOpptOppt " + homeOpptOpptPrevGamesWon + "-" + homeOpptOpptPrevGamesPlayed + " + " + 
			   						"OpptOpptRecord " + homeBoxScore.getStandings().get(0).getGamesWon() + "-" + homeBoxScore.getStandings().get(0).getGamesPlayed() + " = " +
			   						"NewOpptOppt " + homeBoxScore.getStandings().get(0).getOpptOpptWins() + "-" + homeBoxScore.getStandings().get(0).getOpptOpptGamesPlayed());
			System.out.println("     StrengthOfSchedule " + homeOpptWinPercent.multiply(new BigDecimal(2)).add(homeOpptOpptWinPercent).divide(new BigDecimal(3), 3, RoundingMode.HALF_UP));
			
			System.out.println("  AwayTeamStanding " + awayBoxScore.getTeam().getShortName());
			System.out.println("     OldOppt " + awayOpptPrevGamesWon + "-" + awayOpptPrevGamesPlayed + " + " + 
			   						"OpptRecord " + homeBoxScore.getStandings().get(0).getGamesWon() + "-" + homeBoxScore.getStandings().get(0).getGamesPlayed() + " = " +
			   						"NewOppt " + awayBoxScore.getStandings().get(0).getOpptWins() + "-" + awayBoxScore.getStandings().get(0).getOpptGamesPlayed()); 
			System.out.println("     OldOpptOppt " + awayOpptOpptPrevGamesWon + "-" + awayOpptOpptPrevGamesPlayed + " + " + 
									"OpptOpptRecord " + awayBoxScore.getStandings().get(0).getGamesWon() + "-" + awayBoxScore.getStandings().get(0).getGamesPlayed() + " = " +
									"NewOpptOppt " + awayBoxScore.getStandings().get(0).getOpptOpptWins() + "-" + awayBoxScore.getStandings().get(0).getOpptOpptGamesPlayed());
			System.out.println("     StrengthOfSchedule " + awayOpptWinPercent.multiply(new BigDecimal(2)).add(awayOpptOpptWinPercent).divide(new BigDecimal(3), 3, RoundingMode.HALF_UP));
			
		  	Game.update(game, processingType);		  	
		  	System.out.println("Game Complete " + awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
		  	
		  	controller.tell(NextGame, getSelf());
		}
		else if(message instanceof IncompleteRosterException) {
			controller.tell(message, getSelf());
		}
		else if(message instanceof IncompleteOfficialException) {
			ModelException me = new ModelException(((IncompleteOfficialException)message).getMessage());
			listener.tell(me, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}