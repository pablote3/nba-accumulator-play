package controllers;

import static play.data.Form.form;
import models.entity.Official;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.official.editOfficial;
import views.html.official.listOfficials;

public class Officials extends Controller {

    /**
     * Display the paginated list of officials.
     *
     * @param page Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order Sort order (either asc or desc)
     * @param filter Filter applied on official names
     */
    public static Result list(int page, String sortBy, String order, String filter) {
        return ok(
            listOfficials.render(
                Official.page(page, 15, sortBy, order, filter), sortBy, order, filter
            )
        );
    }
    
    /**
     * Return the id of an existing official.
     *
     * @param key of the official to search for
     * @param value of the official to search for
     */
    public static Result search(String key, String value) {
        try {
        	Official official = Official.find.where().eq(key, value).findUnique();
			return ok(official.getId().toString());
		} catch (Exception e) {
			return badRequest();
		}
    }

    /**
     * Display (GET) the 'edit form' of an existing official or a new official.
     *
     * @param id Id of the official to edit
     */
    public static Result edit(Long id) {
    	Form<Official> form = null;
    	if (id == -1L) {
    		form = form(Official.class);
    	}
    	else {
            form = form(Official.class).fill(Official.find.byId(id));
    	}
        return ok(
        	editOfficial.render(id, form)
	    );
    } 
    
    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the official to edit
     */
    public static Result save(Long id) {
    	Form<Official> form = form(Official.class).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(editOfficial.render(id, form));
        }
    	if (id == -1L) {
    		form.get().save();
            flash("success", "Official " + form.get().getFirstName() + " " +  form.get().getLastName() + " has been created");
    	}
    	else {
            form.get().update(id);
            flash("success", "Official " + form.get().getFirstName() + " " +  form.get().getLastName() + " has been updated");
    	}        
        return redirect(routes.Officials.list(0, "number", "asc", ""));
    }
    
    /**
     * Handle (POST) the 'edit form' submission 
     *
     * @param id Id of the official to delete
     */
    public static Result delete(Long id) {
    	Official.find.ref(id).delete();
        flash("success", "Official has been deleted");
        return redirect(routes.Officials.list(0, "number", "asc", ""));
    }
}
