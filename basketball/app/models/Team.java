package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

import play.db.ebean.Model;

import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.EnumValue;
import org.codehaus.jackson.annotate.JsonProperty;

@Entity
public class Team extends Model {
	private static final long serialVersionUID = 1L;	
	private String key;
	private String fullName;
	private String abbr;
	private boolean active;
	private Conference conference;
	private Division division;
	private String siteName;
	private String city;
	private String state;
	private List<BoxScore> boxScores = new ArrayList<BoxScore>();
  
	@Id
	@TableGenerator(name="table_gen", table="sequence_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="team_seq")
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	
	@OneToMany(mappedBy="team", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
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

	@Column(name="team_key", length=35, nullable=false)
	@JsonProperty("team_id")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	@Column(name="full_name", length=35, nullable=false)
	@JsonProperty("full_name")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@Column(name="abbr", length=5, nullable=false)
	@JsonProperty("abbreviation")
	public String getAbbr() {
		return abbr;
	}
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
	
	@Column(name="active", nullable=false)
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="conference", length=4, nullable=false)
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
	
	@Enumerated(EnumType.STRING)
	@Column(name="division", length=9, nullable=false)
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
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
    
	@Column(name="city", length=15, nullable=false)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
    
	@Column(name="state", length=2, nullable=false)
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public static Finder<Long,Team> find = new Finder<Long, Team>(Long.class, Team.class);
	  
	public static List<Team> all() {
	    return find.all();
	}
	  
	public static void create(Team team) {
	  	team.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}
	
    /**
     * Return a page of team
     *
     * @param page Page to display
     * @param pageSize Number of teams per page
     * @param sortBy team property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<Team> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("fullName", "%" + filter + "%")
//                .orderBy(sortBy + " " + order)
//                .fetch("boxScore")
                .findPagingList(pageSize)
                .getPage(page);
    }
	  
	public String toString() {
		return (new StringBuffer())
			.append("  id: " + this.id)
			.append("  key: " + this.key)
			.append("  abbr: " + this.abbr)
			.append("  conference: " + this.conference)
			.append("  division: " + this.division)
			.append("  siteName: " + this.siteName)
			.append("  city: " + this.city)
			.append("  state: " + this.state)
			.toString();
	}
}