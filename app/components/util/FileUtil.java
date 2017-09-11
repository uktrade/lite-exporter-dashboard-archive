package components.util;

import java.text.DecimalFormat;

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

}
