package services;

import com.google.inject.AbstractModule;

public class InjectorModule extends AbstractModule {
	@Override 
	protected void configure() {
		bind(EbeanServerService.class).to(EbeanServerServiceImpl.class);
	}
}
