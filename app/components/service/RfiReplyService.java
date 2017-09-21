package components.service;

import components.upload.UploadFile;

import java.util.List;

public interface RfiReplyService {

  void insertRfiReply(String createdByUserId, String rfiId, String message, List<UploadFile> files);

}
