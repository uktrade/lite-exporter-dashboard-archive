package components.util;

import java.util.List;
import java.util.stream.Collectors;
import models.File;
import uk.gov.bis.lite.exporterdashboard.api.DashboardDocument;

public class MessageUtil {

  public static List<DashboardDocument> getDashboardDocuments(List<File> files) {
    return files.stream()
        .map(file -> {
          DashboardDocument dashboardDocument = new DashboardDocument();
          dashboardDocument.setId(file.getId());
          dashboardDocument.setFilename(file.getFilename());
          dashboardDocument.setUrl(file.getUrl());
          return dashboardDocument;
        }).collect(Collectors.toList());
  }

}
