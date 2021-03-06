package models;

import java.sql.Timestamp;
import java.util.ArrayList;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import services.EbeanServerService;
import services.InjectorModule;

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
	private static EbeanServerService service = injector.getInstance(EbeanServerService.class);	
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
	@Column(name="last_name", length=25, nullable=false)
	@JsonProperty("last_name")
	private String lastName;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Required
	@Column(name="first_name", length=25, nullable=false)
	@JsonProperty("first_name")
	private String firstName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Required
	@Column(name="display_name", length=50, nullable=false)
	@JsonProperty("display_name")
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Column(name="height", nullable=true)
	@JsonProperty("height_in")
	private Short height;
	public Short getHeight() {
		return height;
	}
	public void setHeight(Short height) {
		this.height = height;
	}
	
	@Column(name="weight", nullable=true)
	@JsonProperty("weight_lb")
	private Short weight;
	public Short getWeight() {
		return weight;
	}
	public void setWeight(Short weight) {
		this.weight = weight;
	}
	
	@Required
	@Column(name="birthdate", nullable=true)
	@Temporal(TemporalType.DATE)
	@JsonProperty("birthdate")
	private LocalDate birthDate;
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
	@Required
	@Column(name="birthplace", length=50, nullable=true)
	@JsonProperty("birthplace")
	private String birthPlace;
	public String getBirthPlace() {
		return birthPlace;
	}
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
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
	
	public static List<Player> findByName(String lastName, String firstName, ProcessingType processingType) {
		List<Player> players;
		Query<Player> query; 
	  	if (processingType.equals(ProcessingType.batch)) 
	  		query = ebeanServer.find(Player.class);
  		else if (processingType.equals(ProcessingType.online))
  			query = Ebean.find(Player.class);	

		query = ebeanServer.find(Player.class);
		query.where().eq("lastName", lastName);
		query.where().eq("firstName", firstName);
		players = query.findList();
	    return players;
	}
	
	public static Player findByNameBirthDate(String lastName, String firstName, String birthDate, ProcessingType processingType) {
		Player player;
		Query<Player> query; 
	  	if (processingType.equals(ProcessingType.batch)) 
	  		query = ebeanServer.find(Player.class);
  		else if (processingType.equals(ProcessingType.online))
  			query = Ebean.find(Player.class);	

		query = ebeanServer.find(Player.class);
		query.where().eq("lastName", lastName);
		query.where().eq("firstName", firstName);
		query.where().eq("birthdate", birthDate);
		player = query.findUnique();
	    return player;
	}
	
	public static void create(Player player, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.save(player);
		else if (processingType.equals(ProcessingType.online))
			Ebean.save(player);
	}
	
	public static void update(Player player, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.update(player);
		else if (processingType.equals(ProcessingType.online))
			Ebean.update(player);
	}
	  
	public static void delete(Player player, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.delete(player);
		else if (processingType.equals(ProcessingType.online))
			Ebean.delete(player);
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
			.append("  lastName:" + this.lastName)
			.append("  firstName:" + this.firstName)
			.append("  birthDate:" + this.birthDate)
			.toString();
	}
	
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(lastName).
            append(firstName).
            append(birthDate).
            toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Player))
            return false;

        Player player = (Player) obj;
        return new EqualsBuilder().
            append(lastName, player.lastName).
            append(firstName, player.firstName).
            append(birthDate, player.birthDate).
            isEquals();
    }
}