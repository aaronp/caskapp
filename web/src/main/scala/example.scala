// using scala-js
// using scala 3.1.0
// using lib org.scala-js::scalajs-dom:2.1.0
//
// see https://scala-cli.virtuslab.org/docs/guides/scala-js
// scala-cli setup-ide .
// scala-cli . --js
import org.scalajs.dom
import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.{Ajax, AjaxException}

import java.util.Base64
import concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.URIUtils.encodeURIComponent

def appendPar(targetNode: dom.Node, text: String): Unit = {
  val parNode = document.createElement("p")
  parNode.textContent = text
  targetNode.appendChild(parNode)
}

@JSExportTopLevel("addClickedMessage")
def addClickedMessage(): Unit = {
  appendPar(document.body, "You clicked the button!")
}

def auth = {
  val user = "aaronpritzlaff@nimbleapproach.com"
  val pwd = "4FVvUCTyX9s6NZW32ISk94A5"
  //    val token: String = encodeURIComponent(user + ':' + pwd)

  //    val decoded: Array[Byte] = Base64.getUrlDecoder.decode(token)
  //    val enc = Base64.getUrlEncoder.encodeToString(decoded)
  //    println(token)
  //    println(enc)
  //    val token = Base64.getEncoder.encodeToString(x)

  s"Basic aaronpritzlaff%40nimbleapproach.com:4FVvUCTyX9s6NZW32ISk94A5"
}

// see https://id.atlassian.com/manage-profile/security/api-tokens
def fetch() = {
  val got = Ajax.get("http://localhost:8080/allspacespages", headers = Map(
    "Access-Control-Allow-Origin" -> "*"))

  got.onComplete { result =>
    println(s"result was $result")
  }
}

def fetch2() = {
  val got = Ajax.get("https://nimbledelivery.atlassian.net/wiki/rest/api/space?type=global",
    headers = Map("Authorization" -> auth,
    "Access-Control-Allow-Origin" -> "nimbledelivery.atlassian.net"))
  got.onComplete { result =>
    println(s"result was $result")
  }
}

@main def main() = {
  fetch()
  document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
    setupUI()
  })
}

def setupUI(): Unit = {
  val button = document.createElement("button")
  button.textContent = "Click me!"
  button.addEventListener("click", { (e: dom.MouseEvent) =>
    addClickedMessage()
  })
  document.body.appendChild(button)

  appendPar(document.body, "Hello World")
}