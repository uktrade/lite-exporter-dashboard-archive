package components.service;

import components.upload.UploadFile;

import java.util.List;

public interface RfiResponseService {
  void insertRfiResponse(String rfiId, String message, List<UploadFile> files);
}
