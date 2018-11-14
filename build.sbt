name := "ScalaWordCount" 
version := "1.0" 
scalaVersion := "2.11.7" 
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.0.1"

publishTo <<= version { (v: String) =>
  if (v.trim.endsWith("-SNAPSHOT"))
    Some(Resolver.file("Snapshots", file("http://10.65.200.207/artifactory/scalatest/snapshots/")))
  else
    Some(Resolver.file("Releases", file("http://10.65.200.207/artifactory/scalatest/releases/")))
}
