package components.service;

import components.upload.UploadFile;

import java.util.List;

public interface AmendmentService {

  void insertAmendment(String sentBy, String appId, String message, List<UploadFile> files);

}
