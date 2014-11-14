package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.util.List;

import models.BoxScore;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Status;
import models.Standing;

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

			Integer homePrevOpptOpptGamesWon = 0;
			Integer homePrevOpptOpptGamesPlayed = 0;
			Short homePrevGamesWon = 0;
			Short homePrevGamesPlayed = 0;
			Integer awayPrevOpptOpptGamesWon = 0;
			Integer awayPrevOpptOpptGamesPlayed = 0;
			Short awayPrevGamesWon = 0;
			Short awayPrevGamesPlayed = 0;

			if (homePreviousGame != null) {
				if (homePreviousGame.getBoxScores().get(0).getTeam().getKey().equals(homeBoxScore.getTeam().getKey())) {
					homePrevOpptOpptGamesWon = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptWins();
					homePrevOpptOpptGamesPlayed = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptGamesPlayed();
					homePrevGamesWon = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesWon();
					homePrevGamesPlayed = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesPlayed();
				}
				else {
					homePrevOpptOpptGamesWon = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptWins();
					homePrevOpptOpptGamesPlayed = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptGamesPlayed();
					homePrevGamesWon = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesWon();
					homePrevGamesPlayed = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesPlayed();
				}
			}
			
			if (awayPreviousGame != null) {
				if (awayPreviousGame.getBoxScores().get(0).getTeam().getKey().equals(awayBoxScore.getTeam().getKey())) {
					awayPrevOpptOpptGamesWon = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptWins();
					awayPrevOpptOpptGamesPlayed = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getOpptOpptGamesPlayed();
					awayPrevGamesWon = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesWon();
					awayPrevGamesPlayed = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getGamesPlayed();
				}
				else {
					awayPrevOpptOpptGamesWon = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptWins();
					awayPrevOpptOpptGamesPlayed = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getOpptOpptGamesPlayed();
					awayPrevGamesWon = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesWon();
					awayPrevGamesPlayed = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getGamesPlayed();
				}
			}
			
//			int homeResult = homeBoxScore.getResult() == Result.win ? 1 : 0;
//			int awayResult = awayBoxScore.getResult() == Result.win ? 1 : 0;
			
			homeBoxScore.getStandings().get(0).setGameDate(game.getDate());
//			homeBoxScore.getStandings().get(0).setOpptOpptWins(awayPrevOpptOpptGamesWon + homePrevGamesWon + homeResult);
			homeBoxScore.getStandings().get(0).setOpptOpptWins(awayPrevOpptOpptGamesWon + homePrevGamesWon);
			homeBoxScore.getStandings().get(0).setOpptOpptGamesPlayed(awayPrevOpptOpptGamesPlayed + homePrevGamesPlayed + 1);
			homeBoxScore.getStandings().get(0).setAvgOpptOpptWinPercentage(Standing.findOpponentOppenentWinPercentageSeason(gameDate, homeBoxScore.getTeam().getKey(), processingType));

			awayBoxScore.getStandings().get(0).setGameDate(game.getDate());
//			awayBoxScore.getStandings().get(0).setOpptOpptWins(homePrevOpptOpptGamesWon + awayPrevGamesWon + awayResult);
			awayBoxScore.getStandings().get(0).setOpptOpptGamesPlayed(homePrevOpptOpptGamesPlayed + awayPrevGamesPlayed + 1);
			awayBoxScore.getStandings().get(0).setAvgOpptOpptWinPercentage(Standing.findOpponentOppenentWinPercentageSeason(gameDate, awayBoxScore.getTeam().getKey(), processingType));
			
			System.out.println("  HomeTeamStanding " + homeBoxScore.getTeam().getShortName());
			System.out.println("     AwayPrevOpptOppt " + awayPrevOpptOpptGamesWon + "-" + awayPrevOpptOpptGamesPlayed + " + " +
									"HomePrevious " + homePrevGamesWon + "-" + homePrevGamesPlayed + " + " + 
//									"HomeCurrent " + homeResult + "-1 = " +
							   		"Result " + homeBoxScore.getStandings().get(0).getOpptOpptWins() + "-" + homeBoxScore.getStandings().get(0).getOpptOpptGamesPlayed()); 
			
			System.out.println("  AwayTeamStanding " + awayBoxScore.getTeam().getShortName());
			System.out.println("     HomePrevOpptOppt " + homePrevOpptOpptGamesWon + "-" + homePrevOpptOpptGamesPlayed + " + " +
									"AwayPrevious " + awayPrevGamesWon + "-" + awayPrevGamesPlayed + " + " + 
//									"AwayCurrent " + awayResult + "-1 = " +
									"Result " + awayBoxScore.getStandings().get(0).getOpptOpptWins() + "-" + awayBoxScore.getStandings().get(0).getOpptOpptGamesPlayed());
			
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