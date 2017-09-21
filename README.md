# LITE Exporter Dashboard

A frontend application allowing exporters to see the status of their licence applications

## Getting started

* Download everything - `git clone <url>`, `cd lite-exporter-dashboard`, `git submodule init`, `git submodule update`
* Copy `sample-application.conf` to `application.conf`
* Run the application - `sbt run`
* Go to the index page (e.g. `http://localhost:9000`)

## Publishing api to maven

* The package `uk.gov.bis.lite.exporterdashboard.api` contains the objects that are sent to RabbitMQ. They can be published with gradle via
`gradle publishBuildPublicationToReleasesRepository` (release) or `gradle publishBuildPublicationToSnapshotsRepository` (snapshot). 