package components.service;

import java.util.List;
import models.AppData;
import models.ReadData;
import models.view.MessageView;

public interface MessageViewService {

  List<MessageView> getMessageViews(AppData appData, ReadData readData);

}
