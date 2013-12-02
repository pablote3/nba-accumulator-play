package models.partial;

public class GameKey {
	private String date;
	private String homeTeamKey;
	private String awayTeamKey;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getHomeTeamKey() {
		return homeTeamKey;
	}
	public void setHomeTeamKey(String homeTeamKey) {
		this.homeTeamKey = homeTeamKey;
	}
	public String getAwayTeamKey() {
		return awayTeamKey;
	}
	public void setAwayTeamKey(String awayTeamKey) {
		this.awayTeamKey = awayTeamKey;
	}
	public String toString() {
		return new StringBuffer()
		.append("  date: " + this.date)
		.append("  homeTeamKey: " + this.homeTeamKey)
		.append("  awayTeamKey: " + this.awayTeamKey)
		.toString();
	}
}
