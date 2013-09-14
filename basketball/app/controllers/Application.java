package controllers;

import static play.data.Form.form;
import models.Game;
import models.Team;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.game.*;
import views.html.team.*;

public class Application extends Controller {
	
    /**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
        routes.Application.listTeams(0, "fullName", "asc", "")
    );
    
    /**
     * Handle default path requests, redirect to computers list
     */
    public static Result index() {
        return GO_HOME;
    }

    /**
     * Display the paginated list of teams.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on team names
     */
    public static Result listTeams(int page, String sortBy, String order, String filter) {
        return ok(
            listTeams.render(
                Team.page(page, 15, sortBy, order, filter), sortBy, order, filter
            )
        );
    }
    
    /**
     * Display (GET) the 'edit form' of a existing team.
     *
     * @param id Id of the team to edit
     */
    public static Result editTeam(Long id) {
        Form<Team> form = form(Team.class).fill(
        	Team.find.byId(id)
        );
        return ok(
            editTeam.render(id, form)
        );
    }
    
    /**
     * Return the id of an existing team.
     *
     * @param key of the team to search for
     * @param value of the team to search for
     */
    public static Result searchTeam(String key, String value) {
        try {
        	Team team = Team.find.where().eq(key, value).findUnique();
			return ok(team.getId().toString());
		} catch (Exception e) {
			return badRequest();
		}
    }

    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the team to edit
     */
    public static Result updateTeam(Long id) {
        Form<Team> form = form(Team.class).fill(
        	Team.find.byId(id)).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(editTeam.render(id, form));
        }
        form.get().update(id);
        flash("success", "Team " + form.get().getFullName() + " has been updated");
        return GO_HOME;
    }
    
    /**
     * Display (GET) the 'create form' of a new team.
     */
    public static Result createTeam() {
        Form<Team> form = form(Team.class);
        return ok(
            createTeam.render(form)
        );
    }
    
    /**
     * Handle (POST) the 'create form' submission 
     */
    public static Result saveTeam() {
        Form<Team> form = form(Team.class).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(createTeam.render(form));
        }
        form.get().save();
//        System.out.println(form.get().getId());
        flash("success", "Team " + form.get().getFullName() + " has been saved");
        return GO_HOME;
    }
    
    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the team to delete
     */
    public static Result deleteTeam(Long id) {
        Team.find.ref(id).delete();
        flash("success", "Team has been deleted");
        return GO_HOME;
    }
    
     /**
     * Display the list of games.
     *
     * @param page Current page number (starts from 0)
     */
   public static Result listGames(int page) {
       return ok(
           listGames.render(
               Game.page(page, 15)
           )
       );
   }
}
