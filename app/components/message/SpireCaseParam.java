package components.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "caseStatusUpdate")
public class SpireCaseParam {

  private String liteApplicationId;
  private String statusCode;
  private String timestamp;

  @XmlElement
  public String getLiteApplicationId() {
    return liteApplicationId;
  }

  @XmlElement
  public String getStatusCode() {
    return statusCode;
  }

  @XmlElement
  public String getTimestamp() {
    return timestamp;
  }

  public void setLiteApplicationId(String liteApplicationId) {
    this.liteApplicationId = liteApplicationId;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

}
