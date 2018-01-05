package components.dao;

import models.File;
import models.enums.DraftType;

import java.util.List;

public interface DraftFileDao {

  List<File> getDraftFiles(String relatedId, DraftType draftType);

  void deleteDraftFiles(String relatedId, DraftType draftType);

  String addDraftFile(String filename, String url, String relatedId, DraftType draftType);

  void deleteDraftFile(String fileId, String relatedId, DraftType draftType);

  void deleteAllDraftFiles();

}
