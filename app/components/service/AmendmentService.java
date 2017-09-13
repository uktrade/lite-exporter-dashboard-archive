package components.service;

import components.upload.UploadFile;

import java.util.List;

public interface AmendmentService {

  void insertAmendment(String appId, String message, List<UploadFile> files);

}
