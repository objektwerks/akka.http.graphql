package objektwerks

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}

import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson.{SprayJsonInputUnmarshallerJObject, SprayJsonResultMarshaller}
import sangria.parser.QueryParser
import sangria.schema.Schema

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Try

object GraphQL {
  def parseQuery(queryJsValue: JsValue): (String, Option[String], JsObject) = {
    val JsObject(fields) = queryJsValue
    val JsString(query) = fields("query")
    val operationName = fields.get("operationName") collect {
      case JsString(op) => op
    }
    val variables = fields.get("variables") match {
      case Some(jsObject: JsObject) => jsObject
      case _ => JsObject.empty
    }
    (query, operationName, variables)
  }

  def parseQuery(query: String): Try[Document] = QueryParser.parse(query)

  def executeQuery(userSchema: Schema[UserStore, Unit],
                   userStore: UserStore,
                   query: Document,
                   operationName: Option[String],
                   variables: JsObject)(implicit executor: ExecutionContextExecutor): Future[(StatusCode, JsValue)] =
    Executor
      .execute(userSchema, query, userStore, variables = variables, operationName = operationName)
      .map( OK -> _ )
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
}