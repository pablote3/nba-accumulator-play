package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import models.Game.ProcessingType;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import services.EbeanServerService;
import services.InjectorModule;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Entity
public class Team extends Model {
	private static final long serialVersionUID = 1L;
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerService.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();
  
	@Id
	@TableGenerator(name="table_gen", table="sequence_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="team_seq", initialValue=1)
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
	
	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
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
	
	@OneToMany(mappedBy="team", fetch=FetchType.LAZY)
	private List<RosterPlayer> rosterPlayers = new ArrayList<RosterPlayer>();
	public List<RosterPlayer> getRosterPlayers()  {
		return rosterPlayers;
	}
	public void setRosterPlayers(List<RosterPlayer> rosterPlayers)  {
		this.rosterPlayers = rosterPlayers;
	}
	public void addRosterPlayer(RosterPlayer rosterPlayer)  {
		this.getRosterPlayers().add(rosterPlayer);
	}
	public void removeRosterPlayer(RosterPlayer rosterPlayer)  {
		this.getRosterPlayers().remove(rosterPlayer);
	}

	@Required
	@Column(name="team_key", length=35, nullable=false)
	@JsonProperty("team_id")
	private String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Required
	@Column(name="full_name", length=35, nullable=false)
	@JsonProperty("full_name")
	private String fullName;
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Required
	@Column(name="short_name", length=20, nullable=false)
	@JsonProperty("short_name")
	private String shortName;
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Required
	@Column(name="abbr", length=5, nullable=false)
	@JsonProperty("abbreviation")
	private String abbr;
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	
	@Required
	@Column(name="active", nullable=false)
	private boolean active;
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Required
	@Enumerated(EnumType.STRING)
	@Column(name="conference", length=4, nullable=false)
	private Conference conference;
	public Conference getConference() {
		return conference;
	}
	public void setConference(Conference conference) {
		this.conference = conference;
	}
	
    public enum Conference {
        @EnumValue("East") East,
        @EnumValue("West") West,
    }
	
    @Required
	@Enumerated(EnumType.STRING)
	@Column(name="division", length=9, nullable=false)
	private Division division;
	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}
	
	public enum Division {
        @EnumValue("Atlantic")  Atlantic,
        @EnumValue("Central")   Central,
        @EnumValue("Southeast") Southeast,
        @EnumValue("Southwest") Southwest,
        @EnumValue("Northwest") Northwest,
        @EnumValue("Pacific")   Pacific,
    }
	
	@Column(name="site_name", length=30, nullable=false)
	@JsonProperty("site_name")
	private String siteName;
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
    
	@Column(name="city", length=15, nullable=false)
	private String city;
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
    
	@Column(name="state", length=2, nullable=false)
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public static Team findById(Long id) {
		Team team = Ebean.find(Team.class, id);
		return team;
	}
	
	public static Team findByKey(String key, String value, ProcessingType processingType) {
		Query<Team> query = null;
		if (processingType.equals(ProcessingType.batch))
			query = ebeanServer.find(Team.class);
		else if (processingType.equals(ProcessingType.online))
			query = Ebean.find(Team.class);
		
		query.where().eq(key, value);		
		Team team = query.findUnique();
		return team;
	}
	  
	public static List<Team> findAll() {
		Query<Team> query = Ebean.find(Team.class);
		List<Team> teams = query.findList();
	    return teams;
	}	
	
	public static Team findByAbbr(String abbr, ProcessingType processingType) {
		Team team;
		Query<Team> query = null; 
	  	if (processingType.equals(ProcessingType.batch)) 
	  		query = ebeanServer.find(Team.class);
  		else if (processingType.equals(ProcessingType.online))
  			query = Ebean.find(Team.class);	

		query.where().eq("abbr", abbr);
		query.where().eq("active", true);
		team = query.findUnique();
		return team;
	}
	
	
	public static Team findByTeamKey(String teamKey, ProcessingType processingType) {
		Team team;
		Query<Team> query = null; 
	  	if (processingType.equals(ProcessingType.batch)) 
	  		query = ebeanServer.find(Team.class);
  		else if (processingType.equals(ProcessingType.online))
  			query = Ebean.find(Team.class);	

		query.where().eq("team_key", teamKey);
		team = query.findUnique();
		return team;
	}
	
	public static List<Team> findByActive(boolean active) {
		Query<Team> query = Ebean.find(Team.class);
		query.where().eq("active", active);
		List<Team> teams = query.findList();
	    return teams;
	}
	
	public static List<Team> findFilter(String filter) {
		Query<Team> query = Ebean.find(Team.class);
		query.where().ilike("fullName", "%" + filter + "%");
		List<Team> teams = query.findList();
		return teams;
	}
	  
	public static void create(Team team, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.save(team);
		else if (processingType.equals(ProcessingType.online))
			Ebean.save(team);
	}
	  
	public static void delete(Long id) {
		Team team = Team.findById(id);
	  	team.delete();
	}
	
    public static Page<Team> page(int page, int pageSize, String sortBy, String order, String filter) {
    	Query<Team> query = Ebean.find(Team.class);
    	query.where().ilike("fullName", "%" + filter + "%");
    	query.where().orderBy(sortBy + " " + order);
    	Page<Team> p = query.findPagingList(pageSize).getPage(page);
    	return p;
    }
	  
	public String toString() {
		return new StringBuffer()
			.append("\n" + "  id: " + this.id)
			.append("  key: " + this.key)
			.append("  fullName: " + this.fullName)
			.append("  abbr: " + this.abbr)
			.append("  conference: " + this.conference)
			.append("  division: " + this.division + "\n")
			.toString();
	}
}