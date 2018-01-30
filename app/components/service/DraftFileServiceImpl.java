package components.service;

import com.google.inject.Inject;
import components.common.upload.FileService;
import components.dao.DraftFileDao;
import models.Attachment;
import models.enums.DraftType;

import java.util.Optional;

public class DraftFileServiceImpl implements DraftFileService {

  private final FileService fileService;
  private final DraftFileDao draftFileDao;

  @Inject
  public DraftFileServiceImpl(FileService fileService, DraftFileDao draftFileDao) {
    this.fileService = fileService;
    this.draftFileDao = draftFileDao;
  }

  @Override
  public void deleteDraftFile(String fileId, String relatedId, DraftType draftType) {
    Optional<Attachment> attachmentOptional = draftFileDao.getAttachments(relatedId, draftType).stream()
        .filter(draftFile -> draftFile.getId().equals(fileId))
        .findAny();
    if (attachmentOptional.isPresent()) {
      Attachment attachment = attachmentOptional.get();
      fileService.deleteFile(attachment.getId(), attachment.getBucket(), attachment.getFolder());
      draftFileDao.deleteDraftFile(fileId, relatedId, draftType);
    } else {
      String message = String.format("Unable to delete file with fileId %s and relatedId %s and draftType %s", fileId, relatedId, draftType);
      throw new RuntimeException(message);
    }
  }

}
