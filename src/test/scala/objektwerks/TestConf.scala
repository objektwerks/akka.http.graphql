package objektwerks

import com.typesafe.config.ConfigFactory

import sangria.macros._

import spray.json._

object TestConf {
  val conf = ConfigFactory.load("test.conf")

  val name = conf.getString("app.name")

  val userSchema = UserSchema.schema
  val userStore = UserStore(conf)

  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")

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
          id
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
        "query": "{ find(id: 1) { id name } }"
      }
    """.parseJson

  val emptyQueryAsJsValue = new JsString("")
}