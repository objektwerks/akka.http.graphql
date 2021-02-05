package objektwerks

import akka.http.scaladsl.server.Directives

object UserRouter  extends Directives with UserGraphQl with UserJsonSupport {
  val index = path("") {
    getFromResource("user/graphql.html")
  }
  
  val routes = index
}