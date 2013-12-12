package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.util.ArrayList;
import java.util.List;

import models.entity.BoxScore;
import models.entity.Game;
import actor.ActorApi.GameId;
import actor.ActorApi.GameIds;
import actor.ActorApi.ModelException;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef xmlStats = getContext().actorOf(Props.create(XmlStats.class, listener));
	private ActorRef gameController;
	private String propDate;
	private String propTeam;
	
	public GameModel(ActorRef listener) {
		this.listener = listener;
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			propDate = ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;			
			xmlStats.tell(message, getSender());
		}
		else if (message.equals(WorkStart)) {					
			List<Long> games;
			gameController = getSender();
			if (propTeam == null) {
				games = Game.findIdsByDate(propDate);
				if (games == null) {
					listener.tell(new ModelException("GamesNotFound"), getSelf());
				}
			}
			else {
				games = new ArrayList<Long>();
				Long id = Game.findIdByDateTeam(propDate, propTeam);
				if (id != null) {
					games.add(id);
				}
				else {
					listener.tell(new ModelException("GameNotFound"), getSelf());
				}
			}
			GameIds ids = new GameIds(games);
			getSender().tell(ids, getSelf());
		}
		else if(message instanceof GameId) {
			GameId gameId = (GameId)message;			
			Game game = Game.findById(gameId.game);
			xmlStats.tell(game, getSelf());
		}
		else if(message instanceof Game) {
			Game game = (Game)message;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			System.out.println(awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
//		  	game.update();
		  	gameController.tell(NextGame, getSelf());
		}		
		else {
			unhandled(message);
		}
	}
}