package components.util;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import scala.Option;

public class EnumUtil {

  public static <T extends Enum<T>> T parse(Class<T> enumClass, Option<String> str, T fallback) {
    if (str.isDefined()) {
      Optional<T> optional = Enums.getIfPresent(enumClass, str.get().toUpperCase());
      if (optional.isPresent()) {
        return optional.get();
      } else {
        return fallback;
      }
    } else {
      return fallback;
    }
  }

}
