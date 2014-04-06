import sbt._
import Keys._

object ProjectBuild extends Build {

    override lazy val settings = super.settings ++
        Seq(scalaVersion := "2.10.4", resolvers := Seq())

    val appDependencies = Seq(
        "com.typesafe" % "config" % "1.2.0",
        "org.eclipse.jgit" % "org.eclipse.jgit" % "3.3.1.201403241930-r",
        "eu.henkelmann" % "actuarius_2.10.0" % "0.2.6",
        "org.fusesource.scalate" %% "scalate-wikitext" % "1.6.1",
        "org.fusesource.scalate" %% "scalate-page" % "1.6.1"
        )

    val appResolvers = Seq(
        "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
        )

    lazy val root = Project(
        id = "garbanzo",
        base = file("."),
        settings = Project.defaultSettings ++ Seq(
                libraryDependencies ++= appDependencies,
                resolvers ++= appResolvers)
    )
}
