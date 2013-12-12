package akka;
import static actor.ActorApi.Start;
import static actor.ActorApi.Finish;
import actor.Master;
import actor.ActorApi.PropertyException;
import actor.ActorApi.ModelException;
import actor.ActorApi.XmlStatsException;
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
		final ActorRef listener = system.actorOf(Props.create(Listener.class), "listener");
		final ActorRef master = system.actorOf(Props.create(Master.class, listener), "master");
		master.tell(Start, listener);
	}

	public static class Listener extends UntypedActor {		
		public void onReceive(Object message) {
			if (message instanceof PropertyException) {
				PropertyException pe = (PropertyException) message;
				System.out.println("Property Exception " + pe.getMessage());
				getContext().system().shutdown();
			}
			if (message instanceof ModelException) {
				ModelException pe = (ModelException) message;
				System.out.println("Model Exception " + pe.getMessage());
				getContext().system().shutdown();
			}
			if (message instanceof XmlStatsException) {
				XmlStatsException pe = (XmlStatsException) message;
				System.out.println("XmlStats Exception " + pe.getMessage());
				getContext().system().shutdown();
			}
			else if (message.equals(Finish)) {
				System.out.println("Mission Compete");
				getContext().system().shutdown();
			}
			else {
				unhandled(message);
			}
		}
	}
}