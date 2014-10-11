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

import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import services.EbeanServerService;
import services.EbeanServerServiceImpl;
import services.InjectorModule;
import util.DateTimeUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Entity
public class Official extends Model {
	private static final long serialVersionUID = 1L;
	
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerServiceImpl.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="official_seq", initialValue=1)
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
	
	@OneToMany(mappedBy="official", fetch=FetchType.LAZY)
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
	@Column(name="number", length=2, nullable=false)
	private String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	@Required
	@Column(name="lastName", length=25, nullable=false)
	@JsonProperty("last_name")
	private String lastName;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Required
	@Column(name="firstName", length=25, nullable=false)
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
	private LocalDate firstGame;
	public LocalDate getFirstGame() {
		return firstGame;
	}
	public void setFirstGame(LocalDate firstGame) {
		this.firstGame = firstGame;
	}
	public String getFirstGameDisplay() {
		return DateTimeUtil.getDisplayDateShort(firstGame);
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
	
	public static Official findById(Long id) {
		Official official = Ebean.find(Official.class, id);
		return official;
	}
	
	public static Official findByKey(String key, String value) {
		Query<Official> query = Ebean.find(Official.class);
		query.where().eq(key, value);	
		Official official = query.findUnique();
		return official;
	}
	  
	public static List<Official> findAll() {
		Query<Official> query = Ebean.find(Official.class);
		List<Official> officials = query.findList();
	    return officials;
	}
	
	public static List<Official> findActive(boolean active) {
		Query<Official> query = Ebean.find(Official.class);
		query.where().eq("active", active);
		List<Official> officials = query.findList();
	    return officials;
	}
	
	public static Official findByNumber(String number, ProcessingType processingType) {
		Official official;
		Query<Official> query = null; 
		if (processingType.equals(ProcessingType.batch))
			query = ebeanServer.find(Official.class);
		else if (processingType.equals(ProcessingType.online))
			query = Ebean.find(Official.class);
		query.where().eq("number", number);
		official = query.findUnique();
	    return official;
	}
	
	public static Official findByName(String lastName, String firstName, ProcessingType processingType) {
		Official official;
		Query<Official> query = null; 
		if (processingType.equals(ProcessingType.batch))
			query = ebeanServer.find(Official.class);
		else if (processingType.equals(ProcessingType.online))
			query = Ebean.find(Official.class);
		query.where().eq("lastName", lastName);
		query.where().eq("firstName", firstName);
		official = query.findUnique();
	    return official;
	}
	
	public static void create(Official official, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch))
			ebeanServer.save(official);
		else if (processingType.equals(ProcessingType.online))
			Ebean.save(official);
	}
	  
	public static void delete(Long id) {
		Official official = Official.findById(id);
	  	official.delete();
	}
	
    public static Page<Official> page(int page, int pageSize, String sortBy, String order, String filter) {
    	Query<Official> query = Ebean.find(Official.class);
    	query.where().ilike("lastName", "%" + filter + "%");
    	query.where().orderBy(sortBy + " " + order);
    	Page<Official> p = query.findPagingList(pageSize).getPage(page);
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