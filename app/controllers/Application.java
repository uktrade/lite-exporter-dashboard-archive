package controllers;

import play.mvc.*;
import play.data.Form;
import play.data.FormFactory;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        FormFactory formFactory = new FormFactory(null,null,null);
        Form form = formFactory.form();
        return ok(index.render(form));
    }

}
