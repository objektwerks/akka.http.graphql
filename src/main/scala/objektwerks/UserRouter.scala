package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object UserRouter {
  def apply(userSchema: UserSchema, userStore: UserStore)
           (implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter(userSchema, userStore)
}

class UserRouter(userSchema: UserSchema, userStore: UserStore)
                (implicit executor: ExecutionContextExecutor) extends Directives {
  val querySchema = userSchema.UserSchema
  val toBadRequest = (error: Throwable) => complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))

  val index = path("") {
    getFromResource("public/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJsValue =>
        GraphQL.parseQueryJsValue(queryJsValue) match {
          case Success((query, operationName, variables)) =>
            GraphQL.parseQuery(query) match {
              case Success(document) => complete(GraphQL.executeQuery(querySchema, userStore, document, operationName, variables))
              case Failure(error) => toBadRequest(error)
            }
          case Failure(error) => toBadRequest(error)
        }
      }
    }
  }

  val routes = index ~ api
}