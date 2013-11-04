package actor;

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