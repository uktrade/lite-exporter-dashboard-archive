package components.dao;

import models.RfiResponse;

import java.util.List;

public interface RfiResponseDao {
  List<RfiResponse> getRfiResponses();

  List<RfiResponse> getRfiResponses(String rfiId);

  void insertRfiResponse(RfiResponse rfiResponse);

  void deleteAllRfiResponses();
}
