package models.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import play.db.ebean.Model;

@Entity
public class PeriodScore extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="periodscore_seq", initialValue=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	
	@ManyToOne
	@JoinColumn(name="boxscore_id", referencedColumnName="id", nullable=false)
	private BoxScore boxScore;
	public BoxScore getBoxScore() {
		return boxScore;
	}
	public void setBoxScore(BoxScore boxScore) {
		this.boxScore = boxScore;
	}
	
	@Column(name="quarter", nullable=false)
	private Short quarter;
	public Short getQuarter() {
		return quarter;
	}
	public void setQuarter(Short quarter) {
		this.quarter = quarter;
	}
	
	@Column(name="score", nullable=false)
	private Short score;
	public Short getScore() {
		return score;
	}
	public void setScore(Short score) {
		this.score = score;
	}
	
	public static Finder<Long,PeriodScore> find = new Finder<Long, PeriodScore>(Long.class, PeriodScore.class);
	  
	public static List<PeriodScore> all() {
	    return find.all();
	}
	  
	public static void create(PeriodScore boxScore) {
	  	boxScore.save();
	}
	  
	public static void delete(Long id) {
	  	find.ref(id).delete();
	}

	public String toString() {
		return (new StringBuffer())
			.append("  id:" + this.id)
			.append("  quarter:" + this.quarter)
			.append("  score:" + this.score)
			.toString();
	}
}