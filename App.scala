// using scala 3.1.0
// using lib com.lihaoyi::cask:0.8.0
// using lib io.circe::circe-core:0.14.1
// using lib io.circe::circe-generic:0.14.1
// using lib io.circe::circe-parser:0.14.1

import io.circe.syntax.*
import io.circe.*
import io.circe.generic.auto.*

import java.time.LocalDate
import scala.util.*

/**
 */
object App extends cask.MainRoutes {

  @cask.staticFiles("/ui", headers = Seq("Cache-Control" -> "max-age=31536000"))
  def staticFileRoutes() = "web"


  @cask.staticFiles("/data")
  def data() = "data"

  private def box(str : String): String = {
    val lines = str.linesIterator.toList
    val maxLen = (0 +: lines.map(_.length)).max
    val boxed = lines.map { line =>
      s" | ${line.padTo(maxLen, ' ')} |"
    }
    val bar = " +-" + ("-" * maxLen) + "-+"
    (bar +: boxed :+ bar).mkString("\n")
  }

  override def host: String = "0.0.0.0"
  override def port = 8080

  initialize()

  println(box(
    s""" 🚀 browse to localhost:8080 and/or open jconsole 🚀
      |      host : $host
      |      port : $port
      |   verbose : $verbose
      | debugMode : $debugMode
      |""".stripMargin))
}