package models.entity;

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

import org.codehaus.jackson.annotate.JsonProperty;

import com.avaje.ebean.Page;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import util.DateTime;

@Entity
public class Official extends Model {
	private static final long serialVersionUID = 1L;

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
	@Column(name="active", nullable=false)
	private boolean active;
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public static Finder<Long,Official> find = new Finder<Long, Official>(Long.class, Official.class);
	  
	public static List<Official> findAll() {
	    return find.all();
	}
	
	public static List<Official> findActive(boolean active) {
		return find.where().eq("active", active).findList();
	}
	
	public static Official findByName(String lastName, String firstName) {
		return find.where().eq("lastName", lastName).eq("firstName", firstName).findUnique();
	}
	
	public static void create(Official official) {
		official.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}
	
    public static Page<Official> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("lastName", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
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