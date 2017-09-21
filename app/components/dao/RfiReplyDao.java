package components.dao;

import uk.gov.bis.lite.exporterdashboard.api.RfiReply;

import java.util.List;

public interface RfiReplyDao {

  List<RfiReply> getRfiReplies(List<String> rfiIds);

  RfiReply getRfiReply(String rfiId);

  void insertRfiReply(RfiReply rfiReply);

  void deleteAllRfiReplies();

  void deleteRfiRepliesByRfiIds(List<String> rfiIds);

}
