package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.NextStanding;
import static actor.ActorApi.GameIneligible;
import static actor.ActorApi.StandingTeamComplete;
import static actor.ActorApi.WorkStart;
import static actor.ActorApi.WorkComplete;

import java.util.ArrayList;
import java.util.List;

import util.DateTimeUtil;
import models.Standing;
import actor.ActorApi.GameComplete;
import actor.ActorApi.GameFind;
import actor.ActorApi.GameIds;
import actor.ActorApi.RepeatGame;
import actor.ActorApi.RosterLoad;
import actor.ActorApi.RosterComplete;
import actor.ActorApi.RosterException;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.StandingsActive;
import actor.ActorApi.StandingsLoad;
import actor.ActorApi.StandingTeamAdjust;
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
	private List<Standing> standingsList;
	private String standingDate;
	private int gameIndex = 0;
	private int standingIndex = 0;

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
				StandingsLoad sl = new StandingsLoad(standingDate);
				standingModel.tell(sl, getSelf());
			}
		}
		else if(message.equals(GameIneligible)) {
			getSelf().tell(NextGame, getSelf());
		}
		else if(message instanceof GameComplete) {
			String gameDate = ((GameComplete) message).gameDate;
			if(!standingDate.equals(gameDate)) {
				StandingsLoad sl = new StandingsLoad(standingDate);
				standingDate = gameDate;
				standingModel.tell(sl, getSelf());
			}
			getSelf().tell(NextGame, getSelf());
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
		else if(message instanceof StandingsActive) {
			StandingsActive activeStandings = (StandingsActive) message;
			standingsList = new ArrayList<Standing>(activeStandings.standings);
			getSelf().tell(NextStanding, getSelf());
		}
		else if (message.equals(NextStanding)) {
			if (standingIndex < standingsList.size()) {
				String standingDate = DateTimeUtil.getFindDateShort(standingsList.get(standingIndex).getDate());
				String standingTeam = standingsList.get(standingIndex).getTeam().getKey();
				standingModel.tell(new StandingTeamAdjust(standingDate, standingTeam), getSelf());
			}
			else {
				master.tell(WorkComplete, getSelf());
			}
		}
		else if(message.equals(StandingTeamComplete)) {
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