import AssemblyKeys._

seq(assemblySettings: _*)

name := "TuningLab"

version := "0.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation" )

mainClass in assembly := Some("tuninglab.Main")

jarName in assembly := "tuninglab.jar"

libraryDependencies ++= Seq(
   "fr.janalyse" %% "primes" % "1.0.9",
   "fr.janalyse" %% "janalyse-ssh" % "0.9.12",
   "fr.janalyse" %% "janalyse-jmx" % "0.6.3",
   "fr.janalyse" %% "unittools"    % "0.2.3",
   "com.jsuereth" %% "scala-arm"   % "1.3",
   "net.databinder.dispatch" %% "dispatch-core" % "0.11.+",
   "com.github.scala-incubator.io" %% "scala-io-core"      % "0.4.2",
   "com.github.scala-incubator.io" %% "scala-io-file"      % "0.4.2"
)

libraryDependencies ++= Seq(
   "org.scalatest" %% "scalatest" % "2.1.0" % "test",
   "junit" % "junit" % "4.11" % "test"
   )

resolvers ++= Seq(
  "JAnalyse Repository" at "http://www.janalyse.fr/repository/"
)

initialCommands in console := """
import scala.reflect.BeanProperty
import fr.janalyse.ssh._
import fr.janalyse.jmx._
import concurrent._
import ExecutionContext.Implicits.global
import duration._
import tuninglab._
import scalax.file.Path
import scalax.io._
import dispatch._
"""

sourceGenerators in Compile <+= 
 (sourceManaged in Compile, version, name, jarName in assembly) map {
  (dir, version, projectname, jarexe) =>
  val file = dir / "tuninglab" / "MetaInfo.scala"
  IO.write(file,
  """package tuninglab
    |object MetaInfo { 
    |  val version="%s"
    |  val project="%s"
    |  val jarbasename="%s"
    |}
    |""".stripMargin.format(version, projectname, jarexe.split("[.]").head) )
  Seq(file)
}
