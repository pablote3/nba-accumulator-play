package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.GameIneligible;
import static actor.ActorApi.GameDayIncomplete;
import static actor.ActorApi.StandingsComplete;
import static actor.ActorApi.WorkComplete;
import static actor.ActorApi.WorkStart;

import java.util.List;

import actor.ActorApi.GameComplete;
import actor.ActorApi.GameFind;
import actor.ActorApi.GameIds;
import actor.ActorApi.GameDayConfirmation;
import actor.ActorApi.GameDayComplete;
import actor.ActorApi.RepeatGame;
import actor.ActorApi.RosterComplete;
import actor.ActorApi.RosterException;
import actor.ActorApi.RosterLoad;
import actor.ActorApi.StandingsLoad;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Controller extends UntypedActor {
	private final ActorRef gameModel;
	private final ActorRef rosterModel;
	private final ActorRef standingModel;
	private ActorRef master;
	private int nbrSecondsDelay;
	private List<Long> gameIdList;
	private int gameIndex = 0;

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
			gameIdList = gameIds.games;
			getSelf().tell(NextGame, getSelf());
		}
		else if (message.equals(NextGame)) {
			if (gameIndex < gameIdList.size()) {
				sleep();
				gameModel.tell(new GameFind(gameIdList.get(gameIndex)), getSelf());
				gameIndex++;
			}
			else {
				master.tell(WorkComplete, getSelf());
			}
		}
		else if(message.equals(GameIneligible)) {
			getSelf().tell(NextGame, getSelf());
		}
		else if(message instanceof GameComplete) {
			String gameDate = ((GameComplete) message).gameDate;
			GameDayConfirmation gdc = new GameDayConfirmation(gameDate);
			gameModel.tell(gdc, getSelf());
		}
		else if(message.equals(GameDayIncomplete)) {
			getSelf().tell(NextGame, getSelf());
		}
		else if(message instanceof GameDayComplete) {
			String gameDate = ((GameDayComplete) message).date;
			StandingsLoad sl = new StandingsLoad(gameDate);
			standingModel.tell(sl, getSelf());
		}
		else if (message instanceof RepeatGame) {
			sleep();
			Long gameId = ((RepeatGame)message).gameId;
			gameModel.tell(new GameFind(gameId), getSelf());
		}
		else if(message instanceof RosterException) {
			Long gameId = ((RosterException) message).gameId;
			String gameDate = ((RosterException) message).date;
			String gameTeam = ((RosterException) message).team;
		
			if (gameId != null && gameDate != null && gameTeam != null) {
				sleep();
				RosterLoad rl = new RosterLoad(gameId, gameDate, gameTeam);
				rosterModel.tell(rl, getSelf());
			}
			else {
				throw new NullPointerException();
			}
		}
		else if(message instanceof RosterComplete) {
			Long gameId = ((RosterComplete) message).gameId;
			getSelf().tell(new RepeatGame(gameId), getSelf());
		}
		else if(message.equals(StandingsComplete)) {
			getSelf().tell(NextGame, getSelf());
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