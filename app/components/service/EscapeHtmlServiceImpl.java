package components.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class EscapeHtmlServiceImpl implements EscapeHtmlService {

  private static final Whitelist WHITELIST = new Whitelist().addTags("p", "b", "i", "u");
  private static final Document.OutputSettings OUTPUT_SETTINGS = new Document.OutputSettings().prettyPrint(false);

  @Override
  public String escape(String html) {
    if (html == null) {
      return null;
    } else {
      String safe = Jsoup.clean(html, "", WHITELIST, OUTPUT_SETTINGS);
      Document document = Jsoup.parse(safe);
      document.body().getElementsByTag("b").tagName("strong").addClass("bold");
      document.body().getElementsByTag("i").tagName("em");
      document.body().getElementsByTag("u").tagName("span").addClass("underline");
      document.outputSettings(OUTPUT_SETTINGS);
      return document.body().html();
    }
  }

}
