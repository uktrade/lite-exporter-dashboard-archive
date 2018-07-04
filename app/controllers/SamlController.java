package controllers;

import components.common.auth.SamlAuthorizer;
import components.common.auth.SpireSAML2Client;
import org.pac4j.play.java.Secure;
import play.mvc.Controller;

@Secure(clients = SpireSAML2Client.CLIENT_NAME, authorizers = SamlAuthorizer.AUTHORIZER_NAME)
public class SamlController extends Controller {

}
