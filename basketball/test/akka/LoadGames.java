package akka;
import static akka.LoadGames.PropertyApi.GameDay;
import static akka.LoadGames.PropertyApi.XmlStats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import util.FileIO;
import akka.LoadGames.PropertyApi.GameDayProps;
import akka.LoadGames.PropertyApi.PropertyException;
import akka.LoadGames.PropertyApi.XmlStatProps;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

public class LoadGames {
 
	public static void main(String[] args) {
		LoadGames pi = new LoadGames();
		pi.calculate(4, 10000, 10000);
	}
 
	static class Calculate { }
 
	static class Work {
		private final int start;
		private final int nrOfElements;
 
		public Work(int start, int nrOfElements) {
			this.start = start;
			this.nrOfElements = nrOfElements;
 		}

 		public int getStart() {
			return start;
		}
 
 		public int getNrOfElements() {
			return nrOfElements;
		}
	}

  static class Result {
		private final double value;
		
		public Result(double value) {
			this.value = value;
		}

  	public double getValue() {
			return value;
		}
 	}
 
 	static class PiApproximation {
		private final double pi;
		private final Duration duration;

 		public PiApproximation(double pi, Duration duration) {
			this.pi = pi;
			this.duration = duration;
		}
 
		public double getPi() {
			return pi;
		}

		public Duration getDuration() {
			return duration;
		}
	}
 	
	public static class Worker extends UntypedActor {

		private double calculatePiFor(int start, int nrOfElements) {
			double acc = 0.0;

			for (int i = start * nrOfElements; i <= ((start + 1) * nrOfElements - 1); i++) {
				acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
			}
			return acc;
		}

		public void onReceive(Object message) {
			if (message instanceof Work) {
				Work work = (Work) message;
				double result = calculatePiFor(work.getStart(), work.getNrOfElements());
				getSender().tell(new Result(result), getSelf());
			} 
			else {
				unhandled(message);
			}
		}
	}

	public interface PropertyApi {
		public static final Object GameDay = "GameDay";
		public static final Object XmlStats = "XmlStats";
		
		public static class GameDayProps {
			public final String date;
			public final String team;
			
			public GameDayProps(String date, String team) {
				this.date = date;
				this.team = team;
			}			
			public String toString() {
				return String.format("%s(%s)", getClass().getSimpleName(), date, team);
			}
		}
		
		public static class XmlStatProps {
			public final String accessToken;
			public final String userAgentName;
			public final String urlBoxScore;
			
			public XmlStatProps(String accessToken, String userAgentName, String urlBoxScore) {
				this.accessToken = accessToken;
				this.userAgentName = userAgentName;
				this.urlBoxScore = urlBoxScore;
			}			
			public String toString() {
				return String.format("%s(%s)", getClass().getSimpleName(), userAgentName);
			}
		}
		
		public static class PropertyException extends RuntimeException {
			private static final long serialVersionUID = 1L;
			
			public PropertyException(String msg) {
				super(msg);
			}
		}
	}
	
	public static class Property extends UntypedActor {
		private Properties props;
		private Properties getProperties() throws PropertyException {
			if (props == null) {
				try {
					String path = FileIO.getPropertyPath("config.basketball");
					props = FileIO.loadProperties(path + "//properties//service.properties");
				}
				catch (FileNotFoundException e) {
					throw new PropertyException("FileNotFoundException");
				}
				catch (IOException e) {
					throw new PropertyException("IOException");
				}
			}
			return props;
		}

		public void onReceive(Object message) {
			if (message.equals(GameDay)) {
				Properties props = getProperties();
				String accessToken = props.getProperty("xmlstats.accessToken");
				String userAgentName = props.getProperty("xmlstats.userAgentName");
				String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
				XmlStatProps xmlStatProps = new XmlStatProps(accessToken, userAgentName, urlBoxScore);
				getSender().tell(xmlStatProps, getSelf());
			} 
			else if (message.equals(XmlStats)) {
				Properties props = getProperties();
				String date = props.getProperty("gameday.date");
				String team = props.getProperty("gameday.team");
				GameDayProps gameDayProps = new GameDayProps(date, team);
				getSender().tell(gameDayProps, getSelf());				
			}
			else {
				unhandled(message);
			}
		}
	}
	
	public static class Master extends UntypedActor {
		private final int nrOfMessages;
		private final int nrOfElements;
		private double pi;
		private int nrOfResults;
		private final long start = System.currentTimeMillis();
		private final ActorRef listener;
		private final ActorRef workerRouter;

 		public Master(final int nrOfWorkers, int nrOfMessages, int nrOfElements, ActorRef listener) {
			this.nrOfMessages = nrOfMessages;
			this.nrOfElements = nrOfElements;
			this.listener = listener;
			workerRouter = this.getContext().actorOf(Props.create(Worker.class).withRouter(new RoundRobinRouter(nrOfWorkers)), "workerRouter");
		}

 		public void onReceive(Object message) {
			if (message instanceof Calculate) {
				for (int start = 0; start < nrOfMessages; start++) {
					workerRouter.tell(new Work(start, nrOfElements), getSelf());
				}
			} 
			else if (message instanceof Result) {
				Result result = (Result) message;
				pi += result.getValue();
				nrOfResults += 1;
				if (nrOfResults == nrOfMessages) {
					// Send the result to the listener
					Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
					listener.tell(new PiApproximation(pi, duration), getSelf());
					
					// Stops this actor and all its supervised children
					getContext().stop(getSelf());
				}
			} 
			else {
				unhandled(message);
			}
		}
	}
	public static class Listener extends UntypedActor {
		
		public void onReceive(Object message) {
			if (message instanceof PiApproximation) {
				PiApproximation approximation = (PiApproximation) message;
				System.out.println(String.format("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s", approximation.getPi(), approximation.getDuration()));
				getContext().system().shutdown();
			} 
			else {
				unhandled(message);
			}
		}
	}

	public void calculate(final int nrOfWorkers, final int nrOfElements, final int nrOfMessages) {
		// Create an Akka system
		ActorSystem system = ActorSystem.create("PiSystem");

 		// create the result listener, which will print the result and shutdown the system
		final ActorRef listener = system.actorOf(Props.create(Listener.class));

		// create the master
		final ActorRef master = system.actorOf(Props.create(Master.class, nrOfWorkers, nrOfMessages, nrOfElements, listener));

		// start the calculation
		master.tell(new Calculate(), master);
	}
}