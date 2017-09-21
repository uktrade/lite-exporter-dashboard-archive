package controllers;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiReplyDao;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;

import java.util.List;
import java.util.Optional;

public class DownloadController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

  private final RfiReplyDao rfiReplyDao;
  private final DraftDao draftDao;

  @Inject
  public DownloadController(RfiReplyDao rfiReplyDao, DraftDao draftDao) {
    this.rfiReplyDao = rfiReplyDao;
    this.draftDao = draftDao;
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
    List<File> files = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT);
    return getFile(files, fileId);
  }

  public Result getWithdrawalFile(String appId, String fileId) {
    List<File> files = draftDao.getDraftAttachments(appId, DraftType.WITHDRAWAL);
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
