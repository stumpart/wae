import AssemblyKeys._

assemblySettings

name := "LascoScript"

version := "1.0"

scalaVersion := "2.11.5"

jarName in assembly := "lascoscript.jar"



val excludeJBossNetty = ExclusionRule(organization = "org.jboss.netty")
val excludeMortbayJetty = ExclusionRule(organization = "org.eclipse.jetty", artifact = "jetty-server")
val excludeAsm = ExclusionRule(organization = "org.ow2.asm")
val excludeCommonsLogging = ExclusionRule(organization = "commons-logging")
val excludeSLF4J = ExclusionRule(organization = "org.slf4j")
val excludeOldAsm = ExclusionRule(organization = "asm")
val excludeServletApi = ExclusionRule(organization = "javax.servlet", artifact = "servlet-api")


libraryDependencies += "org.apache.spark" %% "spark-core" % "1.2.0" excludeAll(
 excludeServletApi, excludeMortbayJetty
)

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.1.0"

libraryDependencies += "org.apache.spark" %% "spark-yarn_2.10" % "1.0.2"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.5.1" excludeAll(
  excludeJBossNetty, excludeMortbayJetty, excludeAsm, excludeCommonsLogging, excludeSLF4J, excludeOldAsm, excludeServletApi
)

libraryDependencies += "org.mortbay.jetty" % "servlet-api" % "3.0.20100224"

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "8.1.16.v20140903"

unmanagedJars in Compile ++= {
  val base = baseDirectory.value
  val baseDirectories = (base / "lib") +++ (base)
  val customJars = (baseDirectories ** "*.jar")
  customJars.classpath
}

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

