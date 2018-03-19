package service;

import static org.assertj.core.api.Assertions.assertThat;

import components.service.WorkingDayService;
import components.service.WorkingDayServiceImpl;
import components.util.HolidayUtil;
import components.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class WorkingDayServiceTest {

  private WorkingDayService workingDayService;

  @Before
  public void setup() {
    List<LocalDate> holidays = HolidayUtil.loadHolidaysFromFile("holidays.json");
    workingDayService = new WorkingDayServiceImpl(holidays);
  }

  @Test
  public void sameDayShouldReturnOneDay() {
    long start = TimeUtil.time(2018, 2, 7, 10, 30);
    long end = TimeUtil.time(2018, 2, 7, 11, 30);
    int days = workingDayService.calculateWorkingDays(start, end);
    assertThat(days).isEqualTo(1);
  }

  @Test
  public void differentDayButLessThanTwentyFourHoursShouldReturnOneDay() {
    long start = TimeUtil.time(2018, 2, 7, 10, 30);
    long end = TimeUtil.time(2018, 2, 8, 9, 30);
    int days = workingDayService.calculateWorkingDays(start, end);
    assertThat(days).isEqualTo(1);
  }

  @Test
  public void twentyFiveHoursShouldReturnTwoDays() {
    long start = TimeUtil.time(2018, 2, 7, 10, 30);
    long end = TimeUtil.time(2018, 2, 8, 11, 30);
    int days = workingDayService.calculateWorkingDays(start, end);
    assertThat(days).isEqualTo(2);
  }

  @Test
  public void christmasAndBoxingDayShouldNotBeCounted() {
    long start = TimeUtil.time(2018, 12, 24, 10, 30);
    long end = TimeUtil.time(2018, 12, 27, 11, 30);
    int days = workingDayService.calculateWorkingDays(start, end);
    assertThat(days).isEqualTo(2);
  }

  @Test
  public void weekendShouldNotBeCounted() {
    long start = TimeUtil.time(2018, 2, 8, 10, 30);
    long end = TimeUtil.time(2018, 2, 15, 10, 0);
    int days = workingDayService.calculateWorkingDays(start, end);
    assertThat(days).isEqualTo(5);
  }

}
