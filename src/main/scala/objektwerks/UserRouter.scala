package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives

import sangria.ast.Document
import sangria.execution.ExecutionScheme.Default.Result
import sangria.execution.Executor
import sangria.marshalling.sprayJson.{SprayJsonInputUnmarshallerJObject, SprayJsonResultMarshaller}
import sangria.schema.Schema

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object UserRouter {
  def apply(userSchema: Schema[UserStore, Unit], userStore: UserStore)
           (implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter(userSchema, userStore)

  def executeQuery(userSchema: Schema[UserStore, Unit],
                   userStore: UserStore,
                   query: Document,
                   operationName: Option[String],
                   variables: JsObject)(implicit executor: ExecutionContextExecutor): Result[StatusCode, JsValue] =
    Executor.execute(userSchema, query, userStore, variables = variables, operationName = operationName)
}

class UserRouter(userSchema: Schema[UserStore, Unit], userStore: UserStore)
                (implicit executor: ExecutionContextExecutor) extends Directives {
  import GraphQL._
  import UserRouter._

  val index = path("") {
    getFromResource("public/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJsValue =>
        val tryResult = for {
          (query, operationName, variables) <- parseQueryJsValue(queryJsValue)
          document <- parseQuery(query)
        } yield executeQuery(userSchema, userStore, document, operationName, variables)
        tryResult match {
          case Success(result) => complete(result)
          case Failure(error) => complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
        }
      }
    }
  }

  val routes = index ~ api
}