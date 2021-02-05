package objektwerks

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.{Directives, Route}

import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson.{SprayJsonInputUnmarshallerJObject, SprayJsonResultMarshaller}
import sangria.parser.QueryParser

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object GraphQLExecutor {
  def apply()(implicit executor: ExecutionContextExecutor): GraphQLExecutor = new GraphQLExecutor()
}

class GraphQLExecutor(implicit executor: ExecutionContextExecutor) extends Directives with UserSchema with UserJsonSupport {
  def execute(queryJson: JsValue): Route = parseExecuteQuery(queryJson)

  private def parseExecuteQuery(queryJson: JsValue): Route = {
    val JsObject(fields) = queryJson
    val JsString(query) = fields("query")
    val operationName = fields.get("operationName") collect {
      case JsString(op) => op
    }
    val variables = fields.get("variables") match {
      case Some(jsObject: JsObject) => jsObject
      case _ => JsObject.empty
    }
    QueryParser.parse(query) match {
      case Success(document) => complete( executeQuery(document, operationName, variables) )
      case Failure(error) => complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }
  }

  private def executeQuery(query: Document,
                           operationName: Option[String],
                           variables: JsObject): Future[(StatusCode, SprayJsonResultMarshaller.Node)] =
    Executor.execute(UserSchema, query, UserStore(), variables = variables, operationName = operationName)
      .map( OK -> _ )
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
}