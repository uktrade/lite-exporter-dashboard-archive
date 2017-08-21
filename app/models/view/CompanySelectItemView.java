package models.view;

public class CompanySelectItemView {

  private final String companyId;
  private final String companyName;

  public CompanySelectItemView(String companyId, String companyName) {
    this.companyId = companyId;
    this.companyName = companyName;
  }

  public String getCompanyId() {
    return companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

}
