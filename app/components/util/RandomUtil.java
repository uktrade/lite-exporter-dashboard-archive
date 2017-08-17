package components.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

public class RandomUtil {

  public static String smallRandom(String prefix) {
    return prefix.toLowerCase() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 5);
  }

  public static String random(String prefix) {
    return prefix.toLowerCase() + "_" + UUID.randomUUID().toString().replace("-", "");
  }

  public static String randomNumber(String prefix) {
    return prefix + randomNumber(12);
  }

  private static String randomNumber(int length) {
    String str = "";
    for (int i = 0; i < length; i++) {
      str = str + RandomUtils.nextInt(0, 9);
    }
    return str;
  }

}
