package objektwerks

import sangria.ast.Document
import sangria.macros._

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
  val listAsString =
    """
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
  val findAsString =
    """
      query Find {
        find(id: 1) {
          name
        }
      }
    """
}