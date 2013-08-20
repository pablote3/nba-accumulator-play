package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonProperty;

import play.db.ebean.Model;

import com.avaje.ebean.annotation.EnumValue;

@Entity
public class Game extends Model {
	private static final long serialVersionUID = 1L;
	private Date date;
	private Status status;
	private SeasonType seasonType;
	private List<BoxScore> boxScores = new ArrayList<BoxScore>();
	private List<Official> officials;
	
	@Id
	@TableGenerator(name="table_gen", table="sequence_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="game_seq")
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	
	@OneToMany(mappedBy="game", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<BoxScore> getBoxScores()  {
		return boxScores;
	}
	public void setBoxScores(List<BoxScore> boxScores)  {
		this.boxScores = boxScores;
	}
	public void addBoxScore(BoxScore boxScore)  {
		this.getBoxScores().add(boxScore);
	}
	public void removeBoxScore(BoxScore boxScore)  {
		this.getBoxScores().remove(boxScore);
	}
	
	@OneToMany
	@JoinColumn(name="official_seq")
	public List<Official> getOfficials()  {
		return officials;
	}

	@Column(name="date", nullable=false)
	@Temporal(TemporalType.DATE)
	@JsonProperty("start_date_time")
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="status", length=9, nullable=false)
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public enum Status {
        @EnumValue("Scheduled") scheduled,
        @EnumValue("Completed") completed,
        @EnumValue("Postponed") postponed,
        @EnumValue("Suspended") suspended,
        @EnumValue("Cancelled") cancelled,
    }
	
	@Enumerated(EnumType.STRING)
	@Column(name="seasonType", length=7, nullable=false)
	@JsonProperty("season_type")
	public SeasonType getSeasonType() {
		return seasonType;
	}
	public void setSeasonType(SeasonType seasonType) {
		this.seasonType = seasonType;
	}
	
	public enum SeasonType {
        @EnumValue("Pre") pre,
        @EnumValue("Regular") regular,
        @EnumValue("Post") post,
    }
	
	public static Finder<Long,Game> find = new Finder<Long, Game>(Long.class, Game.class);
	  
	public static List<Game> all() {
	    return find.all();
	}
	  
	public static void create(Game game) {
	  	game.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}
	  
	public String toString() {
		return (new StringBuffer())
			.append("  id: " + this.id)
			.append("  date: " + this.date)
			.append("  status: " + this.status)
			.append("  seasonType:" + this.seasonType)
			.toString();
	}
}