package components.service;

import components.upload.UploadFile;

import java.util.List;

public interface WithdrawalRequestService {

  void insertWithdrawalRequest(String appId, String message, List<UploadFile> files);

}
