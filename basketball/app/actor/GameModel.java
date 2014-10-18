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
				output.append(Utilities.padString('\n' + "  Finished Game Ready for Completion -", 40));
				output.append(" " + DateTimeUtil.getFindDateNaked(game.getDate()));
				output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
				output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
				System.out.println(output.toString());
				
				ScheduleGame sg = new ScheduleGame(game);
				gameXmlStats.tell(sg, getSelf());
			}
			else  {
				output = new StringBuffer();
				output.append(Utilities.padString('\n' + "  " + game.getStatus() + " Not Eligible for Completion -", 40));
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
			
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			Game awayPreviousGame = Game.findPreviousByDateTeamSeason(gameDate, awayBoxScore.getTeam().getKey(), processingType);
			
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			Game homePreviousGame = Game.findPreviousByDateTeamSeason(gameDate, homeBoxScore.getTeam().getKey(), processingType);
			
			if (awayPreviousGame != null) {
				Standing awayOpptPreviousStanding;
				if (awayPreviousGame.getBoxScores().get(0).getTeam().getKey().equals(awayBoxScore.getTeam().getKey()))
					awayOpptPreviousStanding = awayPreviousGame.getBoxScores().get(1).getStandings().get(0);
				else
					awayOpptPreviousStanding = awayPreviousGame.getBoxScores().get(0).getStandings().get(0);
		
				homeBoxScore.getStandings().get(0).setSumOpptWins((short)(homeBoxScore.getStandings().get(0).getSumOpptWins() + awayOpptPreviousStanding.getGamesWon()));
				homeBoxScore.getStandings().get(0).setSumOpptGamesPlayed((short)(homeBoxScore.getStandings().get(0).getSumOpptGamesPlayed() + awayOpptPreviousStanding.getGamesPlayed()));
			}
			else {
				homeBoxScore.getStandings().get(0).setSumOpptWins((short)0);
				homeBoxScore.getStandings().get(0).setSumOpptGamesPlayed((short)0);
			}
						
			if (homePreviousGame != null) {
				Standing homeOpptPreviousStanding;
				if (homePreviousGame.getBoxScores().get(0).getTeam().getKey().equals(homeBoxScore.getTeam().getKey()))
					homeOpptPreviousStanding = homePreviousGame.getBoxScores().get(0).getStandings().get(0);
				else
					homeOpptPreviousStanding = homePreviousGame.getBoxScores().get(1).getStandings().get(0);
				
				awayBoxScore.getStandings().get(0).setSumOpptWins((short)(awayBoxScore.getStandings().get(0).getSumOpptWins() + homeOpptPreviousStanding.getGamesWon()));
				awayBoxScore.getStandings().get(0).setSumOpptGamesPlayed((short)(awayBoxScore.getStandings().get(0).getSumOpptGamesPlayed() + homeOpptPreviousStanding.getGamesPlayed()));
			}
			else {
				awayBoxScore.getStandings().get(0).setSumOpptWins((short)0);
				awayBoxScore.getStandings().get(0).setSumOpptGamesPlayed((short)0);
			}			
			
			System.out.println(awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
			
		  	Game.update(game, processingType);
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