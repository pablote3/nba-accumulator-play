package controllers;

import models.Game;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.game.listGames;

public class Schedule extends Controller {

   /**
    * Display the list of games.
    *
    * @param page Current page number (starts from 0)
    */
   public static Result list(int page) {
       return ok(
           listGames.render(
               Game.page(page, 15)
           )
       );
   }
}

