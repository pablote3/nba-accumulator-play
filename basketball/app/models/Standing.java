package models;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.Game.ProcessingType;

import org.joda.time.DateTime;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Standing extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="standing_seq", initialValue=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	
	@ManyToOne
	@JoinColumn(name="boxscore_id", referencedColumnName="id", nullable=false)
	private BoxScore boxScore;
	public BoxScore getBoxScore() {
		return boxScore;
	}
	public void setBoxScore(BoxScore boxScore) {
		this.boxScore = boxScore;
	}
	
	@Column(name="teamKey", nullable=false)
	@JsonProperty("team_id")
	private String teamKey;
	public String getTeamKey() {
		return teamKey;
	}
	public void setTeamKey(String teamKey) {
		this.teamKey = teamKey;
	}
	
	@Column(name="gameDate", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private DateTime gameDate;
	public DateTime getGameDate() {
		return gameDate;
	}
	public void setGameDate(DateTime gameDate) {
		this.gameDate = gameDate;
	}
	
	@Column(name="rank", nullable=false)
	private Short rank;
	public Short getRank() {
		return rank;
	}
	public void setRank(Short rank) {
		this.rank = rank;
	}
	
	@Column(name="ordinalRank", nullable=false)
	@JsonProperty("ordinal_rank")
	private String ordinalRank;
	public String getOrdinalRank() {
		return ordinalRank;
	}
	public void setOrdinalRank(String ordinalRank) {
		this.ordinalRank = ordinalRank;
	}
	
	@Column(name="gamesWon", nullable=false)
	@JsonProperty("won")
	private Short gamesWon;
	public Short getGamesWon() {
		return gamesWon;
	}
	public void setGamesWon(Short gamesWon) {
		this.gamesWon = gamesWon;
	}
	@Column(name="gamesLost", nullable=false)
	@JsonProperty("lost")
	private Short gamesLost;
	public Short getGamesLost() {
		return gamesLost;
	}
	public void setGamesLost(Short gamesLost) {
		this.gamesLost = gamesLost;
	}
	
	@Column(name="streak", nullable=false)
	private String streak;
	public String getStreak() {
		return streak;
	}
	public void setStreak(String streak) {
		this.streak = streak;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="streakType", length=4, nullable=false)
	@JsonProperty("streak_type")
	private StreakType streakType;
	public StreakType getStreakType() {
		return streakType;
	}
	public void setStreakType(StreakType streakType) {
		this.streakType = streakType;
	}
	public enum StreakType {
        @EnumValue("Win") win,
        @EnumValue("Loss") loss,
    }
	
	@Column(name="streakTotal", nullable=false)
	@JsonProperty("streak_total")
	private Short streakTotal;
	public Short getStreakTotal() {
		return streakTotal;
	}
	public void setStreakTotal(Short streakTotal) {
		this.streakTotal = streakTotal;
	}
	
	@Column(name="gamesBack", nullable=false)
	@JsonProperty("games_back")
	private Float gamesBack;
	public Float getGamesBack() {
		return gamesBack;
	}
	public void setGamesBack(Float gamesBack) {
		this.gamesBack = gamesBack;
	}
	
	@Column(name="pointsFor", nullable=false)
	@JsonProperty("points_for")
	private Short pointsFor;
	public Short getPointsFor() {
		return pointsFor;
	}
	public void setPointsFor(Short pointsFor) {
		this.pointsFor = pointsFor;
	}
	
	@Column(name="pointsAgainst", nullable=false)
	@JsonProperty("points_against")
	private Short pointsAgainst;
	public Short getPointsAgainst() {
		return pointsAgainst;
	}
	public void setPointsAgainst(Short pointsAgainst) {
		this.pointsAgainst = pointsAgainst;
	}
	
	@Column(name="homeWins", nullable=false)
	@JsonProperty("home_won")
	private Short homeWins;
	public Short getHomeWins() {
		return homeWins;
	}
	public void setHomeWins(Short homeWins) {
		this.homeWins = homeWins;
	}
	@Column(name="homeLosses", nullable=false)
	@JsonProperty("home_lost")
	private Short homeLosses;
	public Short getHomeLosses() {
		return homeLosses;
	}
	public void setHomeLosses(Short homeLosses) {
		this.homeLosses = homeLosses;
	}
	@Column(name="awayWins", nullable=false)
	@JsonProperty("away_won")
	private Short awayWins;
	public Short getAwayWins() {
		return awayWins;
	}
	public void setAwayWins(Short awayWins) {
		this.awayWins = awayWins;
	}
	@Column(name="awayLosses", nullable=false)
	@JsonProperty("away_lost")
	private Short awayLosses;
	public Short getAwayLosses() {
		return awayLosses;
	}
	public void setAwayLosses(Short awayLosses) {
		this.awayLosses = awayLosses;
	}
	
	@Column(name="conferenceWins", nullable=false)
	@JsonProperty("conference_won")
	private Short conferenceWins;
	public Short getConferenceWins() {
		return conferenceWins;
	}
	public void setConferenceWins(Short conferenceWins) {
		this.conferenceWins = conferenceWins;
	}
	@Column(name="conferenceLosses", nullable=false)
	@JsonProperty("conference_lost")
	private Short conferenceLosses;
	public Short getConferenceLosses() {
		return conferenceLosses;
	}
	public void setConferenceLosses(Short conferenceLosses) {
		this.conferenceLosses = conferenceLosses;
	}
	
	@Column(name="lastFive", nullable=false)
	@JsonProperty("last_five")
	private String lastFive;
	public String getLastFive() {
		return lastFive;
	}
	public void setLastFive(String lastFive) {
		this.lastFive = lastFive;
	}
	@Column(name="lastTen", nullable=false)
	@JsonProperty("last_ten")
	private String lastTen;
	public String getLastTen() {
		return lastTen;
	}
	public void setLastTen(String lastTen) {
		this.lastTen = lastTen;
	}
	
	@Column(name="gamesPlayed", nullable=false)
	@JsonProperty("games_played")
	private Short gamesPlayed;
	public Short getGamesPlayed() {
		return gamesPlayed;
	}
	public void setGamesPlayed(Short gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	
	@Column(name="pointsScoredPerGame", nullable=false)
	@JsonProperty("points_scored_per_game")
	private Float pointsScoredPerGame;
	public Float getPointsScoredPerGame() {
		return pointsScoredPerGame;
	}
	public void setPointsScoredPerGame(Float pointsScoredPerGame) {
		this.pointsScoredPerGame = pointsScoredPerGame;
	}
	@Column(name="pointsAllowedPerGame", nullable=false)
	@JsonProperty("points_allowed_per_game")
	private Float pointsAllowedPerGame;
	public Float getPointsAllowedPerGame() {
		return pointsAllowedPerGame;
	}
	public void setPointsAllowedPerGame(Float pointsAllowedPerGame) {
		this.pointsAllowedPerGame = pointsAllowedPerGame;
	}
	
	@Column(name="winPercentage", nullable=false)
	@JsonProperty("win_percentage")
	private Float winPercentage;
	public Float getWinPercentage() {
		return winPercentage;
	}
	public void setWinPercentage(Float winPercentage) {
		this.winPercentage = winPercentage;
	}
	
	@Column(name="pointDifferential", nullable=false)
	@JsonProperty("point_differential")
	private Short pointDifferential;
	public Short getPointDifferential() {
		return pointDifferential;
	}
	public void setPointDifferential(Short pointDifferential) {
		this.pointDifferential = pointDifferential;
	}
	
	@Column(name="pointDifferentialPerGame", nullable=false)
	@JsonProperty("point_differential_per_game")
	private Float pointDifferentialPerGame;
	public Float getPointDifferentialPerGame() {
		return pointDifferentialPerGame;
	}
	public void setPointDifferentialPerGame(Float pointDifferentialPerGame) {
		this.pointDifferentialPerGame = pointDifferentialPerGame;
	}
	
	@Column(name="opptOpptWins", nullable=false)
	private Integer opptOpptWins;
	public Integer getOpptOpptWins() {
		return opptOpptWins;
	}
	public void setOpptOpptWins(Integer opptOpptWins) {
		this.opptOpptWins = opptOpptWins;
	}
	
	@Column(name="opptOpptGamesPlayed", nullable=false)
	private Integer opptOpptGamesPlayed;
	public Integer getOpptOpptGamesPlayed() {
		return opptOpptGamesPlayed;
	}
	public void setOpptOpptGamesPlayed(Integer opptOpptGamesPlayed) {
		this.opptOpptGamesPlayed = opptOpptGamesPlayed;
	}
	
	@Column(name="opptOpptWinPercentage", nullable=false)
	private Float opptOpptWinPercentage;
	public Float getOpptOpptWinPercentage() {
		return opptOpptWinPercentage;
	}
	public void setOpptOpptWinPercentage(Float opptOpptWinPercentage) {
		this.opptOpptWinPercentage = opptOpptWinPercentage;
	}
	
	public static Float findOpponentOppenentWinPercentageSeason(String date, String team_key, ProcessingType processingType) {	
		String sql =
				"select teamKey, sum(opptOpptWins) as sumWins, sum(opptOpptGamesPlayed) as sumGamesPlayed " +
				"from standing " + 
				"group by teamKey ";
			
		RawSql rawSql = 
				RawSqlBuilder
					.parse(sql)
					.columnMapping("teamKey", "teamKey")
					.columnMapping("sum(opptOpptWins)", "sumWins")
					.columnMapping("sum(opptOpptGamesPlayed)", "sumGamesPlayed")
					.create();
		
		Query<StandingAggregate> query = Ebean.find(StandingAggregate.class);
		query.setRawSql(rawSql)
			 .having().eq("teamKey", team_key);
				
		StandingAggregate sa = query.findUnique();
	
		//if overflow is likely, if it would overflow (ie the dividend is bigger than 922337203685477581)
		return new BigDecimal(sa.sumWins).divide(new BigDecimal(sa.sumGamesPlayed), 3, RoundingMode.HALF_UP).floatValue();
	}
	

	public String toString() {
		return new StringBuffer()
			.append("\n" + this.teamKey + "\n")
			.append("  game date: " + this.gameDate)
			.append("  rank: " + this.rank)
			.append("  ordinal rank: " + this.ordinalRank)
			.append("  games won: " + this.gamesWon)
			.append("  games played: " + this.gamesPlayed)
			.append("  oppt games won: " + this.opptOpptWins)
			.append("  oppt games played: " + this.opptOpptGamesPlayed)
			.append("  oppt win percentage: " + this.opptOpptWinPercentage)
			.toString();
	}
}