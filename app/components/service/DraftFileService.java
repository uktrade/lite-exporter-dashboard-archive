package components.service;

import models.enums.DraftType;

public interface DraftFileService {
  void deleteDraftFile(String fileId, String relatedId, DraftType draftType);
}
