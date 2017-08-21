package models;

import java.util.List;

public class Page<T> {

  private final int currentPage;
  private final int pageCount;
  private final int from;
  private final int to;
  private final int total;
  private final List<T> items;

  public Page(int currentPage, int pageCount, int from, int to, int total, List<T> items) {
    this.currentPage = currentPage;
    this.pageCount = pageCount;
    this.from = from;
    this.to = to;
    this.total = total;
    this.items = items;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getPageCount() {
    return pageCount;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public int getTotal() {
    return total;
  }

  public List<T> getItems() {
    return items;
  }

}
