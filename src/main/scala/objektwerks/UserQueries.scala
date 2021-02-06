package objektwerks

import sangria.macros._

import spray.json._

object UserQueries {
  val listQuery =
    graphql"""
      query List {
        list {
          id
          name
        }
      }
    """

  val findQuery =
    graphql"""
      query Find {
        find(id: 1) {
          name
        }
      }
    """

  val listQueryAsJsValue =
    """
      {
        "query": "{ list { id name } }"
      }
    """.parseJson

  val findQueryAsJsValue =
    """
      {
        "query": "{ find(id: 1) { name } }"
      }
    """.parseJson
}