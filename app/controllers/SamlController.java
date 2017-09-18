package controllers;

import components.auth.SamlAuthorizer;
import components.common.CommonContextAction;
import components.common.auth.SpireSAML2Client;
import org.pac4j.play.java.Secure;
import play.mvc.Controller;
import play.mvc.With;

@With(CommonContextAction.class)
@Secure(clients = SpireSAML2Client.CLIENT_NAME, authorizers = SamlAuthorizer.AUTHORIZER_NAME)
public class SamlController extends Controller {

}
