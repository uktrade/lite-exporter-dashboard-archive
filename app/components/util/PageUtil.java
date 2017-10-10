package components.util;

import java.util.List;
import models.Page;

public class PageUtil {

  private static final int SIZE = 10;

  public static <T> Page<T> getPage(Integer pageRequested, List<T> completeList) {
    int total = completeList.size();
    int currentPage = getCurrentPage(pageRequested, total);
    int pageCount = (int) Math.ceil((double) total / (double) SIZE);
    int from = getFrom(currentPage);
    int to = getTo(currentPage, total);
    List<T> items = completeList.subList(from - 1, to);
    return new Page<>(currentPage, pageCount, from, to, total, items);
  }

  private static int getCurrentPage(Integer requested, int listSize) {
    int positive;
    if (requested == null || requested < 1) {
      positive = 1;
    } else {
      positive = requested;
    }
    return Math.min(positive, (listSize / SIZE) + 1);
  }

  private static int getFrom(int currentPage) {
    return ((currentPage - 1) * SIZE) + 1;
  }

  private static int getTo(int currentPage, int listSize) {
    return Math.min(currentPage * SIZE, listSize);
  }

}
