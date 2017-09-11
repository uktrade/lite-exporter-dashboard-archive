package components.dao;

import com.google.inject.Inject;
import components.exceptions.DatabaseException;
import components.util.JsonUtil;
import models.DraftRfiResponse;
import models.File;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DraftRfiResponseDaoImpl implements DraftRfiResponseDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(DraftRfiResponseDaoImpl.class);

  private final DBI dbi;

  @Inject
  public DraftRfiResponseDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public DraftRfiResponse getDraftRfiResponse(String rfiId) {
    try (final Handle handle = dbi.open()) {
      DraftRfiResponseJDBIDao draftRfiResponseJDBIDao = handle.attach(DraftRfiResponseJDBIDao.class);
      return draftRfiResponseJDBIDao.getDraftRfiResponse(rfiId);
    }
  }

  @Override
  public void deleteDraftRfiResponse(String rfiId) {
    try (final Handle handle = dbi.open()) {
      DraftRfiResponseJDBIDao draftRfiResponseJDBIDao = handle.attach(DraftRfiResponseJDBIDao.class);
      draftRfiResponseJDBIDao.delete(rfiId);
    }
  }

  @Override
  public synchronized void addFile(String rfiId, File file) {
    try (final Handle handle = dbi.open()) {
      handle.useTransaction((conn, status) -> {
        DraftRfiResponseJDBIDao draftRfiResponseJDBIDao = handle.attach(DraftRfiResponseJDBIDao.class);
        DraftRfiResponse existingDraftRfiResponse = draftRfiResponseJDBIDao.getDraftRfiResponse(rfiId);
        List<File> attachments = new ArrayList<>();
        if (existingDraftRfiResponse != null) {
          attachments.addAll(existingDraftRfiResponse.getAttachments());
        }
        attachments.add(file);
        String attachmentsJson = JsonUtil.convertFilesToJson(attachments);
        draftRfiResponseJDBIDao.insertDraftRfiResponse(rfiId, attachmentsJson);
      });
    }
  }

  @Override
  public void deleteFile(String rfiId, String fileId) {
    try (final Handle handle = dbi.open()) {
      DraftRfiResponseJDBIDao draftRfiResponseJDBIDao = handle.attach(DraftRfiResponseJDBIDao.class);
      handle.useTransaction((conn, status) -> {
        DraftRfiResponse existingDraftRfiResponse = draftRfiResponseJDBIDao.getDraftRfiResponse(rfiId);
        if (existingDraftRfiResponse == null) {
          String errorMessage = String.format("Unable to delete file with rfiId %s and fileId %s since there is no draftRfiResponse", rfiId, fileId);
          throw new DatabaseException(errorMessage);
        } else {
          List<File> files = existingDraftRfiResponse.getAttachments().stream()
              .filter(file -> !file.getFileId().equals(fileId))
              .collect(Collectors.toList());
          if (files.size() == existingDraftRfiResponse.getAttachments().size()) {
            String errorMessage = String.format("Unable to delete file with rfiId %s and fileId %s since there is no such file", rfiId, fileId);
            throw new DatabaseException(errorMessage);
          } else {
            draftRfiResponseJDBIDao.insertDraftRfiResponse(rfiId, JsonUtil.convertFilesToJson(files));
          }
        }
      });
    }
  }

  @Override
  public void deleteAllDraftRfiResponses() {
    try (final Handle handle = dbi.open()) {
      DraftRfiResponseJDBIDao draftRfiResponseJDBIDao = handle.attach(DraftRfiResponseJDBIDao.class);
      draftRfiResponseJDBIDao.truncateTable();
    }
  }

}
