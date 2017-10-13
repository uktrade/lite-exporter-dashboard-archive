package controllers;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.DraftDao;
import components.dao.RfiReplyDao;
import components.dao.WithdrawalRequestDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import models.Amendment;
import models.File;
import models.RfiReply;
import models.WithdrawalRequest;
import models.enums.DraftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;

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

  public Result getRfiReplyFile(String rfiId, String fileId) {
    List<File> files = new ArrayList<>();
    files.addAll(draftDao.getDraftAttachments(rfiId, DraftType.RFI_REPLY));
    RfiReply rfiReply = rfiReplyDao.getRfiReply(rfiId);
    if (rfiReply != null) {
      files.addAll(rfiReply.getAttachments());
    }
    return getFile(files, fileId);
  }

  public Result getFile(String appId, String fileId) {
    List<File> files = new ArrayList<>();
    files.addAll(draftDao.getDraftAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL));
    amendmentDao.getAmendments(appId).stream()
        .map(Amendment::getAttachments)
        .flatMap(List::stream)
        .forEach(files::add);
    withdrawalRequestDao.getWithdrawalRequestsByAppId(appId).stream()
        .map(WithdrawalRequest::getAttachments)
        .flatMap(List::stream)
        .forEach(files::add);
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
