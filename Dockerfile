FROM openjdk:8-jre

ARG NEXUS_BASE_URL=http://nexus.mgmt.licensing.service.trade.gov.uk.test/repository
ARG NEXUS_REPO=lite-builds-raw
ARG CRYPTO_SECRET=abcdefghijk
ARG BUILD_VERSION

ENV ARTEFACT_NAME lite-exporter-dashboard-$BUILD_VERSION
ENV CONFIG_FILE /conf/exporter-dashboard-config.conf
ENV CRYPTO_SECRET ${CRYPTO_SECRET}

LABEL uk.gov.bis.lite.version=$BUILD_VERSION

WORKDIR /opt/exporter-dashboard

ADD $NEXUS_BASE_URL/$NEXUS_REPO/lite-exporter-dashboard/lite-exporter-dashboard/$BUILD_VERSION/$ARTEFACT_NAME.zip $ARTEFACT_NAME.zip
RUN unzip ${ARTEFACT_NAME}.zip

EXPOSE 9000

CMD $ARTEFACT_NAME/bin/lite-exporter-dashboard -Dplay.crypto.secret=$CRYPTO_SECRET -Dconfig.file=$CONFIG_FILE