package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.util.List;

import models.BoxScore;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Status;

import org.joda.time.DateTime;

import util.DateTimeUtil;
import util.Utilities;
import actor.ActorApi.ActiveGame;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.FindGame;
import actor.ActorApi.GameIds;
import actor.ActorApi.ModelException;
import actor.ActorApi.OfficialException;
import actor.ActorApi.RetrieveGame;
import actor.ActorApi.RosterException;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.StandingException;
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
	private String standingsDate;
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
		else if(message instanceof FindGame) {
			FindGame findGame = (FindGame)message;
			game = Game.findById(findGame.gameId, processingType);
			String gameDate = DateTimeUtil.getFindDateNaked(game.getDate());
			
			if (standingsDate == null || !standingsDate.equals(gameDate)) {
				System.out.println("Standings not loaded for " + gameDate);
				StandingException se = new StandingException(game.getId(), gameDate);
				getSender().tell(se, getSelf());
			}
			else {
				System.out.println("Standings already loaded for " + gameDate);
				StringBuffer output;
				if (game.getStatus().equals(Status.scheduled) || game.getStatus().equals(Status.finished)) {
					output = new StringBuffer();
					output.append(Utilities.padString('\n' + "Finished Game Ready for Completion -", 40));
					output.append(" " + gameDate);
					output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
					output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
					System.out.println(output.toString());
					
					RetrieveGame rg = new RetrieveGame(game);
					gameXmlStats.tell(rg, getSelf());
				}
				else  {
					output = new StringBuffer();
					output.append(Utilities.padString('\n' + "" + game.getStatus() + " Not Eligible for Completion -", 40));
					output.append(" " + gameDate);
					output.append("-" + game.getBoxScores().get(0).getTeam().getKey() + "-at");
					output.append("-" + game.getBoxScores().get(1).getTeam().getKey());
					System.out.println(output.toString());
					controller.tell(NextGame, getSelf());
				}
			}
		}
		else if(message instanceof ActiveGame) {
			controller.tell(message, getSelf());
		}
		else if(message instanceof CompleteGame) {
			Game game = ((CompleteGame)message).game;
//			String gameDate = DateTimeUtil.getFindDateShort(game.getDate());
			
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			
			BoxScore awayBoxScore = game.getBoxScores().get(0);

		  	Game.update(game, processingType);
		  	System.out.println("Game Complete " + awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
		  	
		  	controller.tell(NextGame, getSelf());
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