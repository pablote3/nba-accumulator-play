package actor;

import static actor.ActorApi.InitXmlStats;
import static actor.ActorApi.Complete;

import java.util.ArrayList;
import java.util.List;

import models.entity.BoxScore;
import models.entity.Game;
import actor.ActorApi.GameId;
import actor.ActorApi.GameIds;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {
	private final ActorRef xmlStatsActor = getContext().actorOf(Props.create(XmlStats.class), "xmlStatsModel");
	private ActorRef masterActor;
	private String propDate;
	private String propTeam;

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			masterActor = getSender();
			propDate = ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;			
			xmlStatsActor.tell(message, getSelf());
		}
		else if (message.equals(InitXmlStats)) {					
			List<Long> games;
			if (propTeam == null) {
				games = Game.findIdsByDate(propDate);
			}
			else {
				games = new ArrayList<Long>();
				Long id = Game.findIdByDateTeam(propDate, propTeam);
				if (id != null) {
					games.add(id);
				}
			}
			GameIds ids = new GameIds(games);
			masterActor.tell(ids, getSender());
		}
		else if(message instanceof GameId) {
			GameId gameId = (GameId)message;			
			Game game = Game.findById(gameId.game);
			xmlStatsActor.tell(game, getSelf());
		}
		else if(message instanceof Game) {
			Game game = (Game)message;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			System.out.println(awayBoxScore.getTeam().getShortName() +  " " + awayBoxScore.getPoints() + " " + homeBoxScore.getTeam().getShortName() +  " " + homeBoxScore.getPoints());
//		  	game.update();
		  	masterActor.tell(Complete, getSender());
		}		
		else {
			unhandled(message);
		}
	}
}