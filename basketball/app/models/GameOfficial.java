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
public class GameOfficial extends Model {
	private static final long serialVersionUID = 1L;
	
	private static Injector injector = Guice.createInjector(new InjectorModule());
	private static EbeanServerService service = injector.getInstance(EbeanServerServiceImpl.class);	
	private static EbeanServer ebeanServer = service.createEbeanServer();

	@Id
	@TableGenerator(name="table_gen", table="seq_table", pkColumnName="seq_name", valueColumnName="seq_count", pkColumnValue="gameOfficial_seq", initialValue=1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="table_gen")
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	
	@ManyToOne
	@JoinColumn(name="official_id", referencedColumnName="id", nullable=false)
	private Official official;
	public Official getOfficial() {
		return official;
	}
	public void setOfficial(Official official) {
		this.official = official;
	}
	
	@Column(name="counter", nullable=false)
	private Short counter;
	public Short getCounter() {
		return counter;
	}
	public void setCounter(Short counter) {
		this.counter = counter;
	}
	
	public static void delete(GameOfficial gameOfficial, ProcessingType processingType) {
		if (processingType.equals(ProcessingType.batch)) {
			ebeanServer.delete(gameOfficial);
		}
		else {
			Ebean.delete(gameOfficial);
		}
	}
}