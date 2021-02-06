package objektwerks

import akka.http.scaladsl.server.Directives

import spray.json.JsValue

import scala.concurrent.ExecutionContextExecutor

object UserRouter {
  def apply()(implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter()
}

class UserRouter(implicit executor: ExecutionContextExecutor) extends Directives with UserSchema with UserJsonSupport {
  private val graphQL = GraphQL()

  val index = path("") {
    getFromResource("user/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJson =>
        graphQL.execute(queryJson)
      }
    }
  }

  val routes = index ~ api
}