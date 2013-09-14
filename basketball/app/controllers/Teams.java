package controllers;

import static play.data.Form.form;
import models.Team;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.team.createTeam;
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
        return redirect(routes.Teams.listTeams(0, "fullName", "asc", ""));
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
        return redirect(routes.Teams.listTeams(0, "fullName", "asc", ""));
    }
    
    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the team to delete
     */
    public static Result deleteTeam(Long id) {
        Team.find.ref(id).delete();
        flash("success", "Team has been deleted");
        return redirect(routes.Teams.listTeams(0, "fullName", "asc", ""));
    }
}
