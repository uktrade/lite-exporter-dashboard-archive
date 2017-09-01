package components.service;

import static play.mvc.Controller.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApplicationListState;
import models.LicenceListState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CacheServiceImpl implements CacheService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String APPLICATION_LIST_STATE = "applicationListState";
  private static final String LICENSE_LIST_STATE = "licenseListState";

  @Override
  public ApplicationListState getApplicationListState(String tab, String sort, String direction, String company, String show, Integer page) {
    ApplicationListState state = null;
    if (tab == null && sort == null && direction == null && company == null && show == null && page == null) {
      state = getFromSession(APPLICATION_LIST_STATE, ApplicationListState.class);
    }
    if (state == null) {
      state = new ApplicationListState(tab, sort, direction, company, show, page);
      save(APPLICATION_LIST_STATE, state);
    }
    return state;
  }

  @Override
  public LicenceListState getLicenseListState(String tab, String sort, String direction, Integer page) {
    LicenceListState state = null;
    if (tab != null && sort != null && direction != null && page != null) {
      state = getFromSession(LICENSE_LIST_STATE, LicenceListState.class);
    }
    if (state == null) {
      state = new LicenceListState(tab, sort, direction, page);
      save(LICENSE_LIST_STATE, state);
    }
    return state;
  }

  private void save(String key, Object value) {
    try {
      session(key, MAPPER.writeValueAsString(value));
    } catch (JsonProcessingException error) {
      LOGGER.error(String.format("Unable to save state %s", key), error);
    }
  }

  private <T> T getFromSession(String key, Class<T> clazz) {
    String value = session(key);
    if (value != null) {
      try {
        return MAPPER.readValue(value, clazz);
      } catch (IOException error) {
        LOGGER.error(String.format("Unable to parse value %s to state %s", value, key), error);
        return null;
      }
    } else {
      return null;
    }
  }

}
