package components.service;

import com.google.inject.Inject;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.dao.RfiWithdrawalDao;
import models.Rfi;
import models.RfiWithdrawal;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RfiServiceImpl implements RfiService {

  private final RfiDao rfiDao;
  private final RfiReplyDao rfiReplyDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;

  @Inject
  public RfiServiceImpl(RfiDao rfiDao, RfiReplyDao rfiReplyDao, RfiWithdrawalDao rfiWithdrawalDao) {
    this.rfiDao = rfiDao;
    this.rfiReplyDao = rfiReplyDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
  }

  @Override
  public List<Rfi> getOpenRfiList(List<String> appIds) {
    List<Rfi> rfiList = rfiDao.getRfiList(appIds);
    List<String> rfiIds = rfiList.stream()
        .map(Rfi::getRfiId)
        .collect(Collectors.toList());
    Set<String> repliedToRfiIds = rfiReplyDao.getRfiReplies(rfiIds).stream()
        .map(RfiReply::getRfiId)
        .collect(Collectors.toSet());
    Set<String> withdrawnRfiIds = rfiWithdrawalDao.getRfiWithdrawals(rfiIds).stream()
        .map(RfiWithdrawal::getRfiId)
        .collect(Collectors.toSet());
    return rfiList.stream()
        .filter(rfi -> !repliedToRfiIds.contains(rfi.getRfiId()) && !withdrawnRfiIds.contains(rfi.getRfiId()))
        .collect(Collectors.toList());
  }

}
