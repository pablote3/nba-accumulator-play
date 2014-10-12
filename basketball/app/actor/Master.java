package actor;

import static actor.ActorApi.Start;
import static actor.ActorApi.InitializeStart;
import static actor.ActorApi.InitializeComplete;
import static actor.ActorApi.WorkStart;
import static actor.ActorApi.WorkComplete;
import static actor.ActorApi.Wait;
import static actor.ActorApi.Finish;
import models.Game;
import models.Game.ProcessingType;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	private ActorRef listener;
	private final ActorRef controller;
	private ProcessingType processingType;
	int synchThreadCount = 0;
	
	public Master(ActorRef listener) {
		this.listener = listener;
		controller = getContext().actorOf(Props.create(Controller.class, listener), "controller");
	}

	public void onReceive(Object message) {
		if (message.equals(Start)) {
			final ActorRef property = getContext().actorOf(Props.create(Property.class, listener), "property");
			property.tell(InitializeStart, getSelf());
		}
		else if (message instanceof ServiceProps) {
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			controller.tell(message, getSelf());	
		}
		else if (message.equals(InitializeComplete)) {
			if (synchThreadCount < 2)
				synchThreadCount++;
			else
				controller.tell(WorkStart, getSelf());	
		} 
		else if (message.equals(WorkComplete)) {
			if (processingType.equals(ProcessingType.batch))
				listener.tell(Finish, getSelf());
			else if (processingType.equals(ProcessingType.online))
				listener.tell(Wait, getSelf());
		} 
		else {
			unhandled(message);
		}
	}
}