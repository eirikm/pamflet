package pamflet

import com.tristanhunt.knockoff._
import scala.util.parsing.input.{ CharSequenceReader, Position, Reader }
import scala.util.matching.Regex

trait SmartyDiscounter extends Discounter {
  override def createSpanConverter(
    linkDefinitions: Seq[LinkDefinitionChunk]) =
    new SpanConverter(linkDefinitions) with SmartySpanConverter
}

trait SmartySpanConverter extends SpanConverter {
  val punctClass = """[!\"#\$\%'()*+,-.\/:;<=>?\@\[\\\]\^_`{|}~]"""
  val closeClass = """[^\ \t\r\n\[\{\(\-]"""
  def smartyMatchers : List[ String => Option[SpanMatch] ] = List(
    // special case for quote as first character, then punctuation
    replacer("""^'(?=%s\B)""".format(punctClass).r, "‘"),
    replacer("""^"(?=%s\B)""".format(punctClass).r, "“"),
    // normal opening quote cases
    replacer("""(?<=\s|--|—)'(?=\w)""".r, "‘"),
    replacer("""(?<=\s|--|—)"(?=\w)""".r, "“"),
    // closing quotes
    replacer("""(?<=%s)?'(?=\s|\w|%s)""".format(closeClass, punctClass).r, "’"),
    replacer("""(?<=%s)?"(?=\s|%s)""".format(closeClass, punctClass).r, "”"),
    // assume everything else is opening
    replacer("'".r, "‘"),
    replacer("\"".r, "“"),
    replacer("--".r, "—"),
    replacer("""\.\.\.""".r, "…")
  )
  override def matchers =  smartyMatchers ::: super.matchers

  private def replacer(r: Regex, smarted: String)(source : String) = {
    r.findFirstMatchIn( source ).map { qmatch =>
      val before = qmatch.before.toOption.map( Text(_) )
      val html = HTMLSpan(smarted)
      SpanMatch( qmatch.start, before, html, qmatch.after.toOption )
    }
  }
}
