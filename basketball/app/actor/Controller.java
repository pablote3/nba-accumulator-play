package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkComplete;
import static actor.ActorApi.WorkStart;

import java.util.List;

import actor.ActorApi.ActiveGame;
import actor.ActorApi.AdjustOpponent;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.FindGame;
import actor.ActorApi.GameIds;
import actor.ActorApi.LoadRoster;
import actor.ActorApi.LoadStandings;
import actor.ActorApi.RepeatGame;
import actor.ActorApi.RosterException;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.StandingException;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Controller extends UntypedActor {
	private final ActorRef gameModel;
	private final ActorRef rosterModel;
	private final ActorRef standingModel;
	private ActorRef master;
	private int nbrSecondsDelay;
	private List<Long> ids;
	private int i = 0;

	public Controller(ActorRef listener) {
		gameModel = getContext().actorOf(Props.create(GameModel.class, listener), "gameModel");
		rosterModel = getContext().actorOf(Props.create(RosterModel.class, listener), "rosterModel");
		standingModel = getContext().actorOf(Props.create(StandingModel.class, listener), "standingModel");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			master = getSender();
			nbrSecondsDelay = Integer.parseInt(((ServiceProps) message).delay);
			gameModel.tell(message, master);
			rosterModel.tell(message, master);
			standingModel.tell(message, master);
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
				gameModel.tell(new FindGame(ids.get(i)), getSelf());
				i++;
			}
			else {
				master.tell(WorkComplete, getSelf());
			}
		}
		else if(message instanceof ActiveGame) {
			standingModel.tell(new AdjustOpponent(((ActiveGame) message).game), getSelf());
		}
		else if(message instanceof CompleteGame) {
			gameModel.tell(message, getSelf());
		}
		else if (message instanceof RepeatGame) {
			sleep();
			Long gameId = ((RepeatGame)message).gameId;
			gameModel.tell(new FindGame(gameId), getSelf());
		}
		else if(message instanceof RosterException) {
			Long gameId = ((RosterException) message).gameId;
			String gameDate = ((RosterException) message).date;
			String gameTeam = ((RosterException) message).team;
		
			if (gameId != null && gameDate != null && gameTeam != null) {
				sleep();
				LoadRoster lr = new LoadRoster(gameId, gameDate, gameTeam);
				rosterModel.tell(lr, getSelf());
			}
			else {
				throw new NullPointerException();
			}
		}
		else if (message instanceof StandingException) {
			Long gameId = ((StandingException) message).gameId;
			String gameDate = ((StandingException)message).date;
			LoadStandings ls = new LoadStandings(gameId, gameDate);
			standingModel.tell(ls, getSelf());
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