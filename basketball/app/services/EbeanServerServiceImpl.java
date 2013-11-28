package services;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;

public class EbeanServerServiceImpl implements EbeanServerService {
	EbeanServer server;
	
	public EbeanServerServiceImpl() {
	}

	public EbeanServer createEbeanServer() {
		ServerConfig config = new ServerConfig();
		config.setName("basketball"); 
		
		DataSourceConfig mySql = new DataSourceConfig();  
		mySql.setDriver("com.mysql.jdbc.Driver");
		mySql.setUsername("root");  
		mySql.setPassword("root");  
		mySql.setUrl("jdbc:mysql://localhost:3306/basketball");  
		mySql.setHeartbeatSql("select count(*) from team");  		  
		config.setDataSourceConfig(mySql);
		config.setDefaultServer(false);  
		config.setRegister(false);
		
		EbeanServer server = EbeanServerFactory.create(config);
		return server;
	}
}
