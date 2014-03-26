package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Entity
public class Player extends Model {
	private static final long serialVersionUID = 1L;
	
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerServiceImpl.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="player_seq", initialValue=1)
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
	
	@OneToMany(mappedBy="player", fetch=FetchType.LAZY)
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
	
	@Required
	@Column(name="number", length=2, nullable=false)
	private String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	@Required
	@Column(name="lastName", length=35, nullable=false)
	@JsonProperty("last_name")
	private String lastName;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Required
	@Column(name="firstName", length=35, nullable=false)
	@JsonProperty("first_name")
	private String firstName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Required
	@Column(name="firstGame", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date firstGame;
	public Date getFirstGame() {
		return firstGame;
	}
	public void setFirstGame(Date firstGame) {
		this.firstGame = firstGame;
	}
	public String getFirstGameDisplay() {
		return DateTime.getDisplayDateShort(firstGame);
	}
	
	@Required
	@Column(name="birthDate", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date birthDate;
	public Date getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	public String getBirthDateDisplay() {
		return DateTime.getDisplayDateShort(birthDate);
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
	
	public static Player findById(Long id) {
		Player player = Ebean.find(Player.class, id);
		return player;
	}
	
	public static Player findByKey(String key, String value) {
		Query<Player> query = Ebean.find(Player.class);
		query.where().eq(key, value);	
		Player player = query.findUnique();
		return player;
	}
	  
	public static List<Player> findAll() {
		Query<Player> query = Ebean.find(Player.class);
		List<Player> players = query.findList();
	    return players;
	}
	
	public static List<Player> findActive(boolean active) {
		Query<Player> query = Ebean.find(Player.class);
		query.where().eq("active", active);
		List<Player> players = query.findList();
	    return players;
	}
	
	public static Player findByName(String lastName, String firstName) {
		Query<Player> query = Ebean.find(Player.class);
		query.where().eq("lastName", lastName);
		query.where().eq("firstName", firstName);
		Player player = query.findUnique();
	    return player;
	}
	
	public static Player findByName(String lastName, String firstName, ProcessingType processingType) {
		Player player;
		Query<Player> query; 
		if (processingType.equals(ProcessingType.batch)) {
			query = ebeanServer.find(Player.class);
			query.where().eq("lastName", lastName);
			query.where().eq("firstName", firstName);
			player = query.findUnique();
		}
		else {
			player = findByName(lastName, firstName);
		}
	    return player;
	}
	
	public static void create(Player player) {
		player.save();
	}
	  
	public static void delete(Long id) {
		Player player = Player.findById(id);
	  	player.delete();
	}
	
    public static Page<Player> page(int page, int pageSize, String sortBy, String order, String filter) {
    	Query<Player> query = Ebean.find(Player.class);
    	query.where().ilike("lastName", "%" + filter + "%");
    	query.where().orderBy(sortBy + " " + order);
    	Page<Player> p = query.findPagingList(pageSize).getPage(page);
    	return p;
    }

	public String toString() {
		return new StringBuffer()
			.append("  id:" + this.id)
			.append("  number:" + this.number)
			.append("  lastName:" + this.lastName)
			.append("  firstName:" + this.firstName)
			.append("  active:" + this.active)
			.toString();
	}
}