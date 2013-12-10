package actor;

import java.util.List;

public interface ActorApi {
	public static final Object Start = "Start";
	public static final Object Complete = "Start";
	public static final Object Service = "Service";
	public static final Object InitXmlStats = "InitXmlStats";
	
	public static class ServiceProps {
		public final String date;
		public final String team;
		public final String accessToken;
		public final String userAgentName;
		public final String urlBoxScore;
		
		public ServiceProps(String date, String team, String accessToken, String userAgentName, String urlBoxScore) {
			this.date = date;
			this.team = team;
			this.accessToken = accessToken;
			this.userAgentName = userAgentName;
			this.urlBoxScore = urlBoxScore;
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
	
	public static class GameIds {
		public final List<Long> games;
		
		public GameIds(List<Long> games) {
			this.games = games;
		}			
	}
}