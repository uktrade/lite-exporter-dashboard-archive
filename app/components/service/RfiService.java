package components.service;

import models.Rfi;

import java.util.List;

public interface RfiService {

  List<Rfi> getOpenRfiList(List<String> appIds);

}
