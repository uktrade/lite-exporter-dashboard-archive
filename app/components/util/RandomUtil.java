package components.util;

import java.util.UUID;

public class RandomUtil {

  public static String random(String prefix) {
    return prefix.toLowerCase() + "_" + UUID.randomUUID().toString().replace("-", "");
  }

}
