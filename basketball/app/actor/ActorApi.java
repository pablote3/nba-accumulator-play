package actor;

import java.util.List;

public interface ActorApi {
	public static final Object Start = "Start";
	public static final Object InitializeStart = "InitializeStart";
	public static final Object InitializeComplete = "InitializeComplete";
	public static final Object WorkStart = "WorkStart";
	public static final Object NextGame = "NextGame";
	public static final Object WorkComplete = "WorkComplete";
	public static final Object Finish = "Finish";
	
	public static class ServiceProps {
		public final String date;
		public final String team;
		public final String accessToken;
		public final String userAgentName;
		public final String urlBoxScore;
		public final String delay;
		
		public ServiceProps(String date, String team, String accessToken, String userAgentName, String urlBoxScore, String delay) {
			this.date = date;
			this.team = team;
			this.accessToken = accessToken;
			this.userAgentName = userAgentName;
			this.urlBoxScore = urlBoxScore;
			this.delay = delay;
		}			
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class PropertyException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public PropertyException(String msg) {
			super(msg);
		}
	}
	
	public static class ModelException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public ModelException(String msg) {
			super(msg);
		}
	}
	
	public static class XmlStatsException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public XmlStatsException(String msg) {
			super(msg);
		}
	}
	
	public static class GameIds {
		public final List<Long> games;
		
		public GameIds(List<Long> games) {
			this.games = games;
		}			
	}
	
	public static class GameId {
		public final Long game;
		
		public GameId(Long game) {
			this.game = game;
		}
	}
}