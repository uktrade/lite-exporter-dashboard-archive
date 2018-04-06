package components.service;

import com.google.inject.Inject;

/**
 * By binding this empty implementation on startup, we allow tests to bind their own version of startUpService, for
 * example to shutdown ehcache instances
 */
public class StartUpServiceImpl implements StartUpService {

  @Inject
  public StartUpServiceImpl() {
  }

}
