package actor;

import static actor.ActorApi.WorkStart;
import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkComplete;

import java.util.List;

import actor.ActorApi.GameId;
import actor.ActorApi.GameIds;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameController extends UntypedActor {
	private final ActorRef gameModel;
	private ActorRef master;
	private int nbrSecondsDelay;
	private List<Long> ids;
	private int i = 0;
	private Long id;
	private GameId gid;

	public GameController(ActorRef listener) {
		gameModel = getContext().actorOf(Props.create(GameModel.class, listener), "gameModel");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			master = getSender();
			nbrSecondsDelay = Integer.parseInt(((ServiceProps) message).delay);
			gameModel.tell(message, getSender());
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
				try {
				    Thread.sleep(nbrSecondsDelay);
				} 
				catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				
				id = ids.get(i);
				gid = new GameId(id);
				gameModel.tell(gid, getSelf());
				i++;
			}
			else {
				master.tell(WorkComplete, getSelf());
			}
		}
		else {
			unhandled(message);
		}
	}
}