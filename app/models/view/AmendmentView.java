package models.view;

import java.util.List;
import utils.common.SelectOption;

public class AmendmentView {

  private final boolean applicationInProgress;
  private final boolean hasPendingWithdrawalRequest;
  private final boolean hasCreatorOrAdminPermission;
  private final List<PreviousRequestItemView> previousRequestItemViews;
  private final List<SelectOption> selectOptions;
  private final List<FileView> fileViews;
  private final OfficerView officerView;

  public AmendmentView(boolean applicationInProgress,
                       boolean hasPendingWithdrawalRequest,
                       boolean hasCreatorOrAdminPermission,
                       List<PreviousRequestItemView> previousRequestItemViews,
                       List<SelectOption> selectOptions,
                       List<FileView> fileViews,
                       OfficerView officerView) {
    this.applicationInProgress = applicationInProgress;
    this.hasPendingWithdrawalRequest = hasPendingWithdrawalRequest;
    this.hasCreatorOrAdminPermission = hasCreatorOrAdminPermission;
    this.previousRequestItemViews = previousRequestItemViews;
    this.selectOptions = selectOptions;
    this.fileViews = fileViews;
    this.officerView = officerView;
  }

  public boolean isApplicationInProgress() {
    return applicationInProgress;
  }

  public boolean isHasPendingWithdrawalRequest() {
    return hasPendingWithdrawalRequest;
  }

  public boolean isHasCreatorOrAdminPermission() {
    return hasCreatorOrAdminPermission;
  }

  public List<PreviousRequestItemView> getPreviousRequestItemViews() {
    return previousRequestItemViews;
  }

  public List<SelectOption> getSelectOptions() {
    return selectOptions;
  }

  public List<FileView> getFileViews() {
    return fileViews;
  }

  public OfficerView getOfficerView() {
    return officerView;
  }

}
