// using scala 3.0.2
// using lib com.lihaoyi::requests:0.7.0
// using lib io.circe::circe-generic:0.15.0-M1
// using lib io.circe::circe-parser:0.15.0-M1

import io.circe.parser.*

object Confluence {

  
  // def globalSpacesUrl = "https://nimbledelivery.atlassian.net/wiki/rest/api/space?type=global"
  // def spaceUrl(spaceKey : String, offset : Int, limit :Int) =
  //   s"https://nimbledelivery.atlassian.net/wiki/rest/api/space/$spaceKey/content?start=$offset&limit=$limit&depth=all&expand=ancestors,childType.pages"

  def allSpaces = "https://nimbledelivery.atlassian.net/wiki/rest/api/space/DEV/content?depth=all&expand=ancestors,childType.pages"

  case class Client(user : String, pwd: String) {
    override def toString = s"Client($user, ${pwd.map(_ => '*')})"
//    private def credentialsString = s"$user:$pwd"
//    private def auth = java.util.Base64.getEncoder.encode(credentialsString.getBytes("UTF-8"))
    def basicAuth = requests.RequestAuth.Basic(user, pwd)
    def spaces = {
      val r = requests.get(allSpaces, auth = basicAuth)
      println(r.text)
      println(r.getClass)
      
    }
  }
  object Client {

    private def failOnKey = {
      val dt =java.awt.Desktop.getDesktop
      val tokenUrl = "https://id.atlassian.com/manage-profile/security/api-tokens"
      val msg = s"Usage: env variable 'KEY' not set. Generate one from $tokenUrl"
      println(msg)
      dt.browse(new java.net.URI(tokenUrl))
      sys.error(msg)
    }

    def user = sys.env.getOrElse("USER", sys.error("Usage: env variable 'CONFLUENCE_USER' not set"))
    def apiKey = sys.env.getOrElse("KEY", failOnKey)
    def apply() = new Client(user, apiKey)
  }
}
import Confluence.*

@main def mainFromEnv() =
  fetchKB(Client.user, Client.apiKey)

def fetchKB(user : String, key :String) =
  println("fetch kb")
  val client = Client(user, key)
  println(client.spaces)