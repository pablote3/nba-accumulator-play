package controllers;

import static play.data.Form.form;
import models.Team;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.team.editTeam;
import views.html.team.listTeams;

public class Teams extends Controller {

    /**
     * Display the paginated list of teams.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on team names
     */
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(
            listTeams.render(
                Team.page(page, 15, sortBy, order, filter), sortBy, order, filter
            )
        );
    }
    
    /**
     * Return the id of an existing team.
     *
     * @param key of the team to search for
     * @param value of the team to search for
     */
    public static Result search(String key, String value) {
        try {
        	Team team = Team.findByKey(key, value);
			return ok(team.getId().toString());
		} catch (Exception e) {
			return badRequest();
		}
    }

    /**
     * Display (GET) the 'edit form' of an existing team or a new team.
     *
     * @param id Id of the team to edit
     */
    public static Result edit(Long id) {
    	Form<Team> form = null;
    	if (id == -1L) {
    		form = form(Team.class);
    	}
    	else {
    		Team team = Team.findById(id);
            form = form(Team.class).fill(team);
    	}
        return ok(
        	editTeam.render(id, form)
	    );
    } 
    
    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the team to edit
     */
    public static Result save(Long id) {
    	Form<Team> form = form(Team.class).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(editTeam.render(id, form));
        }
    	if (id == -1L) {
    		form.get().save();
            flash("success", "Team " + form.get().getFullName() + " has been created");
    	}
    	else {
            form.get().update(id);
            flash("success", "Team " + form.get().getFullName() + " has been updated");
    	}        
        return redirect(routes.Teams.list(0, "fullName", "asc", ""));
    }
    
    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the team to delete
     */
    public static Result delete(Long id) {
    	Team team = Team.findById(id);
        team.delete();
        flash("success", "Team has been deleted");
        return redirect(routes.Teams.list(0, "fullName", "asc", ""));
    }
}
