package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.enums.DocumentType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class OutcomeDocument {

  @NotBlank
  private final String id;

  @NotNull
  private final DocumentType documentType;

  @NotBlank
  private final String licenceRef;

  @NotBlank
  private final String filename;

  @NotBlank
  private final String url;

  public OutcomeDocument(@JsonProperty("id") String id,
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
