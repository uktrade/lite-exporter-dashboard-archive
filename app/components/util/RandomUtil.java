package components.util;

import java.util.UUID;

public class RandomUtil {

  public static String smallRandom(String prefix) {
    return prefix.toLowerCase() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 5);
  }

  public static String random(String prefix) {
    return prefix.toLowerCase() + "_" + UUID.randomUUID().toString().replace("-", "");
  }

}
