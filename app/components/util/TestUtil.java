package components.util;

public class TestUtil {

  public static String wrapSiteId(String userId, String siteId) {
    return userId + "_" + siteId;
  }

  public static String unwrapSiteId(String siteId) {
    return removeUserId(siteId);
  }

  public static String wrapCustomerId(String userId, String customerId) {
    return userId + "_" + customerId;
  }

  public static String unwrapCustomerId(String customerId) {
    return removeUserId(customerId);
  }

  private static String removeUserId(String str) {
    int first = str.indexOf("_");
    if (first != -1) {
      return str.substring(first + 1);
    } else {
      return str;
    }
  }

}
