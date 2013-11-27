package ebean;

import models.entity.BoxScore;
import models.entity.Game;
import models.entity.Team;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;

import controllers.Games;

public class EbeanServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerConfig config = new ServerConfig();
		config.setName("test"); 
		
		DataSourceConfig mySql = new DataSourceConfig();  
		mySql.setDriver("com.mysql.jdbc.Driver");
		mySql.setUsername("root");  
		mySql.setPassword("root");  
		mySql.setUrl("jdbc:mysql://localhost:3306/basketball");  
//		mySql.setHeartbeatSql("select count(*) from team");  		  
		config.setDataSourceConfig(mySql);
		config.setDefaultServer(false);  
		config.setRegister(false);
		
//		config.addClass(Game.class);
//		config.addClass(BoxScore.class);
//		config.addClass(Team.class);
		
		EbeanServer server = EbeanServerFactory.create(config);
		
		String sql = "select count(*) as count from team";
		SqlRow row = server.createSqlQuery(sql).findUnique();
		Integer i = row.getInteger("count");
		System.out.println("Got " + i + " count");
		
		//find(Games.findKeyByDateTeam("2012-10-31", "sacramento-kings"));

	}

}
