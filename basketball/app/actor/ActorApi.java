package actor;

import java.util.List;

import models.Game;
import models.RosterPlayer;
import models.Standing;

public interface ActorApi {
	public static final Object Start = "Start";
	public static final Object InitializeStart = "InitializeStart";
	public static final Object InitializeComplete = "InitializeComplete";
	public static final Object WorkStart = "WorkStart";
	public static final Object WorkGame = "WorkGame";
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
		public final String fileBoxScore;
		public final String urlRoster;
		public final String fileRoster;
		public final String urlStanding;
		public final String fileStanding;
		public final String delay;
		public final String processType;
		public final String sourceBoxScore;
		public final String sourceRoster;
		public final String sourceStanding;
		
		public ServiceProps(String date, String team, String size, String accessToken, String userAgentName, String urlBoxScore, String fileBoxScore, String urlRoster, String fileRoster, String urlStanding, String fileStanding, String delay, String processType, String sourceBoxScore, String sourceRoster, String sourceStanding) {
			this.date = date;
			this.team = team;
			this.size = size;
			this.accessToken = accessToken;
			this.userAgentName = userAgentName;
			this.urlBoxScore = urlBoxScore;
			this.fileBoxScore = fileBoxScore;
			this.urlRoster = urlRoster;
			this.fileRoster = fileRoster;
			this.urlStanding = urlStanding;
			this.fileStanding = fileStanding;
			this.delay = delay;
			this.processType = processType;
			this.sourceBoxScore = sourceBoxScore;
			this.sourceRoster = sourceRoster;
			this.sourceStanding = sourceStanding;
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
	
	public static class LoadRoster {
		public final Long gameId;
		public final String date;
		public final String team;
		
		public LoadRoster(Long gameId, String date, String team) {
			this.gameId = gameId;
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class RetrieveRoster {
		public final String date;
		public final String team;
		
		public RetrieveRoster(String date, String team) {
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class LoadStandings {
		public final Long gameId;
		public final String date;
		
		public LoadStandings(Long gameId, String date) {
			this.gameId = gameId;
			this.date = date;
		}
	}
	
	public static class RetrieveStandings {
		public final String date;
		
		public RetrieveStandings(String date) {
			this.date = date;
		}
	}
	
	public static class AdjustOpponent {
		public final Game game;
		
		public AdjustOpponent(Game game) {
			this.game = game;
		}
	}
	
	public static class StandingException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public final Long gameId;
		public final String date;
		
		public StandingException(Long gameId, String date) {
			this.gameId = gameId;
			this.date = date;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date);
		}
	}
	
	public static class RosterException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public final Long gameId;
		public final String date;
		public final String team;
		
		public RosterException(Long gameId, String date, String team) {
			this.gameId = gameId;
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class OfficialException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public OfficialException(String msg) {
			super(msg);
		}
	}
	
	public static class GameIds {
		public final List<Long> games;
		
		public GameIds(List<Long> games) {
			this.games = games;
		}			
	}
	
	public static class FindGame {
		public final Long gameId;
		
		public FindGame(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class RepeatGame {
		public final Long gameId;
		
		public RepeatGame(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class RetrieveGame {
		public final Game game;
		
		public RetrieveGame(Game game) {
			this.game = game;
		}
	}
	
	public static class ActiveStandings {
		public final List<Standing> standings;
		
		public ActiveStandings(List<Standing> standings) {
			this.standings = standings;
		}
	}
	
	public static class ActiveRoster {
		public final List<RosterPlayer> rosterPlayers;

		public ActiveRoster(List<RosterPlayer> rosterPlayers) {
			this.rosterPlayers = rosterPlayers;
		}
	}
	
	public static class ActiveGame {
		public final Game game;
		
		public ActiveGame(Game game) {
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