import AssemblyKeys._

seq(assemblySettings: _*)

name := "TuningLab"

version := "0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation" )

mainClass in assembly := Some("tuninglab.Main")

jarName in assembly := "tuninglab.jar"

libraryDependencies ++= Seq(
)

libraryDependencies ++= Seq(
   "org.scalatest" %% "scalatest" % "2.0.+" % "test",
   "junit" % "junit" % "4.+" % "test"
   )

initialCommands in console := """
import tuninglab._
"""

sourceGenerators in Compile <+= 
 (sourceManaged in Compile, version, name, jarName in assembly) map {
  (dir, version, projectname, jarexe) =>
  val file = dir / "dummy" / "MetaInfo.scala"
  IO.write(file,
  """package dummy
    |object MetaInfo { 
    |  val version="%s"
    |  val project="%s"
    |  val jarbasename="%s"
    |}
    |""".stripMargin.format(version, projectname, jarexe.split("[.]").head) )
  Seq(file)
}
