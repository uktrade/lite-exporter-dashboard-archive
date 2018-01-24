package components.service;

public interface RfiReplyService {

  void insertRfiReply(String createdByUserId, String appId, String rfiId, String message);

}
