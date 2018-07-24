package components.service;

import models.view.ApplicationItemView;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ApplicationItemViewService {

  CompletionStage<List<ApplicationItemView>> getApplicationItemViews(String userId);

}

