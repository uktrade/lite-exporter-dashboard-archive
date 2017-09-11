CREATE TABLE STATUS_UPDATE (
  ID              INTEGER PRIMARY KEY,
  APP_ID          TEXT    NOT NULL,
  STATUS_TYPE     TEXT    NOT NULL,
  START_TIMESTAMP INTEGER NOT NULL,
  END_TIMESTAMP   INTEGER
);

CREATE TABLE RFI (
  ID                 INTEGER PRIMARY KEY,
  RFI_ID             TEXT    NOT NULL,
  APP_ID             TEXT    NOT NULL,
  STATUS             TEXT    NOT NULL,
  RECEIVED_TIMESTAMP INTEGER NOT NULL,
  DUE_TIMESTAMP      INTEGER,
  SENT_BY            TEXT,
  MESSAGE            TEXT
);

CREATE TABLE RFI_RESPONSE (
  ID             INTEGER PRIMARY KEY,
  RFI_ID         TEXT    NOT NULL,
  SENT_BY        TEXT,
  SENT_TIMESTAMP INTEGER NOT NULL,
  MESSAGE        TEXT    NOT NULL,
  ATTACHMENTS    TEXT,
  FOREIGN KEY (RFI_ID) REFERENCES RFI (RFI_ID)
);

CREATE TABLE APPLICATION (
  ID                  INTEGER PRIMARY KEY,
  APP_ID              TEXT    NOT NULL UNIQUE,
  COMPANY_ID          TEXT    NOT NULL,
  CREATED_BY          TEXT    NOT NULL,
  CREATED_TIMESTAMP   INTEGER NOT NULL,
  SUBMITTED_TIMESTAMP INTEGER,
  DESTINATION_LIST    TEXT,
  APPLICANT_REFERENCE TEXT    NOT NULL,
  CASE_REFERENCE      TEXT,
  CASE_OFFICER_ID     TEXT
);

CREATE TABLE AMENDMENT (
  ID             INTEGER PRIMARY KEY,
  AMENDMENT_ID   TEXT    NOT NULL UNIQUE,
  APP_ID         TEXT    NOT NULL,
  SENT_TIMESTAMP INTEGER NOT NULL,
  SENT_BY        TEXT    NOT NULL,
  MESSAGE        TEXT    NOT NULL,
  ATTACHMENTS    TEXT
);

CREATE TABLE WITHDRAWAL_REQUEST (
  ID                    INTEGER PRIMARY KEY,
  WITHDRAWAL_REQUEST_ID TEXT    NOT NULL UNIQUE,
  APP_ID                TEXT    NOT NULL,
  SENT_TIMESTAMP        INTEGER NOT NULL,
  SENT_BY               TEXT    NOT NULL,
  MESSAGE               TEXT    NOT NULL,
  ATTACHMENTS           TEXT,
  REJECTED_BY           TEXT,
  REJECTED_TIMESTAMP    INTEGER,
  REJECTED_MESSAGE      TEXT
);

CREATE TABLE DRAFT_RFI_RESPONSE (
  ID          INTEGER PRIMARY KEY,
  RFI_ID      TEXT NOT NULL UNIQUE ON CONFLICT REPLACE,
  ATTACHMENTS TEXT
);
