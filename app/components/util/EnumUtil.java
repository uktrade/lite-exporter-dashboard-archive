package components.util;

import org.apache.commons.lang3.EnumUtils;

public class EnumUtil {

  public static <T extends Enum<T>> T parse(Class<T> enumClass, String str, T fallback) {
    if (str != null) {
      T value = EnumUtils.getEnum(enumClass, str.toUpperCase());
      if (value != null) {
        return value;
      } else {
        return fallback;
      }
    } else {
      return fallback;
    }
  }

}
