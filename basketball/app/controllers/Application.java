package controllers;

import static play.data.Form.form;
import models.Team;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.editTeam;
import views.html.listTeams;

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
}
