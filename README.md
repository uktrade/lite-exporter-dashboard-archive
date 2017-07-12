# lite-admin-prototype

A testbed for prototypes and mockups of case processing screens for LITE

## To build/push

In root directory (note: you need to be logged into Docker with Nexus credentials):

* `sbt dist`
* `docker build -t docker.mgmt.licensing.service.trade.gov.uk.test:80/lite-prototypes/lite-admin-prototype:latest .`
* `docker push docker.mgmt.licensing.service.trade.gov.uk.test:80/lite-prototypes/lite-admin-prototype:latest`

Then run Jenkins job `prototype-push` to deploy to the dev environment.

Note: OpenShift object definitions are available in `openshift-template.yaml`.