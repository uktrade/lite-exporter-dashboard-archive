package components.service;

import java.util.List;
import models.AppData;
import models.Application;

public interface UserPermissionService {

  boolean canAddAmendmentOrWithdrawalRequest(String userId, AppData appData);

  boolean canAddRfiReply(String userId, String rfiId, AppData appData);

  List<String> getCustomerIdsWithViewingPermission(String userId);

  boolean canViewApplication(String userId, Application application);

  boolean hasCreatorOrAdminPermission(String userId, AppData appData);

}
