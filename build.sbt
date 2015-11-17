version       := "0.1"

scalaVersion  := "2.11.7"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

libraryDependencies ++= {
  val akkaVersion = "2.3.14"
  val sprayVersion = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"      % sprayVersion,
    "io.spray"            %%  "spray-routing"  % sprayVersion,
    "io.spray"            %%  "spray-json"     % sprayVersion,
    "io.spray"            %%  "spray-testkit"  % sprayVersion  % "test",
    "com.typesafe.akka"   %%  "akka-actor"     % akkaVersion,
    "com.typesafe.akka"   %%  "akka-testkit"   % akkaVersion   % "test",
    "org.specs2"          %%  "specs2-core"    % "2.3.11"      % "test",
    "com.netaporter"      %%  "pre-canned"     % "0.0.7"       % "test",
    "com.typesafe.slick"  %%  "slick"          % "3.1.0",
    "org.slf4j"            %  "slf4j-nop"      % "1.7.10",
    "org.slf4j"            %  "slf4j-api"      % "1.7.10",
    "org.scalatest"        %  "scalatest_2.11" % "2.2.1" % "test",
    "com.typesafe"         %  "config"         % "1.2.1",
    "org.postgresql"       %  "postgresql"     % "9.4-1203-jdbc42",
    "com.gettyimages"     %%  "spray-swagger"  % "0.5.0",
    "org.webjars"          %  "swagger-ui"     % "2.0.12"
  )
}

fork in Test := false

parallelExecution in Test := false

Revolver.settings : Seq[sbt.Def.Setting[_]]

fork in run := true

mainClass in Runtime := Some("web.Web")

test in assembly := {} // Do not run tests when building the assembly
