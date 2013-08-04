import java.io._
import scala.io.Source
import org.fusesource.scalate.TemplateEngine
import eu.henkelmann.actuarius.ActuariusTransformer
import collection.mutable.Map

object Generator extends App {

    val engine = new TemplateEngine
    val outputFile = new File("index.html")
    val webPath = new File(outputFile.getParentFile, "src/main/resources").toURI

    val entries = List(getTemplate("2013/08-04-Demo", "/index.md"))
    writeToFile(toHtml(entries.mkString("\n")), outputFile)

    def getTemplate(pathTo:String, fileName:String, map:Map[String,String]
        = Map.empty[String,String]):String = {
        val text = Source.fromInputStream(getClass.getResourceAsStream(pathTo + fileName)).mkString
        val template = engine.compileMoustache(text)
        map += "path" -> (webPath + pathTo)
        return engine.layout("",template, map.toMap)
    }

    def toHtml(md:String):String = {
        return getTemplate("", "main.mu", Map("entries" -> fromMdtoHtml(md)))
    }

    def fromMdtoHtml(input:String) = new ActuariusTransformer()(input)

    def writeToFile( s: String, file:File) {
        def write(s:String, file:File) {
            val out = new PrintWriter(file, "UTF-8")
            try{ out.print( s ) }
            finally{ out.close }
        }
        if (file.exists) {
            val content = Source.fromFile(file).getLines.mkString.replaceAll("\n", "")
            if (content != s.replaceAll("\n", "")) {
                write(s, file)
            }
        } else {
            write(s, file)
        }
    }
}
