package actor;

import java.util.List;

import models.Game;

public interface ActorApi {
	public static final Object Start = "Start";
	public static final Object InitializeStart = "InitializeStart";
	public static final Object InitializeComplete = "InitializeComplete";
	public static final Object WorkStart = "WorkStart";
	public static final Object NextGame = "NextGame";
	public static final Object WorkComplete = "WorkComplete";
	public static final Object Wait = "Wait";
	public static final Object Finish = "Finish";
	
	public static class ServiceProps {
		public final String date;
		public final String team;
		public final String size;
		public final String accessToken;
		public final String userAgentName;
		public final String urlBoxScore;
		public final String urlRoster;
		public final String delay;
		public final String processType;
		
		public ServiceProps(String date, String team, String size, String accessToken, String userAgentName, String urlBoxScore, String urlRoster, String delay, String processType) {
			this.date = date;
			this.team = team;
			this.size = size;
			this.accessToken = accessToken;
			this.userAgentName = userAgentName;
			this.urlBoxScore = urlBoxScore;
			this.urlRoster = urlRoster;
			this.delay = delay;
			this.processType = processType;
		}			
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team, size);
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
	
	public static class IncompleteRosterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public final String date;
		public final String team;
		
		public IncompleteRosterException(String date, String team) {
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
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
	
	public static class ScheduleGame {
		public final Game game;
		
		public ScheduleGame(Game game) {
			this.game = game;
		}
	}
	
	public static class CompleteGame {
		public final Game game;
		
		public CompleteGame(Game game) {
			this.game = game;
		}
	}
}