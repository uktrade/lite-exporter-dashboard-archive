package components.dao;

public interface BacklogDao {

  void insert(long timestamp, String routingKey, String message);

  void deleteAllBacklogMessages();

}
