package components.dao.impl;

import com.google.inject.Inject;
import components.common.upload.UploadResult;
import components.dao.DraftFileDao;
import components.dao.jdbi.DraftFileJDBIDao;
import models.Attachment;
import models.DraftFile;
import models.enums.DraftType;
import org.skife.jdbi.v2.DBI;

import java.util.List;
import java.util.stream.Collectors;

public class DraftFileDaoImpl implements DraftFileDao {

  private final DraftFileJDBIDao draftFileJDBIDao;

  @Inject
  public DraftFileDaoImpl(DBI dbi) {
    this.draftFileJDBIDao = dbi.onDemand(DraftFileJDBIDao.class);
  }

  @Override
  public List<Attachment> getAttachments(String relatedId, DraftType draftType) {
    return draftFileJDBIDao.getDraftFiles(relatedId, draftType).stream()
        .map(draftFile -> new Attachment(draftFile.getId(),
            draftFile.getFilename(),
            draftFile.getBucket(),
            draftFile.getFolder(),
            draftFile.getSize()))
        .collect(Collectors.toList());
  }

  @Override
  public DraftFile getDraftFile(String id) {
    return draftFileJDBIDao.getDraftFile(id);
  }

  @Override
  public void deleteDraftFiles(String relatedId, DraftType draftType) {
    draftFileJDBIDao.deleteDraftFiles(relatedId, draftType);
  }

  @Override
  public void addDraftFile(UploadResult uploadResult, String relatedId, DraftType draftType) {
    draftFileJDBIDao.insertDraftFile(uploadResult.getId(),
        uploadResult.getFilename(),
        uploadResult.getBucket(),
        uploadResult.getFolder(),
        uploadResult.getSize(),
        relatedId,
        draftType);
  }

  @Override
  public void deleteDraftFile(String fileId, String relatedId, DraftType draftType) {
    draftFileJDBIDao.deleteDraftFile(fileId, relatedId, draftType);
  }

  @Override
  public void deleteAllDraftFiles() {
    draftFileJDBIDao.truncateTable();
  }

}
