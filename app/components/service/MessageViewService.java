package components.service;

import models.view.MessageView;

import java.util.List;

public interface MessageViewService {

  List<MessageView> getMessageViews(String appId);

}
