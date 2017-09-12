package components.util;

import components.upload.UploadFile;
import models.File;
import play.data.Form;
import play.mvc.Http;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUtil {

  public static String getReadableFileSize(String path) {
    long size = new java.io.File(path).length();
    if (size <= 0) {
      return "0";
    }
    String[] units = new String[]{" bytes", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
  }

  /**
   * Null check on 'uploadFile' is workaround for bug https://github.com/playframework/playframework/issues/6203
   */
  public static List<UploadFile> getUploadFiles(Http.Request request) {
    Http.MultipartFormData<UploadFile> body = request.body().asMultipartFormData();
    return body.getFiles().stream()
        .map(Http.MultipartFormData.FilePart::getFile)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public static List<File> toFiles(List<UploadFile> uploadFiles) {
    return uploadFiles.stream()
        .map(uploadFile -> new File(UUID.randomUUID().toString(), uploadFile.getOriginalFilename(), uploadFile.getDestinationPath(), System.currentTimeMillis()))
        .collect(Collectors.toList());
  }

  public static void processErrors(Form form, List<UploadFile> uploadFiles) {
    uploadFiles.stream()
        .map(UploadFile::getProcessErrorInfo)
        .filter(Objects::nonNull)
        .forEach(form::reject);
  }

}
