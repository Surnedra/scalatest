import sbt.Credentials
import sbt.Keys.{credentials, publishTo}

lazy val commonSettings = Seq(
  fork := true,
  name := "sample-project",
  version := "1.1.5-SNAPSHOT",
  scalaVersion := "2.11.12",
  outputStrategy := Some(StdoutOutput),
  // scalastyle
  scalastyleFailOnError := true,
  // sbt-scoverage config
  coverageExcludedPackages := "wordcount.WordCount;wordcount.Setup",
  coverageMinimum := 100,
  coverageFailOnMinimum := true,
  //Increase the amount of memory for testing
  fork in Test := true,
  parallelExecution in Test := false,
  javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")
)

lazy val wordCount = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    //Configurations for assembly plugin
    Seq(
      assemblyJarName in assembly := name.value + "_" + scalaVersion.value + "-" + version.value + ".jar",
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", xs @ _*) => MergeStrategy.discard
        case _ => MergeStrategy.last
      }
    ),
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "2.4.0" % "provided",
      "org.apache.spark" %% "spark-sql" % "2.4.0" % "provided",
      "org.scalactic" %% "scalactic" % "3.0.5",
      "com.ibm.stocator" % "stocator" % "1.0.24",
      "com.typesafe" % "config" % "1.3.3",
      // testing libraries
      "org.scalamock" %% "scalamock" % "4.1.0" % "test",
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "org.apache.spark" %% "spark-hive" % "2.4.0" % "test",
      "com.holdenkarau" %% "spark-testing-base" % "2.3.1_0.10.0" % "test"
    ),

    resolvers ++= Seq(
      "Artima Maven Repository" at "http://repo.artima.com/releases"
    ),

    credentials += Credentials("Artifactory Realm", "10.65.200.207", "admin", "password"),

    publishTo in Global := {
      if (isSnapshot.value) {
        Some(snapshotRepository)
      }
      else {
        Some(releaseRepository)
      }
    }
  )

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)

lazy val releaseRepository =
  "Artifactory Realm" at
    "http://10.65.200.207/artifactory/scalatest/"

lazy val snapshotRepository =
  "Artifactory Realm" at
    "http://10.65.200.207/artifactory/scalatest;build.timestamp=" + new java.util.Date().getTime

