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
 * See https://metrics.dropwizard.io/4.1.2/getting-started.html
 */
object App extends cask.MainRoutes {

  case class CustomerRow(customerId: Long,
                         firstName: String,
                         lastName: String,
                         dateOfBirth: LocalDate,
                         email: String,
                         postCode: String,
                         mobile: String,
                         telephone: String)
  case class BulkUpdateRequest(updates: Seq[CustomerRow], deletes: Set[Long])

  private var byId = Map[Long, CustomerRow]()
  object Lock

  private def applyUpdate(request : BulkUpdateRequest) : Try[String] = Try {
    Lock.synchronized {
      val newMap = request.updates.foldLeft(byId) {
        case (map, update) =>
          map.updated(update.customerId, update)
      }
      byId = newMap -- request.deletes

      Map(
        "updates" -> request.updates.size.asJson,
        "deletes" -> request.deletes.size.asJson,
        "total" -> byId.size.asJson
      ).asJson.noSpaces
    }
  }

  @cask.put("/customer/v1/update")
  def update(request: cask.Request) = {
    val responseTry = for {
      json <- io.circe.parser.parse(request.text()).toTry
      request <- json.as[BulkUpdateRequest].toTry
      response <- applyUpdate(request)
    } yield response

    responseTry match {
      case Success(result) => result
      case Failure(err) => Map("error" -> err.toString.asJson).asJson.noSpaces
    }
  }

  def testData = CustomerRow(1,"name", "last", LocalDate.of(1,2,3), "e@mail.com", "postcode", "mobile", "tel")

  @cask.get("/")
  def usage() =
    s"""# create
       |curl -X PUT -d '${BulkUpdateRequest(Seq(testData), Set(20)).asJson.noSpaces}' http://localhost:8080//customer/v1/update
       |
       |# get
       |curl http://localhost:8080/get/12
       |
       |# list page / limit
       |curl http://localhost:8080/list/0/10
       |""".stripMargin

  @cask.get("/get/:id")
  def getId(id : Long) = byId.get(id).map(_.asJson).getOrElse(Json.Null).noSpaces

  @cask.get("/list/:page/:limit")
  def list(page : Int, limit : Int) = {
    val offset = page * limit
    byId.toList.sortBy(_._1).drop(offset).take(limit).map(_._2).toList.asJson.noSpaces
  }

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
  override def port = 8085

  initialize()

  println(box(
    s""" 🚀 browse to localhost:8080 and/or open jconsole 🚀
      |      host : $host
      |      port : $port
      |   verbose : $verbose
      | debugMode : $debugMode
      |""".stripMargin))
}