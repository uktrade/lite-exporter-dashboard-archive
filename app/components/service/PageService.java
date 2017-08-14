package components.service;

import models.Page;

import java.util.List;

public interface PageService {

  <T> Page<T> getPage(Integer pageRequested, List<T> list);

}
