package objektwerks

import com.typesafe.config.ConfigFactory

object TestConf {
  val conf = ConfigFactory.load("test.conf")
  val userSchema = UserSchema.schema
  val userStore = UserStore(conf)
  val name = conf.getString("app.name")
  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")
}