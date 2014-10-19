package actor;

import java.util.ArrayList;
import java.util.Arrays;

import util.DateTimeUtil;
import models.BoxScore;
import models.Game;
import models.Standing;
import actor.ActorApi.ActiveStandings;
import actor.ActorApi.CompleteBoxScore;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.RetrieveStandings;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class StandingModel extends UntypedActor {
	@SuppressWarnings("unused")
	private ActorRef listener;
	
	private final ActorRef standingXmlStats;
	private ActorRef controller;
	private Game game;
	
	public StandingModel(ActorRef listener) {
		this.listener = listener;
		standingXmlStats = getContext().actorOf(Props.create(StandingXmlStats.class, listener), "standingXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			standingXmlStats.tell(message, getSender());
		}
		else if(message instanceof CompleteBoxScore) {
			controller = getSender();
			CompleteBoxScore cbs = (CompleteBoxScore)message;
			game = cbs.game;
			
			RetrieveStandings rs = new RetrieveStandings(DateTimeUtil.getFindDateNaked(game.getDate()));
			standingXmlStats.tell(rs, getSelf());
		}
		else if(message instanceof ActiveStandings) {
			ActiveStandings activeStandings = (ActiveStandings) message;
			ArrayList<Standing> standings = new ArrayList<Standing>(Arrays.asList(activeStandings.standings.standing));

			BoxScore awayBoxScore = game.getBoxScores().get(0);
			String awayTeamKey = awayBoxScore.getTeam().getKey();
			for (int i = 0; i < standings.size(); i++)  {
				if (standings.get(i).getTeamKey().equals(awayTeamKey))  {
					awayBoxScore.getStandings().add(standings.get(i));
					break;
				}
			}

			BoxScore homeBoxScore = game.getBoxScores().get(1);
			String homeTeamKey = homeBoxScore.getTeam().getKey();
			for (int i = 0; i < standings.size(); i++)  {
				if (standings.get(i).getTeamKey().equals(homeTeamKey))  {
					homeBoxScore.getStandings().add(standings.get(i));
					break;
				}
			}
			CompleteGame cg = new CompleteGame(game);
			controller.tell(cg, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}