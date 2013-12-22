import java.util.concurrent.TimeUnit;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import scala.concurrent.duration.Duration;
import actor.Listener;
import actor.Master;
import static actor.ActorApi.Start;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

    public class Global extends GlobalSettings {
    	
		private ActorRef listener;
		private ActorRef master;
      
		@Override
		public void onStart(Application app) {
			Logger.info("Application has started");
			System.out.println("Application has started");
			Config config = ConfigFactory.parseString("akka.loglevel = DEBUG \n" + "akka.actor.debug.lifecycle = on");
			ActorSystem system = ActorSystem.create("GameSystem", config);
			listener = system.actorOf(Props.create(Listener.class), "listener");
			master = system.actorOf(Props.create(Master.class, listener), "master");
			master.tell(Start, listener);
			
//		    system.scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS),
//                    Duration.create(10, TimeUnit.SECONDS), master, "tick",
//                    system.dispatcher());
		}  
}
