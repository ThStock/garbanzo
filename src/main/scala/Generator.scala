import java.io._

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.fusesource.scalate.TemplateEngine

import scala.io.Source

object Generator extends App {

  val engine = new TemplateEngine
  val outputFile = new File("index.html")
  val resources = "src/main/resources"
  val contentFolder = new File(outputFile.getParentFile, resources)
  val webPath = contentFolder.toURI

  writeToFile(toHtml(findEntries.mkString("\n")), outputFile)

  def findEntries(): List[String] = {
    def findRecursiv(file: File, filter: File => Boolean): Array[File] = {
      val files = file.listFiles
      files.filter(filter) ++ files.filter(_.isDirectory)
        .flatMap(findRecursiv(_, filter))
    }

    findRecursiv(contentFolder, _.getName == "index.md")
      .map(index => index.getParentFile.getPath.substring(resources.length + 1))
      .sorted.reverse
      .map(folder => toTemplate(folder, "/index.md"))
      .toList
  }

  def toTemplate(pathTo: String, fileName: String, map: Map[String, AnyRef] = Map.empty[String, AnyRef]): String = {
    val text = Source.fromInputStream(getClass.getResourceAsStream(pathTo + fileName)).mkString
    val template = engine.compileMoustache(text)
    val newm = Map(
      "path" -> (webPath + pathTo),
      "changeDate" -> Util.lastEdit(resources + "/" + pathTo + fileName),
      "gravatar" -> ((emails: String) => Util.gravatars(emails))
    ) ++ map
    engine.layout("", template, newm)
  }

  def toHtml(md: String): String = {
    toTemplate("", "main.mu", Map("entries" -> fromMdtoHtml(md)))
  }

  def fromMdtoHtml(input: String): String = {
    val parser = Parser.builder.build
    val document = parser.parse(input)
    val renderer = HtmlRenderer.builder.build
    renderer.render(document)
  }

  def writeToFile(s: String, file: File) {
    val out = new PrintWriter(file, "UTF-8")
    try {
      out.print(s)
    }
    finally {
      out.close()
    }
  }
}
