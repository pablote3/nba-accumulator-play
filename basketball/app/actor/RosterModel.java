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
import actor.ActorApi.ModelException;
import actor.ActorApi.ScheduleGame;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class RosterModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef rosterXmlStats;
	private ActorRef gameController;
	private String propDate;
	private String propTeam;
	private ProcessingType processingType;
	
	public RosterModel(ActorRef listener) {
		this.listener = listener;
		rosterXmlStats = getContext().actorOf(Props.create(RosterXmlStats.class, listener), "rosterXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			propDate = ((ServiceProps) message).date == null ? DateTime.getFindDateShort(new Date()) : ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			rosterXmlStats.tell(message, getSender());
		}
		else if (message.equals(WorkStart)) {					
//			List<Long> games = null;
//			gameController = getSender();
//			try {
//				if (propTeam == null) {
//					games = Game.findIdsByDateSize(propDate, propSize, processingType);
//				}
//				else {
//					games = Game.findIdsByDateTeamSize(propDate, propTeam, propSize, processingType);
//				}
//				if (games == null) {
//					throw new NullPointerException();
//				}
//			} catch (NullPointerException e) {
//				getContext().stop(getSelf());
//				ModelException me = new ModelException("NoGamesFound");
//				listener.tell(me, getSelf());
//			}
//			GameIds ids = new GameIds(games);
//			getSender().tell(ids, getSelf());
		}
		else if(message instanceof CompleteGame) {
			Game game = ((CompleteGame)message).game;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			System.out.println(awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
		  	Game.update(game, processingType);
		  	gameController.tell(NextGame, getSelf());
		}		
		else {
			unhandled(message);
		}
	}
}