package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import models.Game.ProcessingType;

import play.db.ebean.Model;
import services.EbeanServerService;
import services.EbeanServerServiceImpl;
import services.InjectorModule;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Entity
public class PeriodScore extends Model {
	private static final long serialVersionUID = 1L;
	
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerServiceImpl.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();

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
	
	public static void delete(PeriodScore periodScore, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch)) 
			ebeanServer.delete(periodScore);
		else if (processingType.equals(ProcessingType.online))
			Ebean.delete(periodScore);
	}

	public String toString() {
		return new StringBuffer()
			.append("  id:" + this.id)
			.append("  quarter:" + this.quarter)
			.append("  score:" + this.score)
			.toString();
	}
}