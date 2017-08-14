package components.service;

import components.util.EnumUtil;
import models.enums.SortDirection;

import java.util.Objects;
import java.util.stream.Stream;

public class SortDirectionServiceImpl implements SortDirectionService {

  @Override
  public long definedCount(SortDirection... sortDirections) {
    return Stream.of(sortDirections).filter(Objects::nonNull).count();
  }

  @Override
  public SortDirection parse(String sortDirection) {
    return EnumUtil.parse(SortDirection.class, sortDirection, null);
  }

  @Override
  public SortDirection next(SortDirection sortDirection) {
    if (sortDirection == SortDirection.DESC) {
      return SortDirection.ASC;
    } else {
      return SortDirection.DESC;
    }
  }

  @Override
  public String toParam(SortDirection sortDirection) {
    if (sortDirection == null) {
      return null;
    } else {
      return sortDirection.toString().toLowerCase();
    }
  }

  @Override
  public String toNextParam(SortDirection sortDirection) {
    return toParam(next(sortDirection));
  }

}
