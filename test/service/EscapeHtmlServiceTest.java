package service;

import static org.assertj.core.api.Assertions.assertThat;

import components.service.EscapeHtmlService;
import components.service.EscapeHtmlServiceImpl;
import org.junit.Test;

public class EscapeHtmlServiceTest {

  private final EscapeHtmlService escapeHtmlService = new EscapeHtmlServiceImpl();

  @Test
  public void shouldReplaceBoldTag() {
    String test = "<b>Bold</b>";
    String escaped = escapeHtmlService.escape(test);
    assertThat(escaped).isEqualTo(toHtml("<strong class='bold'>Bold</strong>"));
  }

  @Test
  public void shouldReplaceItalicTag() {
    String test = "<i>Italic</i>";
    String escaped = escapeHtmlService.escape(test);
    assertThat(escaped).isEqualTo("<em>Italic</em>");
  }

  @Test
  public void shouldReplaceUnderlineTag() {
    String test = "<u>Underlined</u>";
    String escaped = escapeHtmlService.escape(test);
    assertThat(escaped).isEqualTo(toHtml("<span class='underline'>Underlined</span>"));
  }

  @Test
  public void shouldWhitelistParagraphTag() {
    String test = "<p>Paragraph</p>";
    String escaped = escapeHtmlService.escape(test);
    assertThat(escaped).isEqualTo("<p>Paragraph</p>");
  }

  @Test
  public void shouldReplaceSeveralTagsAtOnce() {
    String test = "<i>Italic</i><i>Italic</i><p>Paragraph</p>";
    String escaped = escapeHtmlService.escape(test);
    assertThat(escaped).isEqualTo("<em>Italic</em><em>Italic</em><p>Paragraph</p>");
  }

  @Test
  public void shouldRemoveNonWhitelistedTagsAndAttributes() {
    String html = toHtml("<html><body><p class='what'>This is html.</p><a href='what' target='_blank'>end</a></body>");
    String escaped = escapeHtmlService.escape(html);
    assertThat(escaped).isEqualTo("<p>This is html.</p>end");
  }

  private String toHtml(String str) {
    return str.replace("'", "\"");
  }

}
