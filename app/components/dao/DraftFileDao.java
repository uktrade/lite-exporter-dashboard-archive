package components.dao;

import components.upload.UploadResult;
import models.Attachment;
import models.enums.DraftType;

import java.util.List;

public interface DraftFileDao {

  List<Attachment> getAttachments(String relatedId, DraftType draftType);

  void deleteDraftFiles(String relatedId, DraftType draftType);

  void addDraftFile(UploadResult uploadResult, String relatedId, DraftType draftType);

  void deleteDraftFile(String fileId, String relatedId, DraftType draftType);

  void deleteAllDraftFiles();

}
