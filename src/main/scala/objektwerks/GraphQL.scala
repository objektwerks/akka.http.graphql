package objektwerks

import sangria.ast.Document
import sangria.parser.QueryParser

import spray.json.{JsObject, JsString, JsValue}

import scala.util.Try

object GraphQL {
  def parseQueryJsValue(queryJsValue: JsValue): Try[(String, Option[String], JsObject)] = Try {
    val JsObject(fields) = queryJsValue
    val JsString(query) = fields("query")
    val operationName = fields.get("operationName") collect { case JsString(op) => op }
    val variables = fields.getOrElse("variables", JsObject.empty).asJsObject
    (query, operationName, variables)
  }

  def parseQuery(query: String): Try[Document] = QueryParser.parse(query)
}