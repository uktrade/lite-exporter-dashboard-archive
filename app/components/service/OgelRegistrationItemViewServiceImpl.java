package components.service;

import com.google.inject.Inject;
import models.enums.SortDirection;
import models.view.OgelRegistrationItemView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OgelRegistrationItemViewServiceImpl implements OgelRegistrationItemViewService {

  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> REFERENCE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> LICENSEE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> SITE_COMPARATORS = new EnumMap<>(SortDirection.class);
  private static final Map<SortDirection, Comparator<OgelRegistrationItemView>> DATE_COMPARATORS = new EnumMap<>(SortDirection.class);

  static {
    Comparator<OgelRegistrationItemView> referenceComparator = Comparator.comparing(OgelRegistrationItemView::getRegistrationReference);
    Comparator<OgelRegistrationItemView> licenseeComparator = Comparator.comparing(OgelRegistrationItemView::getLicensee);
    Comparator<OgelRegistrationItemView> siteComparator = Comparator.comparing(OgelRegistrationItemView::getSite);
    Comparator<OgelRegistrationItemView> dateComparator = Comparator.comparing(OgelRegistrationItemView::getRegistrationDate);
    REFERENCE_COMPARATORS.put(SortDirection.ASC, referenceComparator);
    REFERENCE_COMPARATORS.put(SortDirection.DESC, referenceComparator.reversed());
    LICENSEE_COMPARATORS.put(SortDirection.ASC, licenseeComparator);
    LICENSEE_COMPARATORS.put(SortDirection.DESC, licenseeComparator.reversed());
    SITE_COMPARATORS.put(SortDirection.ASC, siteComparator);
    SITE_COMPARATORS.put(SortDirection.DESC, siteComparator.reversed());
    DATE_COMPARATORS.put(SortDirection.ASC, dateComparator);
    DATE_COMPARATORS.put(SortDirection.DESC, dateComparator.reversed());
  }

  private final OgelRegistrationService ogelRegistrationService;
  private final TimeFormatService timeFormatService;

  @Inject
  public OgelRegistrationItemViewServiceImpl(OgelRegistrationService ogelRegistrationService,
                                             TimeFormatService timeFormatService) {
    this.ogelRegistrationService = ogelRegistrationService;
    this.timeFormatService = timeFormatService;
  }

  @Override
  public List<OgelRegistrationItemView> getOgelRegistrationItemViews(String userId, SortDirection reference, SortDirection licensee, SortDirection site, SortDirection date) {
    List<OgelRegistrationView> ogelRegistrationViews = ogelRegistrationService.getOgelRegistrations(userId);
    List<OgelRegistrationItemView> ogelRegistrationItemViews = ogelRegistrationViews.stream().map(this::getOgelRegistrationItemView).collect(Collectors.toList());
    sort(ogelRegistrationItemViews, reference, licensee, site, date);
    return ogelRegistrationItemViews;
  }

  private void sort(List<OgelRegistrationItemView> ogelRegistrationItemViews, SortDirection reference, SortDirection licensee, SortDirection site, SortDirection date) {
    if (reference != null) {
      ogelRegistrationItemViews.sort(REFERENCE_COMPARATORS.get(reference));
    }
    if (licensee != null) {
      ogelRegistrationItemViews.sort(LICENSEE_COMPARATORS.get(licensee));
    }
    if (site != null) {
      ogelRegistrationItemViews.sort(SITE_COMPARATORS.get(site));
    }
    if (date != null) {
      ogelRegistrationItemViews.sort(DATE_COMPARATORS.get(date));
    }
  }

  private OgelRegistrationItemView getOgelRegistrationItemView(OgelRegistrationView ogelRegistrationView) {
    long registrationDateMillis = timeFormatService.parseOgelRegistrationDate(ogelRegistrationView.getRegistrationDate());
    String registrationDate = timeFormatService.formatDate(registrationDateMillis);
    return new OgelRegistrationItemView(ogelRegistrationView.getRegistrationReference(),
        ogelRegistrationView.getOgelType(),
        ogelRegistrationView.getCustomerId(),
        ogelRegistrationView.getSiteId(),
        registrationDate);
  }


}
