package service;

import static components.util.RandomIdUtil.rfiId;
import static org.assertj.core.api.Assertions.assertThat;

import components.service.WorkingDayService;
import components.service.WorkingDayServiceImpl;
import components.util.HolidayUtil;
import components.util.TimeUtil;
import models.AppData;
import models.Rfi;
import models.RfiReply;
import models.RfiWithdrawal;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
    int days = workingDayService.calculateWorkingDays(start, end, noRfiAppData());
    assertThat(days).isEqualTo(1);
  }

  @Test
  public void differentDayButLessThanTwentyFourHoursShouldReturnOneDay() {
    long start = TimeUtil.time(2018, 2, 7, 10, 30);
    long end = TimeUtil.time(2018, 2, 8, 9, 30);
    int days = workingDayService.calculateWorkingDays(start, end, noRfiAppData());
    assertThat(days).isEqualTo(1);
  }

  @Test
  public void twentyFiveHoursShouldReturnTwoDays() {
    long start = TimeUtil.time(2018, 2, 7, 10, 30);
    long end = TimeUtil.time(2018, 2, 8, 11, 30);
    int days = workingDayService.calculateWorkingDays(start, end, noRfiAppData());
    assertThat(days).isEqualTo(2);
  }

  @Test
  public void christmasAndBoxingDayShouldNotBeCounted() {
    long start = TimeUtil.time(2018, 12, 24, 10, 30);
    long end = TimeUtil.time(2018, 12, 27, 11, 30);
    int days = workingDayService.calculateWorkingDays(start, end, noRfiAppData());
    assertThat(days).isEqualTo(2);
  }

  @Test
  public void weekendShouldNotBeCounted() {
    long start = TimeUtil.time(2018, 2, 8, 10, 30);
    long end = TimeUtil.time(2018, 2, 15, 10, 0);
    int days = workingDayService.calculateWorkingDays(start, end, noRfiAppData());
    assertThat(days).isEqualTo(5);
  }

  @Test
  public void openRfiShouldNotBeCounted() {
    // Monday
    long start = TimeUtil.time(2018, 2, 5, 10, 30);
    long end = TimeUtil.time(2018, 2, 9, 10, 29);
    Rfi rfi = createRfi(TimeUtil.time(2018, 2, 7, 10, 29));
    AppData appData = createAppData(Collections.singletonList(rfi), new ArrayList<>(), new ArrayList<>());
    int days = workingDayService.calculateWorkingDays(start, end, appData);
    assertThat(days).isEqualTo(2);
  }

  @Test
  public void answeredRfiShouldNotBeCounted() {
    // Monday
    long start = TimeUtil.time(2018, 2, 5, 10, 30);
    long end = TimeUtil.time(2018, 2, 9, 10, 29);

    Rfi rfi = createRfi(TimeUtil.time(2018, 2, 7, 10, 29));
    RfiReply rfiReply = createRfiReply(rfi.getId(), TimeUtil.time(2018, 2, 8, 10, 28));

    AppData appData = createAppData(Collections.singletonList(rfi), Collections.singletonList(rfiReply), new ArrayList<>());
    int days = workingDayService.calculateWorkingDays(start, end, appData);
    assertThat(days).isEqualTo(3);
  }

  @Test
  public void withdrawnRfiShouldNotBeCounted() {
    // Monday
    long start = TimeUtil.time(2018, 2, 5, 10, 30);
    long end = TimeUtil.time(2018, 2, 9, 10, 29);

    Rfi rfi = createRfi(TimeUtil.time(2018, 2, 7, 10, 29));
    RfiWithdrawal rfiWithdrawal = createRfiWithdrawal(rfi.getId(), TimeUtil.time(2018, 2, 8, 10, 28));

    AppData appData = createAppData(Collections.singletonList(rfi), new ArrayList<>(), Collections.singletonList(rfiWithdrawal));
    int days = workingDayService.calculateWorkingDays(start, end, appData);
    assertThat(days).isEqualTo(3);
  }

  private RfiWithdrawal createRfiWithdrawal(String rfiId, Long createdTimestamp) {
    return new RfiWithdrawal(null, rfiId, null, createdTimestamp, null, null);
  }

  private RfiReply createRfiReply(String rfiId, Long createdTimestamp) {
    RfiReply rfiReply = new RfiReply();
    rfiReply.setRfiId(rfiId);
    rfiReply.setCreatedTimestamp(createdTimestamp);
    return rfiReply;
  }

  private Rfi createRfi(Long createdTimestamp) {
    return new Rfi(rfiId(), null, createdTimestamp, null, null, null, null);
  }

  private AppData noRfiAppData() {
    return createAppData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  private AppData createAppData(List<Rfi> rfiList, List<RfiReply> rfiReplies, List<RfiWithdrawal> rfiWithdrawals) {
    return new AppData(null,
        null,
        null,
        null,
        null,
        null,
        null,
        rfiList,
        rfiReplies,
        rfiWithdrawals,
        null,
        null,
        null,
        null,
        null,
        new ArrayList<>());
  }

}
