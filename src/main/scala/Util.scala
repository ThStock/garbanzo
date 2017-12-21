import java.io._
import java.security.MessageDigest
import java.text.SimpleDateFormat

import org.eclipse.jgit.api._
import org.eclipse.jgit.lib._
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file._

import scala.collection.JavaConverters._
import scala.xml._

object Util {

  val repository: Repository = new FileRepositoryBuilder().setGitDir(new File("./.git"))
    .readEnvironment()
    .findGitDir()
    .build()

  def lastEdit(path: String): String = {

    val commits:List[RevCommit] = new Git(repository)
      .log()
      .addPath(path)
      .call()
      .asScala
      .toList

    if (commits.isEmpty) {
      "n/a"
    } else {
      val dateOfFile = commits.head.getAuthorIdent.getWhen
      val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      formatter.format(dateOfFile)
    }
  }

  def gravatars(emails: String): scala.xml.Elem = {

    def gravatar(email: String): Elem = {
      val url = "https://secure.gravatar.com/avatar/%s?s=140&d=identicon".format(md5(email.trim))
      <span>
        {<img height="20" width="20" style="border-radius:3px;"/> %
          Attribute(None, "src", new Unparsed(url), Null)}{<a>
          {email}
        </a> %
          Attribute(None, "href", new Unparsed("mailto:" + email), Null)}
      </span>
    }

    def md5(s: String) = MessageDigest.getInstance("MD5")
      .digest(s.getBytes).map("%02x".format(_)).mkString

    val emailList: Seq[String] = emails.split(" ").toSeq.map(_.trim).filterNot(_.isEmpty)
    return <span>
      {emailList.map(gravatar)}
    </span>
  }
}
