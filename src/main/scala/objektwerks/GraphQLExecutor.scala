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
  def execute(queryJson: JsValue): Route = graphQLEndpoint(queryJson)

  private def graphQLEndpoint(queryJson: JsValue): Route = {
    val JsObject(fields) = queryJson
    val JsString(query) = fields("query")
    val op = fields.get("operationName") collect {
      case JsString(op) => op
    }
    val vars = fields.get("variables") match {
      case Some(obj: JsObject) => obj
      case _ => JsObject.empty
    }
    QueryParser.parse(query) match {
      case Success(ast) => complete( executeGraphQLQuery(ast, op, vars) )
      case Failure(error) => complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }
  }

  private def executeGraphQLQuery(query: Document,
                                  op: Option[String],
                                  vars: JsObject): Future[(StatusCode, SprayJsonResultMarshaller.Node)] =
    Executor.execute(UserSchema, query, UserStore(), variables = vars, operationName = op)
      .map( OK -> _ )
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
}