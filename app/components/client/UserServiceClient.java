package components.client;

import java.util.Optional;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public interface UserServiceClient {

  Optional<UserPrivilegesView> getUserPrivilegeView(String userId);

}
