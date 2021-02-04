package objektwerks

import akka.http.scaladsl.server.Directives

trait UserRouter  extends Directives with UserType with UserJsonSupport {
  val index = path("") {
    getFromResource("user/graphql.html")
  }
  val routes = index
}