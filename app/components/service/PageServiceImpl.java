package components.service;

import models.Page;

import java.util.List;

public class PageServiceImpl implements PageService {

  private static final int SIZE = 10;

  @Override
  public <T> Page<T> getPage(Integer pageRequested, List<T> list) {
    int total = list.size();
    int currentPage = getCurrentPage(pageRequested, total);
    int pageCount = total / SIZE + 1;
    int from = getFrom(currentPage);
    int to = getTo(currentPage, total);
    List<T> items = list.subList(from - 1, to);
    return new Page<>(currentPage, pageCount, from, to, total, items);
  }

  public int getCurrentPage(Integer requested, int listSize) {
    int nonNull = requested == null ? 1 : requested;
    return Math.min(nonNull, (listSize / SIZE) + 1);
  }

  public int getFrom(Integer currentPage) {
    return ((currentPage - 1) * SIZE) + 1;
  }

  public int getTo(Integer currentPage, int listSize) {
    return Math.min(currentPage * SIZE, listSize);
  }

}
