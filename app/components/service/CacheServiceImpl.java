package components.service;

import static play.mvc.Controller.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApplicationListState;
import models.LicenseListState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.io.IOException;

public class CacheServiceImpl implements CacheService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String APPLICATION_LIST_STATE = "applicationListState";
  private static final String LICENSE_LIST_STATE = "licenseListState";

  @Override
  public ApplicationListState getApplicationListState(Option<String> tab, Option<String> date, Option<String> status, Option<String> show, Option<String> company, Option<Integer> page) {
    ApplicationListState state = null;
    if (tab.isEmpty() && date.isEmpty() && status.isEmpty() && show.isEmpty() && company.isEmpty() && page.isEmpty()) {
      state = getFromSession(APPLICATION_LIST_STATE, ApplicationListState.class);
    }
    if (state == null) {
      state = new ApplicationListState(parse(tab), parse(date), parse(status), parse(show), parse(company), parseNumber(page));
      save(APPLICATION_LIST_STATE, state);
    }
    return state;
  }

  @Override
  public LicenseListState getLicenseListState(Option<String> tab, Option<String> reference, Option<String> licensee, Option<String> site, Option<String> date, Option<Integer> page) {
    LicenseListState state = null;
    if (tab.isEmpty() && reference.isEmpty() && licensee.isEmpty() && site.isEmpty() && date.isEmpty() && page.isEmpty()) {
      state = getFromSession(LICENSE_LIST_STATE, LicenseListState.class);
    }
    if (state == null) {
      state = new LicenseListState(parse(tab), parse(reference), parse(licensee), parse(site), parse(date), parseNumber(page));
      save(LICENSE_LIST_STATE, state);
    }
    return state;
  }

  private String parse(Option<String> str) {
    return str.isDefined() ? str.get() : null;
  }

  private Integer parseNumber(Option<Integer> number) {
    return number.isDefined() ? number.get() : null;
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
