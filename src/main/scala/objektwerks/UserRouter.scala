package objektwerks

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.{Directives, Route}

import sangria.ast.Document
import sangria.execution._
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser

import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object UserRouter {
  def apply()(implicit executor: ExecutionContextExecutor): UserRouter = new UserRouter()
}

class UserRouter(implicit executor: ExecutionContextExecutor) extends Directives with UserSchema with UserJsonSupport {
  def executeGraphQLQuery(query: Document,
                          op: Option[String],
                          vars: JsObject) =
    Executor.execute(UserSchema, query, UserStore(), variables = vars, operationName = op)
      .map( OK -> _ )
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }

  def graphQLEndpoint(queryJson: JsValue): Route = {
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

  val index = path("") {
    getFromResource("user/graphql.html")
  }

  val getOrPost = get | post
  val api = path("graphql") {
    getOrPost {
      entity(as[JsValue]) { queryJson =>
        graphQLEndpoint(queryJson)
      }
    }
  }

  val routes = index ~ api
}