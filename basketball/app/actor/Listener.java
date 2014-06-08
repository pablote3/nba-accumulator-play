package actor;

import static actor.ActorApi.Wait;
import static actor.ActorApi.Finish;
import actor.ActorApi.ModelException;
import actor.ActorApi.PropertyException;
import actor.ActorApi.XmlStatsException;
import akka.actor.UntypedActor;

public class Listener extends UntypedActor {		
	public void onReceive(Object message) {
		if (message instanceof PropertyException) {
			PropertyException pe = (PropertyException) message;
			System.out.println("Property Exception " + pe.getMessage() + " - " + getSender());
		}
		else if (message instanceof ModelException) {
			ModelException pe = (ModelException) message;
			System.out.println("Model Exception " + pe.getMessage() + " - " + getSender());
		}
		else if (message instanceof XmlStatsException) {
			XmlStatsException pe = (XmlStatsException) message;
			System.out.println("XmlStats Exception " + pe.getMessage() + " - " + getSender());
		}
		else if (message.equals(Wait)) {
			System.out.println("Online Mission Compete");	
		}
		else if (message.equals(Finish)) {
			System.out.println("Batch Mission Compete");
		}
		else {
			unhandled(message);
		}
		getContext().system().shutdown();
		System.exit(0);
	}
}