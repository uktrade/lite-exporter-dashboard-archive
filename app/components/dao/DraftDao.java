package components.dao;

import uk.gov.bis.lite.exporterdashboard.api.File;
import models.enums.DraftType;

import java.util.List;

public interface DraftDao {

  List<File> getDraftAttachments(String relatedId, DraftType draftType);

  void deleteDraft(String relatedId, DraftType draftType);

  void addFile(String relatedId, File file, DraftType draftType);

  void deleteFile(String rfiId, String fileId, DraftType draftType);

  void deleteAllDrafts();

}
