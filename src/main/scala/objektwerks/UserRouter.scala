package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives

import sangria.ast.Document
import sangria.execution.ExecutionScheme.Default.Result
import sangria.execution.Executor
import sangria.marshalling.sprayJson.{SprayJsonInputUnmarshallerJObject, SprayJsonResultMarshaller}
import sangria.parser.QueryParser
import sangria.schema.Schema

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

object UserRouter {
  def apply(userSchema: UserSchema, userStore: UserStore)
           (implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter(userSchema, userStore)

  def parseQueryJsValue(queryJsValue: JsValue): Try[(String, Option[String], JsObject)] = Try {
    val JsObject(fields) = queryJsValue
    val JsString(query) = fields("query")
    val operationName = fields.get("operationName") collect { case JsString(op) => op }
    val variables = fields.getOrElse("variables", JsObject.empty).asJsObject
    (query, operationName, variables)
  }

  def parseQuery(query: String): Try[Document] = QueryParser.parse(query)

  def executeQuery(userSchema: Schema[UserStore, Unit],
                   userStore: UserStore,
                   query: Document,
                   operationName: Option[String],
                   variables: JsObject)(implicit executor: ExecutionContextExecutor): Result[StatusCode, JsValue] =
    Executor.execute(userSchema, query, userStore, variables = variables, operationName = operationName)
}

class UserRouter(userSchema: UserSchema, userStore: UserStore)
                (implicit executor: ExecutionContextExecutor) extends Directives {
  import UserRouter._

  val querySchema = userSchema.UserSchema

  val index = path("") {
    getFromResource("public/graphql.html")
  }

  val api = path("graphql") {
    (get | post) {
      entity(as[JsValue]) { queryJsValue =>
        val futureResult = for {
          (query, operationName, variables) <- parseQueryJsValue(queryJsValue)
          document <- parseQuery(query)
        } yield executeQuery(querySchema, userStore, document, operationName, variables)
        futureResult match {
          case Success(result) => complete(result)
          case Failure(error) => complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
        }
      }
    }
  }

  val routes = index ~ api
}