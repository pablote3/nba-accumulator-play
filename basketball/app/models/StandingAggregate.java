package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

import com.avaje.ebean.annotation.Sql;

@Entity
@Sql
public class StandingAggregate extends Model {
	private static final long serialVersionUID = 1L;
	@OneToOne
	Standing standing;
	
	public String teamKey;
	public Integer sumWins;
	public Integer sumGamesPlayed;
//	private Float avgWinPercent;

	public String getTeamKey() {
		return teamKey;
	}

	public void setTeamKey(String teamKey) {
		this.teamKey = teamKey;
	}

	public Integer getSumWins() {
		return sumWins;
	}

	public void setSumWins(Integer sumWins) {
		this.sumWins = sumWins;
	}

	public Integer getSumGamesPlayed() {
		return sumGamesPlayed;
	}

	public void setSumGamesPlayed(Integer sumGamesPlayed) {
		this.sumGamesPlayed = sumGamesPlayed;
	}

//	public Float getAvgWinPercent() {
//		return avgWinPercent;
//	}
//
//	public void setAvgWinPercent(Float avgWinPercent) {
//		this.avgWinPercent = avgWinPercent;
//	}
	
	public String toString() {
		return new StringBuffer()
//			.append("\n" + this.teamKey + "\n")
			.append("  sumWins: " + this.sumWins)
			.append("  sumGamesPlayed: " + this.sumGamesPlayed)
//			.append("  avgWinPercent: " + this.avgWinPercent)
			.toString();
	}
}