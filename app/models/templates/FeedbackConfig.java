package models.templates;

public class FeedbackConfig {
  private final String feedbackUrl;

  public FeedbackConfig(String feedbackUrl) {
    this.feedbackUrl = feedbackUrl;
  }

  public String getFeedbackUrl() {
    return feedbackUrl;
  }
}
