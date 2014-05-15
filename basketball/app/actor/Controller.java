package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkComplete;
import static actor.ActorApi.WorkStart;

import java.util.List;

import actor.ActorApi.RepeatGame;
import actor.ActorApi.WorkGame;
import actor.ActorApi.GameIds;
import actor.ActorApi.IncompleteRosterException;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.UpdateRoster;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Controller extends UntypedActor {
	private final ActorRef gameModel;
	private final ActorRef rosterModel;
	private ActorRef master;
	private int nbrSecondsDelay;
	private List<Long> ids;
	private int i = 0;

	public Controller(ActorRef listener) {
		gameModel = getContext().actorOf(Props.create(GameModel.class, listener), "gameModel");
		rosterModel = getContext().actorOf(Props.create(RosterModel.class, listener), "rosterModel");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			master = getSender();
			nbrSecondsDelay = Integer.parseInt(((ServiceProps) message).delay);
			gameModel.tell(message, master);
			rosterModel.tell(message, master);
		}
		else if (message.equals(WorkStart)) {
			gameModel.tell(message, getSelf());
		}
		else if (message instanceof GameIds) {
			GameIds gameIds = (GameIds) message;
			ids = gameIds.games;
			getSelf().tell(NextGame, getSelf());
		}
		else if (message.equals(NextGame)) {
			if (i < ids.size()) {
				sleep();
				gameModel.tell(new WorkGame(ids.get(i)), getSelf());
				i++;
			}
			else {
				master.tell(WorkComplete, getSelf());
			}
		}
		else if (message instanceof RepeatGame) {
			sleep();
			Long gameId = ((RepeatGame)message).gameId;
			gameModel.tell(new WorkGame(gameId), getSelf());
		}
		else if(message instanceof IncompleteRosterException) {
			Long gameId = ((IncompleteRosterException) message).gameId;
			String gameDate = ((IncompleteRosterException) message).date;
			String gameTeam = ((IncompleteRosterException) message).team;
		
			if (gameId != null && gameDate != null && gameTeam != null) {
				sleep();
				UpdateRoster updateRoster = new UpdateRoster(gameId, gameDate, gameTeam);
				rosterModel.tell(updateRoster, getSelf());
			}
			else {
				throw new NullPointerException();
			}
		}
		else {
			unhandled(message);
		}
	}

	private void sleep() {
		try {
		    Thread.sleep(nbrSecondsDelay);
		} 
		catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
}