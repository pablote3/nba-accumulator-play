package actor;

import models.Game;
import models.Game.ProcessingType;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.UpdateRoster;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class RosterModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef rosterXmlStats;
	private ActorRef controller;
	private String gameDate;
	private String gameTeam;
	private ProcessingType processingType;
	
	public RosterModel(ActorRef listener) {
		this.listener = listener;
		rosterXmlStats = getContext().actorOf(Props.create(RosterXmlStats.class, listener), "rosterXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			rosterXmlStats.tell(message, getSender());
		}
		else if(message instanceof UpdateRoster) {
			controller = getSender();			
			gameDate = ((UpdateRoster) message).date;
			gameTeam = ((UpdateRoster) message).team;
		
			rosterXmlStats.tell(message, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}