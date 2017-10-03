package models;

import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.util.List;

public class WithdrawalInformation {

  private String appId;
  private List<WithdrawalRequest> openWithdrawalRequests;
  private List<WithdrawalRejection> withdrawalRejections;
  private WithdrawalRequest approvedWithdrawalRequest;
  private WithdrawalApproval withdrawalApproval;

  public WithdrawalInformation(String appId, List<WithdrawalRequest> openWithdrawalRequests, List<WithdrawalRejection> withdrawalRejections, WithdrawalRequest approvedWithdrawalRequest, WithdrawalApproval withdrawalApproval) {
    this.appId = appId;
    this.openWithdrawalRequests = openWithdrawalRequests;
    this.withdrawalRejections = withdrawalRejections;
    this.approvedWithdrawalRequest = approvedWithdrawalRequest;
    this.withdrawalApproval = withdrawalApproval;
  }

  public String getAppId() {
    return appId;
  }

  public List<WithdrawalRequest> getOpenWithdrawalRequests() {
    return openWithdrawalRequests;
  }

  public List<WithdrawalRejection> getWithdrawalRejections() {
    return withdrawalRejections;
  }

  public WithdrawalRequest getApprovedWithdrawalRequest() {
    return approvedWithdrawalRequest;
  }

  public WithdrawalApproval getWithdrawalApproval() {
    return withdrawalApproval;
  }

}
