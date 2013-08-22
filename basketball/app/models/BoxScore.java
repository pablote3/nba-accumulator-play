package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

import org.codehaus.jackson.annotate.JsonProperty;

import play.db.ebean.Model;

import com.avaje.ebean.annotation.EnumValue;

@Entity
public class BoxScore extends Model {
	private static final long serialVersionUID = 1L;
	private Team team;
	private Game game;
	private List<PeriodScore> periodScores;
	private Location location;
	private Result result;
	private Short points;
	private Short assists;
	private Short turnovers;
	private Short steals;
	private Short blocks;
	private Short fieldGoalAttempts;
	private Short fieldGoalMade;
	private Float fieldGoalPercent;
	private Short threePointAttempts;
	private Short threePointMade;
	private Float threePointPercent;
	private Short freeThrowAttempts;
	private Short freeThrowMade;
	private Float freeThrowPercent;
	private Short reboundsOffense;
	private Short reboundsDefense;
	private Short personalFouls;
	
	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="boxscore_seq", initialValue=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	
	@ManyToOne
	@JoinColumn(name="team_id", referencedColumnName="id", nullable=false)
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	
	@ManyToOne
	@JoinColumn(name="game_id", referencedColumnName="id", nullable=false)
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	
	@OneToMany
	@JoinColumn(name="periodscore_seq", nullable=true)
	public List<PeriodScore> getPeriodScores()  {
		return periodScores;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="location", length=5, nullable=false)
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public enum Location {
        @EnumValue("Home") home,
        @EnumValue("Away") away,
    }
	
	@Enumerated(EnumType.STRING)
	@Column(name="result", length=4, nullable=true)
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public enum Result {
        @EnumValue("Win") win,
        @EnumValue("Loss") loss,
    }
	
	@Column(name="points", nullable=true)
	public Short getPoints() {
		return points;
	}
	public void setPoints(Short points) {
		this.points = points;
	}
	
	@Column(name="assists", nullable=true)
	public Short getAssists() {
		return assists;
	}
	public void setAssists(Short assists) {
		this.assists = assists;
	}
	
	@Column(name="turnovers", nullable=true)
	public Short getTurnovers() {
		return turnovers;
	}
	public void setTurnovers(Short turnovers) {
		this.turnovers = turnovers;
	}
	
	@Column(name="steals", nullable=true)
	public Short getSteals() {
		return steals;
	}
	public void setSteals(Short steals) {
		this.steals = steals;
	}
	
	@Column(name="blocks", nullable=true)
	public Short getBlocks() {
		return blocks;
	}
	public void setBlocks(Short blocks) {
		this.blocks = blocks;
	}
	
	@Column(name="fieldGoalAttempts", nullable=true)
	@JsonProperty("field_goals_attempted")
	public Short getFieldGoalAttempts() {
		return fieldGoalAttempts;
	}
	public void setFieldGoalAttempts(Short fieldGoalAttempts) {
		this.fieldGoalAttempts = fieldGoalAttempts;
	}	
	@Column(name="fieldGoalMade", nullable=true)
	@JsonProperty("field_goals_made")
	public Short getFieldGoalMade() {
		return fieldGoalMade;
	}
	public void setFieldGoalMade(Short fieldGoalMade) {
		this.fieldGoalMade = fieldGoalMade;
	}	
	@Column(name="fieldGoalPercent", nullable=true)
	@JsonProperty("field_goal_percentage")
	public Float getFieldGoalPercent() {
		return fieldGoalPercent;
	}
	public void setFieldGoalPercent(Float fieldGoalPercent) {
		this.fieldGoalPercent = fieldGoalPercent;
	}
	
	@Column(name="threePointAttempts", nullable=true)
	@JsonProperty("three_point_field_goals_attempted")
	public Short getThreePointAttempts() {
		return threePointAttempts;
	}
	public void setThreePointAttempts(Short threePointAttempts) {
		this.threePointAttempts = threePointAttempts;
	}	
	@Column(name="threePointMade", nullable=true)
	@JsonProperty("three_point_field_goals_made")
	public Short getThreePointMade() {
		return threePointMade;
	}
	public void setThreePointMade(Short threePointMade) {
		this.threePointMade = threePointMade;
	}	
	@Column(name="threePointPercent", nullable=true)
	@JsonProperty("three_point_percentage")
	public Float getThreePointPercent() {
		return threePointPercent;
	}
	public void setThreePointPercent(Float threePointPercent) {
		this.threePointPercent = threePointPercent;
	}
	
	@Column(name="freeThrowAttempts", nullable=true)
	@JsonProperty("free_throws_attempted")
	public Short getFreeThrowAttempts() {
		return freeThrowAttempts;
	}
	public void setFreeThrowAttempts(Short freeThrowAttempts) {
		this.freeThrowAttempts = freeThrowAttempts;
	}	
	@Column(name="freeThrowMade", nullable=true)
	@JsonProperty("free_throws_made")
	public Short getFreeThrowMade() {
		return freeThrowMade;
	}
	public void setFreeThrowMade(Short freeThrowMade) {
		this.freeThrowMade = freeThrowMade;
	}	
	@Column(name="freeThrowPercent", nullable=true)
	@JsonProperty("free_throw_percentage")
	public Float getFreeThrowPercent() {
		return freeThrowPercent;
	}
	public void setFreeThrowPercent(Float freeThrowPercent) {
		this.freeThrowPercent = freeThrowPercent;
	}
	
	@Column(name="reboundsOffense", nullable=true)
	@JsonProperty("offensive_rebounds")
	public Short getReboundsOffense() {
		return reboundsOffense;
	}
	public void setReboundsOffense(Short reboundsOffense) {
		this.reboundsOffense = reboundsOffense;
	}
	
	@Column(name="reboundsDefense", nullable=true)
	@JsonProperty("defensive_rebounds")
	public Short getReboundsDefense() {
		return reboundsDefense;
	}
	public void setReboundsDefense(Short reboundsDefense) {
		this.reboundsDefense = reboundsDefense;
	}
	
	@Column(name="personalFouls", nullable=true)
	@JsonProperty("personal_fouls")
	public Short getPersonalFouls() {
		return personalFouls;
	}
	public void setPersonalFouls(Short personalFouls) {
		this.personalFouls = personalFouls;
	}
	
	public static Finder<Long,BoxScore> find = new Finder<Long, BoxScore>(Long.class, BoxScore.class);
	  
	public static List<BoxScore> all() {
	    return find.all();
	}
	  
	public static void create(BoxScore boxScore) {
	  	boxScore.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}
	
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(BoxScore bs: BoxScore.find.orderBy("location").findList()) {
            options.put(bs.id.toString(), bs.id.toString());
        }
        return options;
    }

	public String toString() {
		return (new StringBuffer())
			.append("  id:" + this.id)
			.append("  location:" + this.location)
			.append("  result:" + this.result)
			.append("  points:" + this.points)
			.append("  assists:" + this.assists)
			.append("  turnovers:" + this.turnovers)
			.append("  steals:" + this.steals)
			.append("  blocks:" + this.blocks)
			.toString();
	}
}