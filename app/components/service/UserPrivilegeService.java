package components.service;

import models.AppData;

public interface UserPrivilegeService {

  boolean isAccessAllowed(String userId, String siteId, String customerId);

  boolean hasRfiReplyPermission(String userId, String rfiId, AppData appData);
}
