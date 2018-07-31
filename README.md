# LITE Exporter Dashboard

A frontend application allowing exporters to see a list of their Open General Export Licences (OGELs).

## Getting started

* Download everything:
  * `git clone git@github.com:uktrade/lite-exporter-dashboard.git`
  * `cd lite-exporter-dashboard` 
  * `git submodule init`
  * `git submodule update`
* Start a local Redis: `docker run -p 6379:6379 --name my-redis -d redis:latest`
* Start a local Postgres: `docker run --name my-postgres -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres`
* Set up your local config file:
  * `cp conf/sample-application.conf conf/application.conf`
  * In `application.conf`, replace `ENTER_USERNAME_HERE` and `ENTER_PASSWORD_HERE` values with their corresponding usernames and passwords.
  * If your local Redis and Postgres are not running with default options, edit the `db` and `redis` sections of `application.conf`.
* Run the application: `sbt run`
* Go to the index page (e.g. `http://localhost:9000`)

## Dependency configuration

The exporter dashboard integrates with several other LITE services. Connection details 
(including usernames/passwords) are defined in `application.conf`.

* [lite-country-service](https://github.com/uktrade/lite-country-service) - country data for typeahead
* [lite-customer-service](https://github.com/uktrade/lite-customer-service) - company and site address information
* [lite-ogel-service](https://github.com/uktrade/lite-ogel-service) - OGEL details
* [lite-permissions-service](https://github.com/uktrade/lite-permissions-service) - OGEL licence details
* [lite-user-service](https://github.com/uktrade/lite-user-service) - user account and permission data

### Common submodule

The exporter dashboard also makes use of the [lite-play-common](https://github.com/uktrade/lite-play-common) base project, which provides various shared functionality such as base templates (`govukTemplate.template.scala`) and service clients (`CountryServiceClient`).

See [Git documentation on submodules](https://git-scm.com/book/en/v2/Git-Tools-Submodules) for details on how to manage changes
to a submodule.

### SAML IdP

The exporter dashboard requires a SAML Identity Provider to be configured to authenticate users. The sample configuration in `sample-application.conf` connects to a mock SAML service.

## User journey

When a user navigates to the landing page `localhost:9000`, they're asked to login with their SAML credentials. Subsequently, they see a list of their `Open General Export Licences`, which can be sorted by `Reference`, `Licensee (site)`, `Registration date`, `Status` and `Last updated`. When clicking on a licence reference, more details about the licence are displayed.

The default value for `ogelOnly` is set to `true` in `sample-application.conf`. If `ogelOnly` is set to `false` in `application.conf`, additional functionality can be accessed. 
