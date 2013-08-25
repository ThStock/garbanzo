import java.io._
import scala.io.Source
import org.fusesource.scalate.TemplateEngine
import eu.henkelmann.actuarius.ActuariusTransformer
import collection.mutable.Map

object Generator extends App {

    val engine = new TemplateEngine
    val outputFile = new File("index.html")
    val resources = "src/main/resources"
    val contentFolder = new File(outputFile.getParentFile, resources)
    val webPath = contentFolder.toURI

    writeToFile(toHtml(findEntries.mkString("\n")), outputFile)

    def findEntries():List[String] = {
        def findRecursiv(file:File, filter:File => Boolean):Array[File] = {
            val files = file.listFiles
            return files.filter(filter) ++ files.filter(_.isDirectory)
                .flatMap(findRecursiv(_,filter))
        }
        return findRecursiv(contentFolder, _.getName == "index.md")
            .map(index => index.getParentFile.getPath.substring(resources.length + 1))
            .sorted.reverse
            .map(folder => toTemplate(folder, "/index.md"))
            .toList
    }

    def toTemplate(pathTo:String, fileName:String, map:Map[String,AnyRef]
        = Map.empty[String,AnyRef]):String = {
        val text = Source.fromInputStream(getClass.getResourceAsStream(pathTo + fileName)).mkString
        val template = engine.compileMoustache(text)
        map += "path" -> (webPath + pathTo)
        map += "changeDate" -> (Util.lastEdit(resources + "/" + pathTo + fileName))
        map += "gravatar" -> ((emails:String) => Util.gravatars(emails))
        return engine.layout("",template, map.toMap)
    }

    def toHtml(md:String):String = {
        return toTemplate("", "main.mu", Map("entries" -> fromMdtoHtml(md)))
    }

    def fromMdtoHtml(input:String) = new ActuariusTransformer()(input)

    def writeToFile( s: String, file:File) {
        val out = new PrintWriter(file, "UTF-8")
        try{ out.print( s ) }
        finally{ out.close }
    }
}
