package components.util;

import components.upload.UploadResult;
import play.data.Form;

import java.text.DecimalFormat;
import java.util.List;

public class FileUtil {

  private static final String[] UNITS = new String[]{" bytes", "KB", "MB", "GB", "TB"};

  public static String getReadableFileSize(long size) {
    if (size <= 0) {
      return "0";
    } else {
      int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
      return new DecimalFormat("#,##0").format(size / Math.pow(1024, digitGroups)) + UNITS[digitGroups];
    }
  }

  public static void addUploadErrorsToForm(Form form, List<UploadResult> uploadResults) {
    uploadResults.stream()
        .filter(uploadResult -> !uploadResult.isValid())
        .forEach(uploadResult -> {
          form.reject("fileupload",
              "Error for file " + uploadResult.getFilename() + ": " + uploadResult.getError());
        });
  }
}
