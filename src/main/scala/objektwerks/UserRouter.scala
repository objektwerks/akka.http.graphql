package objektwerks

import akka.http.scaladsl.server.Directives

import spray.json.JsValue

import scala.concurrent.ExecutionContextExecutor

object UserRouter {
  def apply()(implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter()
}

class UserRouter(implicit executor: ExecutionContextExecutor) extends Directives with UserSchema with UserJsonSupport {
  val graphQLExecutor = GraphQLExecutor()

  val index = path("") {
    getFromResource("user/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJson =>
        graphQLExecutor.execute(queryJson)
      }
    }
  }

  val routes = index ~ api
}