package controllers;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.DraftDao;
import components.dao.RfiReplyDao;
import components.dao.WithdrawalRequestDao;
import models.enums.DraftType;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import models.Amendment;
import models.File;
import models.RfiReply;
import models.WithdrawalRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DownloadController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

  private final RfiReplyDao rfiReplyDao;
  private final DraftDao draftDao;
  private final AmendmentDao amendmentDao;
  private final WithdrawalRequestDao withdrawalRequestDao;

  @Inject
  public DownloadController(RfiReplyDao rfiReplyDao,
                            DraftDao draftDao,
                            AmendmentDao amendmentDao,
                            WithdrawalRequestDao withdrawalRequestDao) {
    this.rfiReplyDao = rfiReplyDao;
    this.draftDao = draftDao;
    this.amendmentDao = amendmentDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
  }

  public Result getRfiFile(String rfiId, String fileId) {
    RfiReply rfiReply = rfiReplyDao.getRfiReply(rfiId);
    if (rfiReply != null) {
      return getFile(rfiReply.getAttachments(), fileId);
    } else {
      List<File> files = draftDao.getDraftAttachments(rfiId, DraftType.RFI_REPLY);
      return getFile(files, fileId);
    }
  }

  public Result getAmendmentFile(String appId, String fileId) {
    List<Amendment> amendments = amendmentDao.getAmendments(appId);
    List<File> attachments = amendments.stream()
        .map(Amendment::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT);
    List<File> files = ListUtils.union(attachments, draftAttachments);
    return getFile(files, fileId);
  }

  public Result getWithdrawalFile(String appId, String fileId) {
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.WITHDRAWAL);
    List<File> attachments = withdrawalRequestDao.getWithdrawalRequestsByAppId(appId).stream()
        .map(WithdrawalRequest::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<File> files = ListUtils.union(attachments, draftAttachments);
    return getFile(files, fileId);
  }

  private Result getFile(List<File> files, String fileId) {
    Optional<File> file = files.stream()
        .filter(f -> f.getId().equals(fileId))
        .findAny();
    if (file.isPresent()) {
      return ok(new java.io.File(file.get().getUrl()));
    } else {
      LOGGER.warn("No file found with fileId {}", fileId);
      return notFound();
    }
  }

}
