package components.service;

import models.AppData;

public interface WorkingDayService {

  int calculateWorkingDays(long start, long end, AppData appData);

}
