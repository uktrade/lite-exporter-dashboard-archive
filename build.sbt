import sbt.Keys.libraryDependencies

name := "lite-exporter-dashboard"

version := scala.util.Properties.envOrElse("BUILD_VERSION", "1.0-SNAPSHOT")

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .dependsOn(`zzz-common` % "test->test;compile->compile")
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "buildinfo"
  )

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  ehcache,
  javaWs,
  filters,
  "net.logstash.logback" % "logstash-logback-encoder" % "5.0",
  "org.flywaydb" %% "flyway-play" % "4.0.0",
  "org.postgresql" % "postgresql" % "42.1.4",
  "org.jdbi" % "jdbi" % "2.78",
  "org.apache.commons" % "commons-collections4" % "4.1",
  "com.amazonaws" % "aws-java-sdk-sqs" % "1.11.264",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.11.257",
  // We need this dependency to use JDBI @BindIn annotation
  "org.antlr" % "stringtemplate" % "3.2.1",
  "org.jsoup" % "jsoup" % "1.11.2"
)

libraryDependencies += "uk.gov.bis.lite" % "lite-permissions-service-api" % "1.4"
libraryDependencies += "uk.gov.bis.lite" % "lite-customer-service-api" % "1.1"
libraryDependencies += "uk.gov.bis.lite" % "lite-ogel-service-api" % "1.2"
libraryDependencies += "uk.gov.bis.lite" % "lite-spire-relay-api" % "1.4-SNAPSHOT"
libraryDependencies += "uk.gov.bis.lite" % "lite-user-service-api" % "1.2"
libraryDependencies += "uk.gov.bis.lite" % "lite-licence-application-api" % "1.9.1"

libraryDependencies += "au.com.dius" % "pact-jvm-consumer-junit_2.11" % "3.5.13" % "test"
libraryDependencies += "au.com.dius" % "pact-jvm-provider-junit_2.11" % "3.5.13" % "test"

libraryDependencies += "org.assertj" % "assertj-core" % "3.5.2" % "test"
libraryDependencies += "ru.yandex.qatools.embed" % "postgresql-embedded" % "2.6" % "test"

resolvers += "Lite Lib Releases " at "https://nexus.ci.uktrade.io/repository/maven-releases/"
resolvers += "Snapshots " at "https://nexus.ci.uktrade.io/repository/maven-snapshots/"

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
