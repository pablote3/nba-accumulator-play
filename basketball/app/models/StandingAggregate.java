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
	
	private String teamKey;
	private Integer sumWins;
	private Integer sumGamesPlayed;

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
	
	public String toString() {
		return new StringBuffer()
			.append("  sumWins: " + this.sumWins)
			.append("  sumGamesPlayed: " + this.sumGamesPlayed)
			.toString();
	}
}