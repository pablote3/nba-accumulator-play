package models.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonProperty;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import util.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.EnumValue;

@Entity
public class Game extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name="table_gen", table="sequence_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="game_seq")
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Version
	private Timestamp lastUpdate;
	public Timestamp getLastUpdate()  {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@OneToMany(mappedBy="game", cascade=CascadeType.ALL)
	private List<BoxScore> boxScores = new ArrayList<BoxScore>();
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

	@OneToMany(mappedBy="game", cascade=CascadeType.ALL)
	private List<GameOfficial> gameOfficials = new ArrayList<GameOfficial>();
	public List<GameOfficial> getGameOfficials()  {
		return gameOfficials;
	}
	public void setGameOfficial(List<GameOfficial> gameOfficials)  {
		this.gameOfficials = gameOfficials;
	}
	public void addGameOfficial(GameOfficial gameOfficial)  {
		this.getGameOfficials().add(gameOfficial);
	}
	public void removeGameOfficial(GameOfficial gameOfficial)  {
		this.getGameOfficials().remove(gameOfficial);
	}

	@Required
	@Column(name="date", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTimeDisplay() {
		return DateTime.getDisplayTime(date);
	}
	
	@Required
	private Status status;
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
        @EnumValue("Finished") finished,
        @EnumValue("Completed") completed,
        @EnumValue("Postponed") postponed,
        @EnumValue("Suspended") suspended,
        @EnumValue("Cancelled") cancelled,
    }
	
	@Required
	@Enumerated(EnumType.STRING)
	@Column(name="seasonType", length=7, nullable=false)
	@JsonProperty("season_type")
	private SeasonType seasonType;
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
	  
	public static List<Game> findAll() {
	    return find.all();
	}
	
	public static List<Game> findByDate(String gameDate) {
	  	Query<Game> query = Ebean.find(Game.class);
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	    query.where().ilike("date", gameDate + "%");
	
	    List<Game> games = query.findList();
	    return games;
	}
	
	public static void create(Game game) {
	  	game.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}

	public static Page<Game> page(int page, int pageSize) {
        return 
            find.where()
                .findPagingList(pageSize)
                .getPage(page);
    }
	
	public static Page<Game> pageByDate(int page, int pageSize, String gameDate) {
	  	Query<Game> query = Ebean.find(Game.class);
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	    query.where().ilike("date", gameDate + "%");
	
	    return query.findPagingList(pageSize).getPage(page);
    }
	  
	public String toString() {
		return new StringBuffer()
			.append("\r" + "  id: " + this.id)
			.append("  date: " + this.date)
			.append("  status: " + this.status)
			.append("  seasonType: " + this.seasonType)
			.append("\n" + this.boxScores)
			.toString();
	}
}