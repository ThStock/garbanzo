import java.security.MessageDigest
import scala.xml._
import org.eclipse.jgit.storage.file._
import org.eclipse.jgit.lib._
import org.eclipse.jgit.api._
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.diff._
import org.eclipse.jgit.util.io._
import java.io._
import java.util.Date
import java.text.SimpleDateFormat
import scala.collection.JavaConversions._

object Util {

    val repository:Repository = new FileRepositoryBuilder().setGitDir(new File("./.git"))
        .readEnvironment()
        .findGitDir()
        .build()

    def lastEdit(path:String):String = {
        val commits = new Git(repository)
            .log()
            .addPath(path)
            .call()
            .toList
        if (commits.isEmpty) {
            return "n/a"
        } else {
            val dateOfFile = commits.head.getAuthorIdent.getWhen
            val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return formatter.format(dateOfFile)
        }
    }

    def gravatars(emails:String):scala.xml.Elem = {

        def gravatar(email:String):Elem = {
            val url = "https://secure.gravatar.com/avatar/%s?s=140&d=identicon".format(md5(email.trim))
            return <span> {<img height="20" width="20" style="border-radius:3px;"/> %
                Attribute(None, "src", new Unparsed(url), Null)} {<a>{email}</a> %
                 Attribute(None, "href", new Unparsed("mailto:" + email), Null)} </span>
        }

        def md5(s: String) = MessageDigest.getInstance("MD5")
            .digest(s.getBytes).map("%02x".format(_)).mkString

        val emailList:Seq[String] = emails.split(" ").toSeq.map(_.trim).filterNot(_.isEmpty)
        return <span>{emailList.map(gravatar)}</span>
    }
}
