package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.util.Date;
import java.util.List;

import models.BoxScore;
import models.Game;
import models.Game.ProcessingType;
import util.DateTime;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.GameId;
import actor.ActorApi.GameIds;
import actor.ActorApi.IncompleteRosterException;
import actor.ActorApi.ModelException;
import actor.ActorApi.ScheduleGame;
import actor.ActorApi.ServiceProps;
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
			propDate = ((ServiceProps) message).date == null ? DateTime.getFindDateShort(new Date()) : ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;
			propSize = ((ServiceProps) message).size == null ? propSize = "0" : ((ServiceProps) message).size;
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			gameXmlStats.tell(message, getSender());
		}
		else if (message.equals(WorkStart)) {					
			List<Long> games = null;
			controller = getSender();
			try {
				if (propTeam == null) {
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
		else if(message instanceof GameId) {
			GameId gameId = (GameId)message;			
			Game game = Game.findById(gameId.game, processingType);
			ScheduleGame sg = new ScheduleGame(game);
			gameXmlStats.tell(sg, getSelf());
		}
		else if(message instanceof CompleteGame) {
			Game game = ((CompleteGame)message).game;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			System.out.println(awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
		  	Game.update(game, processingType);
		  	controller.tell(NextGame, getSelf());
		}
		else if(message instanceof IncompleteRosterException) {
			controller.tell(message, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}