package objektwerks

import sangria.ast.Document
import sangria.macros._

import spray.json._

object UserQueries {
  val list: Document =
    graphql"""
      query List {
        list {
          id
          name
        }
      }
    """
  val find =
    graphql"""
      query Find {
        find(id: 1) {
          name
        }
      }
    """
  val listAsJson =
    """
      {
        "query": "{ list { id name } }"
      }
    """.parseJson.compactPrint
  val findAsJson =
    """
      {
        "query": "{ find(id: 1) { name } }"
      }
    """.parseJson.compactPrint
}