package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import models.enums.DocumentType;
import org.hibernate.validator.constraints.NotBlank;

public class Document {

  @NotBlank
  private String id;

  @NotNull
  private DocumentType documentType;

  @NotBlank
  private String licenceRef;

  @NotBlank
  private String filename;

  @NotBlank
  private String url;

  public Document(@JsonProperty("id") String id,
                  @JsonProperty("documentType") DocumentType documentType,
                  @JsonProperty("licenceRef") String licenceRef,
                  @JsonProperty("filename") String filename,
                  @JsonProperty("url") String url) {
    this.id = id;
    this.documentType = documentType;
    this.licenceRef = licenceRef;
    this.filename = filename;
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public DocumentType getDocumentType() {
    return documentType;
  }

  public String getLicenceRef() {
    return licenceRef;
  }

  public String getFilename() {
    return filename;
  }

  public String getUrl() {
    return url;
  }

}