import com.typesafe.scalalogging.Logger
import de.l3s.boilerpipe.extractors.CanolaExtractor
import spray.json._
import JsonNewsProtocol.jsonNewsFormat

import java.io.File

class ArticleExtraction {

  private val logger: Logger = Logger("RssExtraction Logger")
  private val newsFilesFolderPath = "../news-files/"


  def getAllFileNamesFromDir: Array[File] =
    new File(newsFilesFolderPath).listFiles.filter(_.isFile).filter(_.getName.endsWith(".json"))


  def getNewsObject(fileName: File): Option[News] = {
    val source = scala.io.Source.fromFile(fileName)
    try {
      val sourceString = source.mkString
      Some(sourceString.parseJson.convertTo[News])
    } catch {
      case e: Exception =>
        logger.error("Extracting file: " + fileName + " has failed")
        None
    } finally source.close()
  }


  def stripHtml(htmlArticle: String, stoppwortList: Array[String], miscList: Array[Char]): Array[String] =
    CanolaExtractor.INSTANCE.getText(htmlArticle)
      .split('\n').mkString("", " ", "").split(" ")
      .map(el => {
        if(el.nonEmpty && miscList.contains(el(el.length-1))) el.dropRight(1)
        else el
      })
      .map(el => if(el.nonEmpty && miscList.contains(el.head)) el.drop(1) else el)
      .filter(el => el.nonEmpty && !stoppwortList.contains(el.toLowerCase))


  def wordsByAmount(article: Array[String]): Map[String, Int] = {
    //TODO to oneliner
    val wordsMap = article.groupBy(identity).transform((k, v) => v.size)
    println(article.length + " " + article.mkString("(",", ",")"))
    println(wordsMap.size + " " + wordsMap + "\n")
    wordsMap
  }
}