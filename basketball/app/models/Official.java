package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import org.codehaus.jackson.annotate.JsonProperty;

import play.db.ebean.Model;

import com.avaje.ebean.annotation.EnumValue;

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
	
	@ManyToOne
	@JoinColumn(name="game_id", referencedColumnName="id", nullable=false)
	private Game game;
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="position", length=5, nullable=false)
	private Position position;
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public enum Position {
        @EnumValue("Lead") lead,
        @EnumValue("Trail") trail,
        @EnumValue("Center") center
    }
	
	@Column(name="lastName", length=35, nullable=false)
	@JsonProperty("last_name")
	private String lastName;
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Column(name="firstName", length=35, nullable=false)
	@JsonProperty("first_name")
	private String firstName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public static Finder<Long,Official> find = new Finder<Long, Official>(Long.class, Official.class);
	  
	public static List<Official> all() {
	    return find.all();
	}
	  
	public static void create(Official boxScore) {
	  	boxScore.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}

	public String toString() {
		return (new StringBuffer())
			.append("  id:" + this.id)
			.append("  position:" + this.position)
			.append("  lastName:" + this.lastName)
			.append("  firstName:" + this.firstName)
			.toString();
	}
}