package components.service;

import components.util.ApplicationUtil;
import components.util.TimeUtil;
import models.AppData;
import models.Rfi;
import models.RfiReply;
import models.RfiWithdrawal;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkingDayServiceImpl implements WorkingDayService {

  private final Set<LocalDate> holidays;

  public WorkingDayServiceImpl(List<LocalDate> holidays) {
    this.holidays = new HashSet<>(holidays);
  }

  @Override
  public int calculateWorkingDays(long start, long end, AppData appData) {
    List<Pair<LocalDateTime, LocalDateTime>> rfiTimeSpans = createRfiTimeSpans(appData);

    if (start >= end) {
      return 0;
    } else {
      LocalDateTime startLocalDate = TimeUtil.toLocalDateTime(start);
      LocalDateTime endLocalDate = TimeUtil.toLocalDateTime(end);

      int hours = 0;
      LocalDateTime now = startLocalDate;

      while (endLocalDate.isAfter(now)) {

        if (!isWeekend(now) && !isBankHoliday(now) && !isRfi(now, least(now.plusHours(1), endLocalDate), rfiTimeSpans)) {
          hours++;
        }
        now = now.plusHours(1);
      }

      int days = (int) Math.ceil(hours / 24d);
      return Math.max(1, days);
    }
  }

  private LocalDateTime least(LocalDateTime left, LocalDateTime right) {
    return left.isBefore(right) ? left : right;
  }

  private List<Pair<LocalDateTime, LocalDateTime>> createRfiTimeSpans(AppData appData) {
    List<Rfi> rfiList = ApplicationUtil.getAllRfi(appData);
    Map<String, RfiWithdrawal> rfiIdToRfiWithdrawal = ApplicationUtil.getAllRfiWithdrawals(appData).stream()
        .collect(Collectors.toMap(RfiWithdrawal::getRfiId, Function.identity()));
    Map<String, RfiReply> rfiIdToRfiReply = ApplicationUtil.getAllRfiReplies(appData).stream()
        .collect(Collectors.toMap(RfiReply::getRfiId, Function.identity()));
    List<Pair<LocalDateTime, LocalDateTime>> rfiTimeSpans = new ArrayList<>();
    for (Rfi rfi : rfiList) {
      RfiWithdrawal rfiWithdrawal = rfiIdToRfiWithdrawal.get(rfi.getId());
      RfiReply rfiReply = rfiIdToRfiReply.get(rfi.getId());
      LocalDateTime created = TimeUtil.toLocalDateTime(rfi.getCreatedTimestamp());
      if (rfiWithdrawal != null) {
        rfiTimeSpans.add(ImmutablePair.of(created, TimeUtil.toLocalDateTime(rfiWithdrawal.getCreatedTimestamp())));
      } else if (rfiReply != null) {
        rfiTimeSpans.add(ImmutablePair.of(created, TimeUtil.toLocalDateTime(rfiReply.getCreatedTimestamp())));
      } else {
        rfiTimeSpans.add(ImmutablePair.of(created, LocalDateTime.now()));
      }
    }
    return rfiTimeSpans;
  }

  private boolean isRfi(LocalDateTime start, LocalDateTime end, List<Pair<LocalDateTime, LocalDateTime>> rfiTimeSpans) {
    return rfiTimeSpans.stream().anyMatch(timeSpan -> doOverlap(start, end, timeSpan.getLeft(), timeSpan.getRight()));
  }

  // https://stackoverflow.com/a/325964
  private boolean doOverlap(LocalDateTime startA, LocalDateTime endA, LocalDateTime startB, LocalDateTime endB) {
    return (startA.isBefore(endB) || startA.isEqual(endB)) && (endA.isAfter(startB) || endA.isEqual(startB));
  }

  private boolean isWeekend(LocalDateTime localDateTime) {
    return localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
  }

  private boolean isBankHoliday(LocalDateTime localDateTime) {
    return holidays.contains(localDateTime.toLocalDate());
  }

}
