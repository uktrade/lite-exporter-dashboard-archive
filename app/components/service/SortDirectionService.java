package components.service;

import models.enums.SortDirection;

public interface SortDirectionService {

  long definedCount(SortDirection... sortDirections);

  SortDirection parse(String sortDirection);

  SortDirection next(SortDirection sortDirection);

  String toParam(SortDirection sortDirection);

  String toNextParam(SortDirection sortDirection);
}
