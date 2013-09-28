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

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

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
	public void setGameOfficial(List<GameOfficial> gameOfficials)  {
		this.gameOfficials = gameOfficials;
	}
	public void addGameOfficial(GameOfficial gameOfficial)  {
		this.getGameOfficials().add(gameOfficial);
	}
	public void removeGameOfficial(GameOfficial gameOfficial)  {
		this.getGameOfficials().remove(gameOfficial);
	}
	
	@Column(name="number", length=2, nullable=false)
	private String number;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	@Column(name="lastName", length=35, nullable=false)
	private String lastName;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name="firstName", length=35, nullable=false)
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
	
	public static void create(Official official) {
		official.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}

	public String toString() {
		return (new StringBuffer())
			.append("  id:" + this.id)
			.append("  number:" + this.number)
			.append("  lastName:" + this.lastName)
			.append("  firstName:" + this.firstName)
			.append("  active:" + this.active)
			.toString();
	}
}