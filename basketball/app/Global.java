import play.Application;
import play.GlobalSettings;
import services.InjectorModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

    public class Global extends GlobalSettings {

      private static final Injector INJECTOR = createInjector();
      
      @Override
      public void onStart(Application app) {
        //Logger.info("Application has started");
      }  

      @Override
      public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
    	  return INJECTOR.getInstance(controllerClass);
      }

      private static Injector createInjector() {
    	  return Guice.createInjector(new InjectorModule());
      }	
}
