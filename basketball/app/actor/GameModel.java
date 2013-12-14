package actor;

import static actor.ActorApi.NextGame;
import static actor.ActorApi.WorkStart;

import java.util.ArrayList;
import java.util.List;

import models.entity.BoxScore;
import models.entity.Game;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.GameId;
import actor.ActorApi.GameIds;
import actor.ActorApi.ModelException;
import actor.ActorApi.ScheduleGame;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef xmlStats;
	private ActorRef gameController;
	private String propDate;
	private String propTeam;
	
	public GameModel(ActorRef listener) {
		this.listener = listener;
		xmlStats = getContext().actorOf(Props.create(XmlStats.class, listener), "xmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			propDate = ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;			
			xmlStats.tell(message, getSender());
		}
		else if (message.equals(WorkStart)) {					
			List<Long> games = null;
			gameController = getSender();
			try {
				if (propTeam == null) {
					games = Game.findIdsByDate(propDate);
					if (games == null) {
						throw new NullPointerException();
					}
				}
				else {
					games = new ArrayList<Long>();
					Long id = Game.findIdByDateTeam(propDate, propTeam);
					if (id != null) {
						games.add(id);
					}
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
			Game game = Game.findById(gameId.game);
			ScheduleGame sg = new ScheduleGame(game);
			xmlStats.tell(sg, getSelf());
		}
		else if(message instanceof CompleteGame) {
			Game game = ((CompleteGame)message).game;
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