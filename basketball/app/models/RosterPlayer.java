package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.Game.ProcessingType;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import services.EbeanServerService;
import services.EbeanServerServiceImpl;
import services.InjectorModule;
import util.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Entity
public class RosterPlayer extends Model {
	private static final long serialVersionUID = 1L;
	
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerServiceImpl.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="rosterPlayer_seq", initialValue=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(mappedBy="rosterPlayer", fetch=FetchType.LAZY)
	private List<BoxScorePlayer> boxScorePlayers = new ArrayList<BoxScorePlayer>();
	public List<BoxScorePlayer> getBoxScorePlayers()  {
		return boxScorePlayers;
	}
	public void setBoxScorePlayers(List<BoxScorePlayer> boxScorePlayers)  {
		this.boxScorePlayers = boxScorePlayers;
	}
	public void addBoxScorePlayer(BoxScorePlayer boxScorePlayer)  {
		this.getBoxScorePlayers().add(boxScorePlayer);
	}
	public void removeBoxScorePlayer(BoxScorePlayer boxScorePlayer)  {
		this.getBoxScorePlayers().remove(boxScorePlayer);
	}
	
	@ManyToOne
	@JoinColumn(name="team_id", referencedColumnName="id", nullable=false)
	private Team team;
	public Team getTeam() {
		return team;
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	
	@ManyToOne
	@JoinColumn(name="player_id", referencedColumnName="id", nullable=false)
	private Player player;
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	@Required
	@Column(name="fromDate", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public String getFromDateDisplay() {
		return DateTime.getDisplayDateShort(fromDate);
	}
	
	@Required
	@Column(name="toDate", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date toDate;
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getToDateDisplay() {
		return DateTime.getDisplayDateShort(toDate);
	}

	@Required
	@Enumerated(EnumType.STRING)
	@Column(name="position", length=5, nullable=false)
	@JsonProperty("position")
	private Position position;
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public enum Position {
        @EnumValue("PG") pointGuard,
        @EnumValue("SG") shootingGuard,
        @EnumValue("SF") smallForward,
        @EnumValue("PF") powerForward,
        @EnumValue("C") center
    }
	
	@Required
	@Column(name="number", length=2, nullable=false)
	@JsonProperty("uniform_number")
	private String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public static RosterPlayer findById(Long id) {
		RosterPlayer rosterPlayer = Ebean.find(RosterPlayer.class, id);
		return rosterPlayer;
	}
	
	public static RosterPlayer findByKey(String key, String value) {
		Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
		query.where().eq(key, value);	
		RosterPlayer rosterPlayer = query.findUnique();
		return rosterPlayer;
	}
	  
	public static List<RosterPlayer> findAll() {
		Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
		List<RosterPlayer> rosterPlayers = query.findList();
	    return rosterPlayers;
	}
	
	public static List<RosterPlayer> findByDate(String date) {
		Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
	    query.where().lt("fromDate", date + " 00:00:00");
	    query.where().gt("toDate", date + " 23:59:59");	    
	    List<RosterPlayer> rosterPlayer = query.findList();
	    return rosterPlayer;
	}
	
	public static List<RosterPlayer> findByPlayer(String lastName, String firstName) {
		Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
		query.fetch("player");
		query.where().eq("t1.last_Name", lastName);
		query.where().eq("t1.first_Name", firstName);
		List<RosterPlayer> rosterPlayer = query.findList();
	    return rosterPlayer;
	}
	
	public static List<RosterPlayer> findByDatePlayer(String date, String lastName, String firstName, ProcessingType processingType) {
		List<RosterPlayer> rosterPlayer;
	  	Query<RosterPlayer> query;
	  	if (processingType.equals(ProcessingType.batch)) 
	  		query = ebeanServer.find(RosterPlayer.class);
  		else
  			query = Ebean.find(RosterPlayer.class);	
	  	query.fetch("player");
	  	query.where().lt("fromDate", date + " 00:00:00");
	  	query.where().gt("toDate", date + " 23:59:59");	 
	  	query.where().eq("t1.last_Name", lastName);
	  	query.where().eq("t1.first_Name", firstName);
	  	rosterPlayer = query.findList();
	    
		return rosterPlayer;
	}
	
	public static RosterPlayer findByDateTeamPlayer(String date, String teamAbbr, String lastName, String firstName, ProcessingType processingType) {
		RosterPlayer rosterPlayer;
	  	Query<RosterPlayer> query;
	  	if (processingType.equals(ProcessingType.batch)) 
	  		query = ebeanServer.find(RosterPlayer.class);
  		else
  			query = Ebean.find(RosterPlayer.class);	
	  	query.fetch("player");
	  	query.fetch("team");
	  	query.where().lt("fromDate", date + " 00:00:00");
	  	query.where().gt("toDate", date + " 23:59:59");	 
	  	query.where().eq("t1.last_Name", lastName);
	  	query.where().eq("t1.first_Name", firstName);
	  	query.where().eq("t2.abbr", teamAbbr);
	  	rosterPlayer = query.findUnique();
	    
		return rosterPlayer;
	}
	
	public static List<RosterPlayer> findByTeam(String teamKey) {
	  	Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
	  	query.fetch("team");
	    query.where().eq("t1.team_key", teamKey);
	    List <RosterPlayer> rosterPlayer = query.findList();
	    return rosterPlayer;
	}
	
	public static List<RosterPlayer> findByDateTeam(String date, String teamKey) {
	  	Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
	  	query.fetch("team");
	    query.where().lt("fromDate", date + " 00:00:00");
	    query.where().gt("toDate", date + " 23:59:59");	 
	    query.where().eq("t1.team_key", teamKey);
	    List<RosterPlayer> rosterPlayer = query.findList();
	    return rosterPlayer;
	}
	
	public static void create(RosterPlayer rosterPlayer) {
		rosterPlayer.save();
	}
	
	public static void update(RosterPlayer rosterPlayer, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.update(rosterPlayer);
		else
			Ebean.update(rosterPlayer);
	}
	  
	public static void delete(Long id) {
		RosterPlayer rosterPlayer = RosterPlayer.findById(id);
	  	rosterPlayer.delete();
	}
	
    public static Page<RosterPlayer> page(int page, int pageSize, String sortBy, String order, String filter) {
    	Query<RosterPlayer> query = Ebean.find(RosterPlayer.class);
    	query.where().ilike("lastName", "%" + filter + "%");
    	query.where().orderBy(sortBy + " " + order);
    	Page<RosterPlayer> p = query.findPagingList(pageSize).getPage(page);
    	return p;
    }

	public String toString() {
		return new StringBuffer()
			.append("  id:" + this.id)
			.append("  number:" + this.number)
			.append("  lastName:" + this.player.getLastName())
			.append("  firstName:" + this.player.getFirstName())
			.toString();
	}
}