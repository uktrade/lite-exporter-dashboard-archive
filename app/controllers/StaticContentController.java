package controllers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import scala.Option;
import views.html.util.heading;

import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

public class StaticContentController extends Controller {

  private final views.html.staticContent staticContent;

  @Inject
  public StaticContentController(views.html.staticContent staticContent) {
    this.staticContent = staticContent;
  }

  public enum StaticHtml {
    FAIR_PROCESSING_NOTE("fairProcessingNote.html", "Fair processing note", true);

    private final String filename;
    private final String title;
    private final Html pageHeading;
    private final boolean hideBackLink;

    StaticHtml(String filename, String title, boolean hideBackLink, Html pageHeading) {
      this.filename = filename;
      this.title = title;
      this.pageHeading = pageHeading;
      this.hideBackLink = hideBackLink;
    }

    StaticHtml(String filename, String title, boolean hideBackLink) {
      // Standard page headings are shown by default
      this(filename, title, hideBackLink, HEADING_STANDARD_FUNC.apply(title));
    }

  }

  private static final Function<String, Html> HEADING_STANDARD_FUNC = title -> heading.render(title, "heading-large", false);

  public Result renderStaticHtml(StaticHtml staticHtml) {
    try {
      URL resource = getClass().getClassLoader().getResource("static/html/" + staticHtml.filename);
      if (resource == null) {
        throw new RuntimeException("Not a file: " + staticHtml.filename);
      } else {
        return ok(staticContent.render(staticHtml.title,
            Option.apply(staticHtml.pageHeading),
            new Html(Resources.toString(resource, Charsets.UTF_8)),
            staticHtml.hideBackLink));
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to read", e);
    }
  }

  public Result renderFairProcessingNote() {
    return renderStaticHtml(StaticHtml.FAIR_PROCESSING_NOTE);
  }

}
