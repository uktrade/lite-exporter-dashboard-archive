package components.dao.impl;

import com.google.inject.Inject;
import components.dao.DraftFileDao;
import components.dao.jdbi.DraftFileJDBIDao;
import components.util.RandomIdUtil;
import models.File;
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
  public List<File> getDraftFiles(String relatedId, DraftType draftType) {
    return draftFileJDBIDao.getDraftFiles(relatedId, draftType).stream()
        .map(draftFile -> new File(draftFile.getId(), draftFile.getFilename(), draftFile.getUrl()))
        .collect(Collectors.toList());
  }

  @Override
  public void deleteDraftFiles(String relatedId, DraftType draftType) {
    draftFileJDBIDao.deleteDraftFiles(relatedId, draftType);
  }

  @Override
  public String addDraftFile(String filename, String url, String relatedId, DraftType draftType) {
    String fileId = RandomIdUtil.fileId();
    draftFileJDBIDao.insertDraftFile(fileId, filename, url, relatedId, draftType);
    return fileId;
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
