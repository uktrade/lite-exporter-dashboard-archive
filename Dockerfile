FROM openjdk:8-jre

ENV SERVICE_DIR /opt/admin-prototype
ENV ARTEFACT_NAME lite-admin-prototype-1.0

RUN mkdir -p $SERVICE_DIR

COPY ./target/universal/${ARTEFACT_NAME}.zip $SERVICE_DIR

WORKDIR $SERVICE_DIR

RUN unzip ${ARTEFACT_NAME}.zip

CMD ${ARTEFACT_NAME}/bin/lite-admin-prototype -Dplay.crypto.secret=abcdefghijk
