package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.util.List;

import models.BoxScore;
import models.BoxScore.Result;
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

			Short homeTeamPrevOpptOpptGamesWon = 0;
			Short homeTeamPrevOpptOpptGamesPlayed = 0;			
			Short homeOpptPrevOpptOpptGamesWon = 0;
			Short homeOpptPrevOpptOpptGamesPlayed = 0;
			Short awayTeamPrevOpptOpptGamesWon = 0;
			Short awayTeamPrevOpptOpptGamesPlayed = 0;	
			Short awayOpptPrevOpptOpptGamesWon = 0;
			Short awayOpptPrevOpptOpptGamesPlayed = 0;

			if (homePreviousGame != null) {
				if (homePreviousGame.getBoxScores().get(0).getTeam().getKey().equals(homeBoxScore.getTeam().getKey())) {
					homeTeamPrevOpptOpptGamesWon = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getSumOpptWins();
					homeTeamPrevOpptOpptGamesPlayed = homePreviousGame.getBoxScores().get(0).getStandings().get(0).getSumOpptGamesPlayed();

					homeOpptPrevOpptOpptGamesWon = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getSumOpptWins();
					homeOpptPrevOpptOpptGamesPlayed = homePreviousGame.getBoxScores().get(1).getStandings().get(0).getSumOpptGamesPlayed();
				}
			}
			
			if (awayPreviousGame != null) {
				if (awayPreviousGame.getBoxScores().get(0).getTeam().getKey().equals(awayBoxScore.getTeam().getKey())) {
					awayTeamPrevOpptOpptGamesWon = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getSumOpptWins();
					awayTeamPrevOpptOpptGamesPlayed = awayPreviousGame.getBoxScores().get(0).getStandings().get(0).getSumOpptGamesPlayed();
					
					awayOpptPrevOpptOpptGamesWon = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getSumOpptWins();
					awayOpptPrevOpptOpptGamesPlayed = awayPreviousGame.getBoxScores().get(1).getStandings().get(0).getSumOpptGamesPlayed();
				}
			}
			
			int homeResult = homeBoxScore.getResult() == Result.win ? 1 : 0;
			int awayResult = awayBoxScore.getResult() == Result.win ? 1 : 0;

			homeBoxScore.getStandings().get(0).setSumOpptWins((short)(homeTeamPrevOpptOpptGamesWon + awayOpptPrevOpptOpptGamesWon + homeResult));
			homeBoxScore.getStandings().get(0).setSumOpptGamesPlayed((short)(homeTeamPrevOpptOpptGamesPlayed + awayOpptPrevOpptOpptGamesPlayed + 1));

			awayBoxScore.getStandings().get(0).setSumOpptWins((short)(awayTeamPrevOpptOpptGamesWon + homeOpptPrevOpptOpptGamesWon + awayResult));
			awayBoxScore.getStandings().get(0).setSumOpptGamesPlayed((short)(awayTeamPrevOpptOpptGamesPlayed + homeOpptPrevOpptOpptGamesPlayed + 1));
			
			System.out.println("  HomeTeamStanding " + homeBoxScore.getTeam().getShortName());
			System.out.println("    HomeTeamPrevOpptOppt " + homeTeamPrevOpptOpptGamesWon + "-" + homeTeamPrevOpptOpptGamesPlayed + " + " +
							   		"AwayOpptPrevOpptOppt " + awayOpptPrevOpptOpptGamesWon + "-" + awayOpptPrevOpptOpptGamesPlayed + " + " +
									"HomeCurrent " + homeResult + "-1 = " +
							   		"Result " + homeBoxScore.getStandings().get(0).getSumOpptWins() + "-" + homeBoxScore.getStandings().get(0).getSumOpptGamesPlayed());
			System.out.println("  AwayTeamStanding " + awayBoxScore.getTeam().getShortName());
			System.out.println("    AwayTeamPrevOpptOppt " + awayTeamPrevOpptOpptGamesWon + "-" + awayTeamPrevOpptOpptGamesPlayed + " + " +
							   		"HomeOpptPrevOpptOppt " + homeOpptPrevOpptOpptGamesWon + "-" + homeOpptPrevOpptOpptGamesPlayed + " + " +
									"AwayCurrent " + awayResult + "-1 = " +
									"Result " + awayBoxScore.getStandings().get(0).getSumOpptWins() + "-" + awayBoxScore.getStandings().get(0).getSumOpptGamesPlayed());
			
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