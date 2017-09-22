package models;

import models.enums.DocumentType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public DocumentType getDocumentType() {
    return documentType;
  }

  public void setDocumentType(DocumentType documentType) {
    this.documentType = documentType;
  }

  public String getLicenceRef() {
    return licenceRef;
  }

  public void setLicenceRef(String licenceRef) {
    this.licenceRef = licenceRef;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}