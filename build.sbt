name := "lite-exporter-dashboard"

version := scala.util.Properties.envOrElse("BUILD_VERSION", formatDateAsVersion() + "-SNAPSHOT")

def formatDateAsVersion(): String = {
  val sdf = new java.text.SimpleDateFormat("YYYYMMdd.HHmmss")
  sdf.setTimeZone(new java.util.SimpleTimeZone(java.util.SimpleTimeZone.UTC_TIME, "UTC"))
  sdf.format(new java.util.Date())
}

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .dependsOn(`zzz-common`)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "buildinfo"
  )

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  filters,
  "org.flywaydb" %% "flyway-play" % "3.2.0",
  "org.xerial" % "sqlite-jdbc" % "3.19.3",
  "org.jdbi" % "jdbi" % "2.78",
  "com.rabbitmq" % "amqp-client" % "4.2.0"
)

libraryDependencies += "uk.gov.bis.lite" % "lite-permissions-service-api" % "1.3"
libraryDependencies += "uk.gov.bis.lite" % "lite-customer-service-api" % "1.1"
libraryDependencies += "uk.gov.bis.lite" % "lite-ogel-service-api" % "1.0"
libraryDependencies += "uk.gov.bis.lite" % "lite-spire-relay-api" % "1.4-SNAPSHOT"
libraryDependencies += "uk.gov.bis.lite" % "lite-user-service-api" % "1.0"
// We need this dependency to use JDBI @BindIn annotation
libraryDependencies += "org.antlr" % "stringtemplate" % "3.2.1"

libraryDependencies += "au.com.dius" % "pact-jvm-consumer-junit_2.11" % "3.3.10" % "test"
libraryDependencies += "au.com.dius" % "pact-jvm-provider-junit_2.11" % "3.3.10" % "test"
libraryDependencies += "org.assertj" % "assertj-core" % "3.5.2" % "test"

libraryDependencies += "org.apache.commons" % "commons-collections4" % "4.1"

resolvers += "Lite Lib Releases " at "http://nexus.mgmt.licensing.service.trade.gov.uk.test/repository/maven-releases/"
resolvers += "Snapshots " at "http://nexus.mgmt.licensing.service.trade.gov.uk.test/repository/maven-snapshots/"

// Contains all files and libraries shared across other projects
lazy val `zzz-common` = project.in(file("subprojects/lite-play-common")).enablePlugins(PlayJava)

//Add build time and git commit to the build info object

buildInfoKeys ++= Seq[BuildInfoKey](
  BuildInfoKey.action("gitCommit") {
    try {
      "git rev-parse HEAD".!!.trim
    } catch {
      case e: Throwable => "unknown (" + e.getMessage + ")"
    }
  }
)

buildInfoOptions += BuildInfoOption.BuildTime
buildInfoOptions += BuildInfoOption.ToJson
PlayKeys.externalizeResources := false
