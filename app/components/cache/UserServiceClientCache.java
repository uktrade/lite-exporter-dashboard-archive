package components.cache;

import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public interface UserServiceClientCache {

  UserPrivilegesView getUserPrivilegeView(String userId);

}
