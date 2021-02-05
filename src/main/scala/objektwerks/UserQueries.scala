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

  val listQueryAsJson =
    """
      {
        "query": "{ list { id name } }"
      }
    """.parseJson

  val findQueryAsJson =
    """
      {
        "query": "{ find(id: 1) { name } }"
      }
    """.parseJson
}