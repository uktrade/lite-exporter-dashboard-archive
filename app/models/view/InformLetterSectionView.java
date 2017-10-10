package models.view;

import java.util.List;

public class InformLetterSectionView {

  private final boolean showNewIndicator;
  private final List<InformLetterView> informLetterViews;

  public InformLetterSectionView(boolean showNewIndicator, List<InformLetterView> informLetterViews) {
    this.showNewIndicator = showNewIndicator;
    this.informLetterViews = informLetterViews;
  }

  public boolean isShowNewIndicator() {
    return showNewIndicator;
  }

  public List<InformLetterView> getInformLetterViews() {
    return informLetterViews;
  }

}
