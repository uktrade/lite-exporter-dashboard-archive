package components.dao;

import static components.util.RandomIdUtil.draftId;

import com.google.inject.Inject;
import components.exceptions.DatabaseException;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.Draft;
import models.enums.DraftType;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import uk.gov.bis.lite.exporterdashboard.api.File;

public class DraftDaoImpl implements DraftDao {

  private final DBI dbi;

  @Inject
  public DraftDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<File> getDraftAttachments(String relatedId, DraftType draftType) {
    try (final Handle handle = dbi.open()) {
      DraftJDBIDao draftJDBIDao = handle.attach(DraftJDBIDao.class);
      Draft draft = draftJDBIDao.getDraft(relatedId, draftType);
      if (draft == null) {
        return new ArrayList<>();
      } else {
        return draft.getAttachments();
      }
    }
  }

  @Override
  public void deleteDraft(String relatedId, DraftType draftType) {
    try (final Handle handle = dbi.open()) {
      DraftJDBIDao draftJDBIDao = handle.attach(DraftJDBIDao.class);
      draftJDBIDao.deleteDraft(relatedId, draftType);
    }
  }

  @Override
  public synchronized void addFile(String relatedId, File file, DraftType draftType) {
    try (final Handle handle = dbi.open()) {
      handle.useTransaction((conn, status) -> {
        DraftJDBIDao draftJDBIDao = handle.attach(DraftJDBIDao.class);
        Draft existingDraft = draftJDBIDao.getDraft(relatedId, draftType);
        List<File> attachments = new ArrayList<>();
        if (existingDraft != null) {
          attachments.addAll(existingDraft.getAttachments());
        }
        attachments.add(file);
        String attachmentsJson = JsonUtil.convertListToJson(attachments);
        draftJDBIDao.deleteDraft(relatedId, draftType);
        draftJDBIDao.insertDraft(draftId(), relatedId, draftType, attachmentsJson);
      });
    }
  }

  @Override
  public void deleteFile(String relatedId, String fileId, DraftType draftType) {
    try (final Handle handle = dbi.open()) {
      DraftJDBIDao draftJDBIDao = handle.attach(DraftJDBIDao.class);
      handle.useTransaction((conn, status) -> {
        Draft existingDraft = draftJDBIDao.getDraft(relatedId, draftType);
        if (existingDraft == null) {
          String errorMessage = String.format("Unable to delete file with relatedId %s and draftType %s and fileId %s since there is no such draft.", relatedId, draftType, fileId);
          throw new DatabaseException(errorMessage);
        } else {
          List<File> files = existingDraft.getAttachments().stream()
              .filter(file -> !file.getId().equals(fileId))
              .collect(Collectors.toList());
          if (files.size() == existingDraft.getAttachments().size()) {
            String errorMessage = String.format("Unable to delete file with relatedId %s and draftType %s and fileId %s since there is no such file.", relatedId, draftType, fileId);
            throw new DatabaseException(errorMessage);
          } else {
            draftJDBIDao.deleteDraft(relatedId, draftType);
            draftJDBIDao.insertDraft(draftId(), relatedId, draftType, JsonUtil.convertListToJson(files));
          }
        }
      });
    }
  }

  @Override
  public void deleteAllDrafts() {
    try (final Handle handle = dbi.open()) {
      DraftJDBIDao draftJDBIDao = handle.attach(DraftJDBIDao.class);
      draftJDBIDao.truncateTable();
    }
  }

}
