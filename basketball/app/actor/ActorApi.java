package actor;

import java.util.List;

import json.xmlStats.Standings;
import models.Game;
import models.RosterPlayer;

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
		public final String fileBoxScore;
		public final String urlRoster;
		public final String fileRoster;
		public final String urlStanding;
		public final String fileStanding;
		public final String delay;
		public final String processType;
		public final String sourceBoxScore;
		public final String sourceRoster;
		
		public ServiceProps(String date, String team, String size, String accessToken, String userAgentName, String urlBoxScore, String fileBoxScore, String urlRoster, String fileRoster, String urlStanding, String fileStanding, String delay, String processType, String sourceBoxScore, String sourceRoster) {
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
		public final Long gameId;
		
		public IncompleteRosterException(Long gameId, String date, String team) {
			this.gameId = gameId;
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class UpdateRoster {
		public final Long gameId;
		public final String date;
		public final String team;
		
		public UpdateRoster(Long gameId, String date, String team) {
			this.gameId = gameId;
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class RetrieveStandings {
		public final String date;
		
		public RetrieveStandings(String date) {
			this.date = date;
		}
	}
	
	public static class IncompleteOfficialException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public IncompleteOfficialException(String msg) {
			super(msg);
		}
	}
	
	public static class GameIds {
		public final List<Long> games;
		
		public GameIds(List<Long> games) {
			this.games = games;
		}			
	}
	
	public static class WorkGame {
		public final Long gameId;
		
		public WorkGame(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class RepeatGame {
		public final Long gameId;
		
		public RepeatGame(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class ScheduleGame {
		public final Game game;
		
		public ScheduleGame(Game game) {
			this.game = game;
		}
	}
	
	public static class CompleteBoxScore {
		public final Game game;
		
		public CompleteBoxScore(Game game) {
			this.game = game;
		}
	}
	
	public static class CompleteGame {
		public final Game game;
		
		public CompleteGame(Game game) {
			this.game = game;
		}
	}
	
	public static class ActiveStandings {
		public final Standings standings;
		
		public ActiveStandings(Standings standings) {
			this.standings = standings;
		}
	}
	
	public static class ActiveRosterPlayers {
		public final List<RosterPlayer> rosterPlayers;

		public ActiveRosterPlayers(List<RosterPlayer> rosterPlayers) {
			this.rosterPlayers = rosterPlayers;
		}
	}
}