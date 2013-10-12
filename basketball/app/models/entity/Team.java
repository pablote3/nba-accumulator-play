package models.entity;

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

import org.codehaus.jackson.annotate.JsonProperty;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.EnumValue;

@Entity
public class Team extends Model {
	private static final long serialVersionUID = 1L;
  
	@Id
	@TableGenerator(name="table_gen", table="sequence_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="team_seq")
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
	
	public static Finder<Long,Team> find = new Finder<Long, Team>(Long.class, Team.class);
	  
	public static List<Team> findAll() {
	    return find.all();
	}
	
	public static List<Team> findActive(boolean active) {
		return find.where().eq("active", active).findList();
	}
	
	public static List<Team> findFilter(String filter) {
		return find.where().ilike("fullName", "%" + filter + "%").findList();
	}
	  
	public static void create(Team team) {
	  	team.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}
	
    public static Page<Team> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("fullName", "%" + filter + "%")
                .orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .getPage(page);
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