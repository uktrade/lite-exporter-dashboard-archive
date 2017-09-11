package components.dao;

import models.DraftRfiResponse;
import models.File;

public interface DraftRfiResponseDao {
  DraftRfiResponse getDraftRfiResponse(String rfiId);

  void deleteDraftRfiResponse(String rfiId);

  void addFile(String rfiId, File file);

  void deleteFile(String rfiId, String fileId);

  void deleteAllDraftRfiResponses();
}
