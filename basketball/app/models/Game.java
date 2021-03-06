package models;

import java.sql.Timestamp;
import java.util.ArrayList;
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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import services.EbeanServerService;
import services.InjectorModule;
import util.DateTimeUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Entity
public class Game extends Model {
	private static final long serialVersionUID = 1L;
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerService.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="game_seq", initialValue=1)
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
	public void setGameOfficials(List<GameOfficial> gameOfficials)  {
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
	@JsonProperty("start_date_time")
	private DateTime date;
	public DateTime getDate() {
		return date;
	}
	public void setDate(DateTime date) {
		this.date = date;
	}
	public String getTimeDisplay() {
		return DateTimeUtil.getDisplayTime(date);
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
        @EnumValue("Cancelled") cancelled
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
        @EnumValue("Post") post
    }
	
	public enum ProcessingType {
        @EnumValue("Batch") batch,
        @EnumValue("Online") online,
        @EnumValue("Test") test
    }
	
	public enum Source {
        @EnumValue("File") file,
        @EnumValue("API") api
    }
	
	public static void create(Game game, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.save(game);
		else if (processingType.equals(ProcessingType.online))
			Ebean.save(game);
	}
	
	public static void update(Game game, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.update(game);
		else if (processingType.equals(ProcessingType.online))
			Ebean.update(game);
	}
	  
	public static void delete(Long id, ProcessingType processingType) {
		Game game = Game.findById(id, processingType);
	  	game.delete();
	}
	  
	public static Game findById(Long id, ProcessingType processingType) {
		Game game = null;
		if (processingType.equals(ProcessingType.batch))
			game = ebeanServer.find(Game.class, id);
		else if (processingType.equals(ProcessingType.online))
			game = Ebean.find(Game.class, id);
		return game;
	}
	
	public static List<Game> findAll() {
		Query<Game> query = Ebean.find(Game.class);
		List<Game> games = query.findList();
	    return games;
	}
	
	public static List<Game> findByDate(String date) {
	  	Query<Game> query = Ebean.find(Game.class);
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().between("date", date + " 00:00:00", date + " 23:59:59");
	
	    List<Game> games = query.findList();
	    return games;
	}
	
	public static List<Long> findIdsByDateRangeSize(String propDate, String propSize, ProcessingType processingType) {
	  	Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);
	  	
	  	int maxRows = Integer.parseInt(propSize);
	  	if (maxRows > 0)
		  	query.setMaxRows(maxRows);

	  	LocalDate maxDate = DateTimeUtil.getDateMaxSeason(DateTimeUtil.createDateFromStringDate(propDate));

	  	query.where().between("date", propDate, maxDate);
	  	query.orderBy("t0.date asc");
	    List<Game> games = query.findList();
	    
	    List<Long> gameIds = null;
	    if (games.size() > 0) {
	    	gameIds = new ArrayList<Long>();
		    for (int i = 0; i < games.size(); i++) {
				gameIds.add(games.get(i).getId());
			}
	    }
		return gameIds;
	}
	
	public static List<Long> findIdsByDate(String propDate, ProcessingType processingType) {
	  	Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);

	  	query.where().between("t0.date", propDate + " 00:00:00", propDate + " 23:59:59");
	    List<Game> games = query.findList();
	    
	    List<Long> gameIds = null;
	    if (games.size() > 0) {
	    	gameIds = new ArrayList<Long>();
		    for (int i = 0; i < games.size(); i++) {
				gameIds.add(games.get(i).getId());
			}
	    }
		return gameIds;
	}
	
	public static List<Long> findIdsByDateScheduled(String propDate, ProcessingType processingType) {
	  	Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);

	  	query.where().between("t0.date", propDate + " 00:00:00", propDate + " 23:59:59");
	  	query.where().eq("t0.status", "Scheduled");
	    List<Game> games = query.findList();
	    
	    List<Long> gameIds = null;
	    if (games.size() > 0) {
	    	gameIds = new ArrayList<Long>();
		    for (int i = 0; i < games.size(); i++) {
				gameIds.add(games.get(i).getId());
			}
	    }
		return gameIds;
	}
	
	public static Game findByDateTeam(String date, String teamKey, ProcessingType processingType) {
		Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);
	  	
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().between("t0.date", date + " 00:00:00", date + " 23:59:59");
	    query.where().eq("t2.team_key", teamKey);
	
	    Game sparseGame = query.findUnique();
	    Game game = null;
	    if (sparseGame != null) {
	    	game = Game.findById(sparseGame.getId(), processingType);
	    }
	    return game;
	}
	
	public static Game findPreviousGameByDateTeam(String date, String teamKey) {
	  	Query<Game> query = Ebean.find(Game.class);
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().lt("t0.date", date + " 00:00:00");
	    query.where().eq("t2.team_key", teamKey);
	    query.orderBy("t0.date desc");
	
	    List<Game> games = query.findList();
	    return games.get(0);
	}
	
	public static DateTime findPreviousGameDateByDateTeam(String date, String teamKey, ProcessingType processingType) {
	  	Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);
	  	
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().lt("t0.date", date + " 00:00:00");
	    query.where().eq("t2.team_key", teamKey);
	    query.orderBy("t0.date desc");
	
	    List<Game> games = query.findList();
	    if (games.size() > 0)
	    	return games.get(0).getDate();
	    else
	    	return null;
	}
	
	public static List<Game> findCompletedByDateTeamSeason(String date, String teamKey, ProcessingType processingType) {
		Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);

	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().lt("t0.date", date + " 00:00:00");
	  	query.where().gt("t0.date", DateTimeUtil.getDateMinSeason(DateTimeUtil.createDateFromStringDate(date)) + " 00:00:00");
	  	query.where().eq("t0.status", "Completed");
	  	query.where().between("t0.date", date + " 00:00:00", date + " 23:59:59");
	    query.where().eq("t2.team_key", teamKey);
	    query.orderBy("t0.date asc");
	    List<Game> sparseGames = query.findList();
	    
	    List<Game> games = new ArrayList<Game>();
	    for (int i = 0; i < sparseGames.size(); i++) {
			games.add(Game.findById(sparseGames.get(i).getId(), processingType));
		}
	    return games;
	}
	
	public static List<Game> findByDateTeamSeason(String date, String teamKey, ProcessingType processingType) {
		Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);

	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().lt("t0.date", date + " 23:59:59");
	  	query.where().gt("t0.date", DateTimeUtil.getDateMinSeason(DateTimeUtil.createDateFromStringDate(date)) + " 00:00:00");
	  	query.where().in("t0.status", new Object[]{"Completed", "Scheduled"});
	    query.where().eq("t2.team_key", teamKey);
	    query.orderBy("t0.date asc");
	    List<Game> sparseGames = query.findList();
	    
	    List<Game> games = new ArrayList<Game>();
	    for (int i = 0; i < sparseGames.size(); i++) {
			games.add(Game.findById(sparseGames.get(i).getId(), processingType));
		}
	    return games;
	}
	
	public static List<Long> findIdByDateTeam(String propDate, String propTeam, ProcessingType processingType) {
	  	Query<Game> query = null;
	  	final int maxRows = 1;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);

	  	query.setMaxRows(maxRows);
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	  	query.where().between("t0.date", propDate + " 00:00:00", propDate + " 23:59:59");
	    query.where().eq("t2.team_key", propTeam);
	    query.orderBy("t0.date asc");
	    Game game = query.findUnique();
	    
	    List<Long> gameIds = null;
	    gameIds = new ArrayList<Long>();
		gameIds.add(game.getId());
		return gameIds;
	}
	
	public static int findCountGamesByDateScheduled(String gameDate, ProcessingType processingType) {
	  	Query<Game> query = null;
	  	if (processingType.equals(ProcessingType.batch))
	  		query = ebeanServer.find(Game.class);
	  	else if (processingType.equals(ProcessingType.online))
	  		query = Ebean.find(Game.class);
	  	
	  	query.where().between("t0.date", gameDate + " 00:00:00", gameDate + " 23:59:59");
	    query.where().eq("t0.status", "Scheduled");
	    return query.findList().size();
	}

	public static Page<Game> page(int page, int pageSize) {
    	Query<Game> query = Ebean.find(Game.class);
    	Page<Game> p = query.findPagingList(pageSize).getPage(page);
    	return p;
    }
	
	public static Page<Game> pageByDate(int page, int pageSize, String date) {
	  	Query<Game> query = Ebean.find(Game.class);
	  	query.fetch("boxScores");
	  	query.fetch("boxScores.team");
	    query.where().between("date", date + " 00:00:00", date + " 23:59:59");
	
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