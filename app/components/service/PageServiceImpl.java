package components.service;

import models.Page;

import java.util.List;

public class PageServiceImpl implements PageService {

  private static final int SIZE = 10;

  @Override
  public <T> Page<T> getPage(Integer pageRequested, List<T> completeList) {
    int total = completeList.size();
    int currentPage = getCurrentPage(pageRequested, total);
    int pageCount = total / SIZE + 1;
    int from = getFrom(currentPage);
    int to = getTo(currentPage, total);
    List<T> items = completeList.subList(from - 1, to);
    return new Page<>(currentPage, pageCount, from, to, total, items);
  }

  private int getCurrentPage(Integer requested, int listSize) {
    int positive;
    if (requested == null || requested < 1) {
      positive = 1;
    } else {
      positive = requested;
    }
    return Math.min(positive, (listSize / SIZE) + 1);
  }

  private int getFrom(int currentPage) {
    return ((currentPage - 1) * SIZE) + 1;
  }

  private int getTo(int currentPage, int listSize) {
    return Math.min(currentPage * SIZE, listSize);
  }

}
