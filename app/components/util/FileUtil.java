package components.util;

import static components.util.RandomIdUtil.fileId;

import components.upload.UploadFile;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import play.data.Form;
import play.mvc.Http;
import uk.gov.bis.lite.exporterdashboard.api.File;

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
        .map(uploadFile -> {
          File file = new File();
          file.setId(fileId());
          file.setFilename(uploadFile.getOriginalFilename());
          file.setUrl(uploadFile.getDestinationPath());
          return file;
        }).collect(Collectors.toList());
  }

  public static void processErrors(Form form, List<UploadFile> uploadFiles) {
    uploadFiles.stream()
        .map(UploadFile::getProcessErrorInfo)
        .filter(Objects::nonNull)
        .forEach(form::reject);
  }

}
