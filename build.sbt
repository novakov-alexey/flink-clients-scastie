Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "io.github.novakov-alexey"
ThisBuild / scalaVersion := "3.3.4"

lazy val flinkVersion = System.getProperty("flinkVersion", "1.18.1")

def excludeJars(cp: Classpath) = cp filter { f =>
  Set(
    "scala-asm-.+-scala-1\\.jar",
    "interface-.+\\.jar",
    "jline-terminal-.+\\.jar",
    "jline-reader-.+\\.jar",
    "jline-.+\\.jar",
    "scala-compiler-.+\\.jar",
    "scala3-compiler_.+\\.jar",
    "flink-shaded-zookeeper-.+\\.jar",
    "flink-shaded-jackson-.+\\.jar",
    "flink-shaded-netty-.+\\.jar",
    "annotations-.+\\.jar"
  ).exists(p => f.data.getName.matches(p))
}

lazy val root = (project in file(".")).settings(
  name := "flink-clients-scastie",
  libraryDependencies ++= Seq(
    "org.apache.flink" % "flink-clients" % flinkVersion
  ),
  assemblyPackageScala / assembleArtifact := false,
  assembly / assemblyExcludedJars := {
    val cp = (assembly / fullClasspath).value
    excludeJars(cp)
  },
  description := "Scala bundle for Apache Flink flink-clients module",
  licenses := Seq(
    "APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  ),
  homepage := Some(
    url("https://github.com/novakov-alexey/flink-clients-scastie")
  ),
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
  publishMavenStyle := true,
  publishTo := sonatypePublishToBundle.value,
  pgpPassphrase := scala.util.Properties
    .propOrNone("gpg.passphrase")
    .map(_.toCharArray),
  git.useGitDescribe := true,
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/novakov-alexey/flink-clients-scastie"),
      "scm:git@github.com:novakov-alexey/flink-clients-scastie.git"
    )
  ),
  developers := List(
    Developer(
      id = "novakov-alexey",
      name = "Alexey Novakov",
      email = "novakov.alex@gmail.com",
      url = url("https://novakov-alexey.github.io/")
    )
  ),
  releaseProcess := Seq.empty[ReleaseStep],
  releaseProcess ++= Seq[ReleaseStep](
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease")
  )
)

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true
