package components.service;

import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.LinkUtil;
import components.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AppData;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.view.PreviousRequestItemView;

public class PreviousRequestItemViewServiceImpl implements PreviousRequestItemViewService {

  @Override
  public List<PreviousRequestItemView> getPreviousRequestItemViews(AppData appData) {
    List<PreviousRequestItemView> previousRequestItemViews = new ArrayList<>();
    previousRequestItemViews.addAll(getPreviousAmendmentRequests(appData));
    previousRequestItemViews.addAll(getPreviousWithdrawalRequests(appData));
    previousRequestItemViews.sort(Comparators.PREVIOUS_REQUEST_ITEM_VIEW_CREATED_REVERSED);
    return previousRequestItemViews;
  }

  private List<PreviousRequestItemView> getPreviousAmendmentRequests(AppData appData) {
    return appData.getAmendmentRequests().stream()
        .map(amendmentRequest -> {
          String date = TimeUtil.formatDate(amendmentRequest.getCreatedTimestamp());
          Long createdTimestamp = amendmentRequest.getCreatedTimestamp();
          String type = "Amendment";
          String link = LinkUtil.getAmendmentRequestMessageLink(amendmentRequest);
          return new PreviousRequestItemView(createdTimestamp, date, type, link, null);
        }).collect(Collectors.toList());
  }

  private List<PreviousRequestItemView> getPreviousWithdrawalRequests(AppData appData) {
    Map<String, WithdrawalRejection> withdrawalRejectionMap = ApplicationUtil.getWithdrawalRejectionMap(appData);
    WithdrawalRequest approvedWithdrawalRequest = ApplicationUtil.getApprovedWithdrawalRequest(appData);
    return appData.getWithdrawalRequests().stream()
        .map(withdrawalRequest -> {
          String date = TimeUtil.formatDate(withdrawalRequest.getCreatedTimestamp());
          Long createdTimestamp = withdrawalRequest.getCreatedTimestamp();
          String type = "Withdrawal";
          String link = LinkUtil.getWithdrawalRequestMessageLink(withdrawalRequest);
          String indicator;
          if (withdrawalRejectionMap.get(withdrawalRequest.getId()) != null) {
            indicator = "rejected";
          } else if (approvedWithdrawalRequest != null && approvedWithdrawalRequest.getId().equals(withdrawalRequest.getId())) {
            indicator = "approved";
          } else {
            indicator = null;
          }
          return new PreviousRequestItemView(createdTimestamp, date, type, link, indicator);
        }).collect(Collectors.toList());
  }

}
