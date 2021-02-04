package objektwerks

import akka.http.scaladsl.server.Directives

trait UserRouter  extends Directives with UserJsonSupport {
  val resources = get {
    getFromResourceDirectory("graphql.html")
  }
  val routes = resources
}