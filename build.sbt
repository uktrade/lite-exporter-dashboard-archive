name := "lite-admin-prototype"

version := "1.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .dependsOn(`zzz-common`)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "buildinfo"
  )

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( javaJdbc ,  cache , javaWs )

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

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

//resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"