package akka;
import static actor.ActorApi.Start;
import actor.Listener;
import actor.Master;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

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
}