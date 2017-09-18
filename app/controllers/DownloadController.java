package controllers;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiResponseDao;
import models.File;
import models.RfiResponse;
import models.enums.DraftType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;

import java.util.List;
import java.util.Optional;

public class DownloadController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

  private final RfiResponseDao rfiResponseDao;
  private final DraftDao draftDao;

  @Inject
  public DownloadController(RfiResponseDao rfiResponseDao, DraftDao draftDao) {
    this.rfiResponseDao = rfiResponseDao;
    this.draftDao = draftDao;
  }

  public Result getRfiFile(String rfiId, String fileId) {
    RfiResponse rfiResponse = rfiResponseDao.getRfiResponse(rfiId);
    if (rfiResponse != null) {
      return getFile(rfiResponse.getAttachments(), fileId);
    } else {
      List<File> files = draftDao.getDraftAttachments(rfiId, DraftType.RFI_RESPONSE);
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
        .filter(f -> f.getFileId().equals(fileId))
        .findAny();
    if (file.isPresent()) {
      return ok(new java.io.File(file.get().getPath()));
    } else {
      LOGGER.warn("No file found with fileId {}", fileId);
      return notFound();
    }
  }

}
