package controllers;

import models.entity.Game;
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
//	   java.text.SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH);
//	   String gameDate = simpleDateFormat.format(new java.util.Date()).toString();
	   String gameDate = "2012-10-31";

       return ok(
     		  listGames.render(Game.pageByDate(page, 15, gameDate))
       );
   }
}
