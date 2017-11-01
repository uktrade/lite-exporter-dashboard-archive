package components.service;

import java.util.List;
import models.AppData;
import models.Application;

public interface UserPrivilegeService {

  boolean isAmendmentOrWithdrawalAllowed(String userId, AppData appData);

  boolean isReplyAllowed(String userId, String rfiId, AppData appData);

  List<String> getCustomerIdsWithBasicPermission(String userId);

  boolean isApplicationViewAllowed(String userId, Application application);

  boolean hasCreatorOrAdminPermission(String userId, AppData appData);
}
