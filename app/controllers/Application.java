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

    public Result licenceApplication(String applicationRef, String activeTab) {
        return ok(licenceApplication.render(applicationRef, activeTab));
    }

    public Result licenceList(String activeTab) {
        return ok(licenceList.render(activeTab));
    }

    public Result licenceDetails(String licenceRef) {
        return ok(licenceDetails.render(licenceRef));
    }

}
