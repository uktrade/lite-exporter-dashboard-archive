package components.service;

public interface UserPrivilegeService {

  boolean isAccessAllowed(String userId, String customerId);

}
