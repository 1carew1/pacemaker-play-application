name := "pacemakerplay_web"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "net.sf.flexjson" % "flexjson" % "3.1",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "mysql" % "mysql-connector-java" % "5.1.18"
)     

play.Project.playJavaSettings
 