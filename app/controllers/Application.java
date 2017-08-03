package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.applicationList;
import views.html.licenceDetails;
import views.html.licenceList;

public class Application extends Controller {

  public String setOrRestoreTabState(String tabGroup, String activeTab, String defaultTab) {
    if (activeTab == null) {
      String sessionActiveTab = session(tabGroup + "ActiveTab");
      activeTab = (sessionActiveTab != null) ? sessionActiveTab : defaultTab;
    }
    session(tabGroup + "ActiveTab", activeTab);
    return activeTab;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(String activeTab) {
    String tab = setOrRestoreTabState("applicationList", activeTab, "created-by-you");
    return ok(applicationList.render(tab));
  }

  public Result licenceList(String activeTab) {
    String tab = setOrRestoreTabState("licenceList", activeTab, "siels");
    return ok(licenceList.render(tab));
  }

  public Result licenceDetails(String licenceRef) {
    return ok(licenceDetails.render(licenceRef));
  }

}
