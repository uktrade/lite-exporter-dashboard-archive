package components.dao;

import models.RfiReply;

import java.util.List;

public interface RfiReplyDao {

  List<RfiReply> getRfiReplies(List<String> rfiIds);

  RfiReply getRfiReply(String rfiId);

  void insertRfiReply(RfiReply rfiReply);

  void deleteAllRfiReplies();

  void deleteRfiRepliesByRfiId(String rfiId);

}
