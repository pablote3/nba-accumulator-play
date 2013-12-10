package akka;
import static actor.ActorApi.Start;
import actor.Master;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class GameAction {
 
	public static void main(String[] args) {
		Config config = ConfigFactory.parseString("akka.loglevel = DEBUG \n" + "akka.actor.debug.lifecycle = on");
		ActorSystem system = ActorSystem.create("GameSystem", config);
		final ActorRef listener = system.actorOf(Props.create(Listener.class));
		final ActorRef master = system.actorOf(Props.create(Master.class, 6000, listener));
		master.tell(Start, listener);
	}

	public static class Listener extends UntypedActor {		
		public void onReceive(Object message) {
//			if (message instanceof PiApproximation) {
//				PiApproximation approximation = (PiApproximation) message;
//				System.out.println(String.format("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s", approximation.getPi(), approximation.getDuration()));
//				getContext().system().shutdown();
//			} 
//			else {
//				unhandled(message);
//			}
		}
	}
}