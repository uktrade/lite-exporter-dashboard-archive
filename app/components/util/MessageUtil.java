package components.util;

import java.util.List;
import java.util.stream.Collectors;
import uk.gov.bis.lite.exporterdashboard.api.File;

public class MessageUtil {

  public static List<File> getFiles(List<models.File> files) {
    return files.stream()
        .map(file -> {
          uk.gov.bis.lite.exporterdashboard.api.File messageFile = new uk.gov.bis.lite.exporterdashboard.api.File();
          messageFile.setId(file.getId());
          messageFile.setFilename(file.getFilename());
          messageFile.setUrl(file.getUrl());
          return messageFile;
        }).collect(Collectors.toList());
  }

}
