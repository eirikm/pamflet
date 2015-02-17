package pamflet.news

import java.io.File
import com.github.nscala_time.time.Imports._

import pamflet.{FileStorage,FrontPageNews,NewsStory}

case class NewsStorage(base: File) extends FileStorage {
  import FileStorage._
  def frontPage(dir: File, propFiles: Seq[File]): FrontPageNews = {
    // templating likely won't work if we use defaultTemplate here

    val newsStories = for ((date, file) <- datedStories(dir)) yield {
      val (raw, blocks, template) = knock(file, propFiles)
      NewsStory(
        "what is local path",
        raw,
        blocks,
        date,
        template
      )
    }

    FrontPageNews(newsStories, defaultTemplate)
  }

  val Y = "(\\d{4})".r
  val M = "(\\d{2})".r
  val D = M

  def datedStories(dir: File): Stream[(LocalDate,File)] = {
    FileStorage.depthFirstFiles(dir).filter(FileStorage.isMarkdown).flatMap { f =>
      FileStorage.parents(f).take(3).map(_.getName).reverse match {
        case Seq(Y(year), M(month), D(day)) =>
          Some(new LocalDate(year.toInt, month.toInt, day.toInt) -> f)
        case dirs =>
          Console.err.println(
            "Story does not match expected (yyyy/mm/dd/) parent directories:\n" +
              f.getPath
          )
          None
      }
    }
  }
}
