package objektwerks

import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object UserRouter {
  def apply()(implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter()
}

class UserRouter(implicit executor: ExecutionContextExecutor) extends Directives {
  import UserSchema._

  val index = path("") {
    getFromResource("user/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJsValue =>
        val (query, operationName, variables) = GraphQL.parseQuery(queryJsValue)
        GraphQL.parseQuery(query) match {
          case Success(document) => complete( GraphQL.executeQuery(UserSchema, document, operationName, variables) )
          case Failure(error) => complete( BadRequest, JsObject("error" -> JsString( error.getMessage ) ) )
        }
      }
    }
  }

  val routes = index ~ api
}