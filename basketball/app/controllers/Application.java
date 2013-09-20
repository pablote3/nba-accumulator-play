package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
	
    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
    	routes.Schedule.list(0)
    );
    
    /**
     * Handle default path requests, redirect to computers list
     */
    public static Result index() {
        return GO_HOME;
    }
}
