package services;

import static org.fest.assertions.Assertions.assertThat;
import services.InjectorModule;
import services.EbeanServerService;
import services.EbeanServerServiceImpl;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlRow;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class InvokeEbeanServerTest {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new InjectorModule());
		
		EbeanServerService service = injector.getInstance(EbeanServerServiceImpl.class);
		
		EbeanServer server = service.createEbeanServer();
		
		String sql = "select count(*) as count from team";
		SqlRow row = server.createSqlQuery(sql).findUnique();
        assertThat(row.getInteger("count")).isEqualTo(31);
	}

}
