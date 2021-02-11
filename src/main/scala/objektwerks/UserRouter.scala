package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.Directives

import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson.{SprayJsonInputUnmarshallerJObject, SprayJsonResultMarshaller}
import sangria.schema.Schema

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object UserRouter {
  def apply(userSchema: Schema[UserStore, Unit], userStore: UserStore)
           (implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter(userSchema, userStore)
}

class UserRouter(userSchema: Schema[UserStore, Unit], userStore: UserStore)
                (implicit executor: ExecutionContextExecutor) extends Directives {
  import GraphQL._

  val index = path("") {
    getFromResource("public/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJsValue =>
        val parsedQueryArgs = for {
          (query, operationName, variables) <- parseQueryJsValue(queryJsValue)
          document <- parseQuery(query)
        } yield (document, operationName, variables)
        parsedQueryArgs match {
          case Success( (document, operationName, variables) ) =>
            complete(
              Executor.execute(userSchema, document, userStore, variables = variables, operationName = operationName)
                .map( response => OK -> response )
                .recover {
                  case error: QueryAnalysisError => BadRequest -> error.resolveError
                  case error: ErrorWithResolver => InternalServerError -> error.resolveError
                }
            )
          case Failure(error) => complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
        }
      }
    }
  }

  val routes = index ~ api
}