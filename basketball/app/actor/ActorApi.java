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
	public static final Object NextStanding = "NextStanding";
	public static final Object StandingsComplete = "StandingsComplete";
	public static final Object GameIneligible = "GameIneligible";
	public static final Object GameDayIncomplete = "GameDayIncomplete";
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
	
	public static class RosterLoad {
		public final Long gameId;
		public final String date;
		public final String team;
		
		public RosterLoad(Long gameId, String date, String team) {
			this.gameId = gameId;
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class RosterRetrieve {
		public final String date;
		public final String team;
		
		public RosterRetrieve(String date, String team) {
			this.date = date;
			this.team = team;
		}
		
		public String toString() {
			return String.format("%s(%s)", getClass().getSimpleName(), date, team);
		}
	}
	
	public static class RosterActive {
		public final List<RosterPlayer> rosterPlayers;

		public RosterActive(List<RosterPlayer> rosterPlayers) {
			this.rosterPlayers = rosterPlayers;
		}
	}
	
	public static class RosterComplete {
		public final Long gameId;
		
		public RosterComplete(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class StandingsLoad {
		public final String date;
		
		public StandingsLoad(String date) {
			this.date = date;
		}
	}
	
	
	public static class GameDayConfirmation {
		public final String date;
		
		public GameDayConfirmation(String date) {
			this.date = date;
		}
	}
	
	public static class GameDayComplete {
		public final String date;
		
		public GameDayComplete(String date) {
			this.date = date;
		}
	}
	
	public static class StandingsRetrieve {
		public final String date;
		
		public StandingsRetrieve(String date) {
			this.date = date;
		}
	}
	
	public static class StandingsActive {
		public final List<Standing> standings;
		
		public StandingsActive(List<Standing> standings) {
			this.standings = standings;
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
	
	public static class GameFind {
		public final Long gameId;
		
		public GameFind(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class RepeatGame {
		public final Long gameId;
		
		public RepeatGame(Long gameId) {
			this.gameId = gameId;
		}
	}
	
	public static class GameRetrieve {
		public final Game game;
		
		public GameRetrieve(Game game) {
			this.game = game;
		}
	}
	
	public static class GameActive {
		public final Game game;
		
		public GameActive(Game game) {
			this.game = game;
		}
	}
	
	public static class GameComplete {
		public final String gameDate;
		
		public GameComplete(String gameDate) {
			this.gameDate = gameDate;
		}
	}
}