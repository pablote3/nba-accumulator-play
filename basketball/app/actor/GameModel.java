package actor;

import static actor.ActorApi.GameIneligible;
import static actor.ActorApi.GameDayIncomplete;
import static actor.ActorApi.WorkStart;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import models.BoxScore;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Status;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.Utilities;
import actor.ActorApi.GameActive;
import actor.ActorApi.GameComplete;
import actor.ActorApi.GameFind;
import actor.ActorApi.GameIds;
import actor.ActorApi.GameRetrieve;
import actor.ActorApi.GameDayConfirmation;
import actor.ActorApi.GameDayComplete;
import actor.ActorApi.ModelException;
import actor.ActorApi.OfficialException;
import actor.ActorApi.RosterException;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {
	private ActorRef listener;
	private ActorRef controller;
	private final ActorRef gameXmlStats;
	private String propDate;
	private String propTeam;
	private String propSize;
	private Game game;
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
				  	int maxRows = Integer.parseInt(propSize);
				  	if (maxRows > 0) {
				  		games = Game.findIdsByDateRangeSize(propDate, propSize, processingType);
				  	}
				  	else if (maxRows == 0) {
				  		games = Game.findIdsByDateScheduled(propDate, processingType);
						if (games == null) {
							throw new NullPointerException();
						}
				  	}
				}
				else {
					games = Game.findIdByDateTeam(propDate, propTeam, processingType);
				}
				if (games == null) {
					throw new RejectedExecutionException();
				}
				GameIds ids = new GameIds(games);
				getSender().tell(ids, getSelf());
			} catch (NullPointerException e) {
				System.out.println("No scheduled games remaining for date " + propDate);
				controller.tell(new GameDayComplete(propDate), getSelf());
			} catch (RejectedExecutionException e) {
				getContext().stop(getSelf());
				ModelException me = new ModelException("NoGamesFound");
				listener.tell(me, getSelf());
			}
		}
		else if(message instanceof GameFind) {
			GameFind findGame = (GameFind)message;
			game = Game.findById(findGame.gameId, processingType);
			String gameDate = DateTimeUtil.getFindDateNaked(game.getDate());

			StringBuffer output;
			if (game.getStatus().equals(Status.scheduled) || game.getStatus().equals(Status.finished)) {
				output = new StringBuffer();
				output.append(Utilities.padString('\n' + "Finished Game Ready for Completion -", 40));
				output.append(" " + gameDate);
				output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
				output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
				System.out.println(output.toString());					
				GameRetrieve gr = new GameRetrieve(game);
				gameXmlStats.tell(gr, getSelf());
			}
			else  {
				output = new StringBuffer();
				output.append(Utilities.padString('\n' + "" + game.getStatus() + " Not Eligible for Completion -", 40));
				output.append(" " + gameDate);
				output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
				output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
				System.out.println(output.toString());
				controller.tell(GameIneligible, getSelf());
			}
		}
		else if(message instanceof GameActive) {
			Game game = ((GameActive)message).game;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
		  	Game.update(game, processingType);
		  	System.out.println("Game Complete " + awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
		  	GameComplete gc = new GameComplete(DateTimeUtil.getFindDateShort(game.getDate()));
		  	controller.tell(gc, getSelf());
		}
		else if(message instanceof GameDayConfirmation) {
			String gameDate = ((GameDayConfirmation) message).date;
			int gamesScheduled = Game.findCountGamesByDateScheduled(gameDate, processingType);
			if (gamesScheduled == 0) {
				System.out.println("Game Day complete for " + gameDate + " load standings");
				controller.tell(new GameDayComplete(gameDate), getSelf());
			}
			else {
				System.out.println("Game Day incomplete for " + gameDate + " is " + gamesScheduled);
				controller.tell(GameDayIncomplete, getSelf());
			}
		}
		else if(message instanceof RosterException) {
			controller.tell(message, getSelf());
		}
		else if(message instanceof OfficialException) {
			ModelException me = new ModelException(((OfficialException)message).getMessage());
			listener.tell(me, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}