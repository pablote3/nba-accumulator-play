package controllers;

import models.Game;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.game.listGames;

public class Games extends Controller {

   /**
    * Display the list of games.
    *
    * @param page Current page number (starts from 0)
    */
   public static Result list(int page) {
	   String gameDate = "2012-10-31";

       return ok(
     		  listGames.render(Game.pageByDate(page, 15, gameDate))
       );
   }
}
