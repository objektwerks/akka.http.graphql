package objektwerks

import com.typesafe.config.Config
import io.getquill.{H2JdbcContext, Ord, SnakeCase}

object UserStore {
  def apply(conf: Config): UserStore = new UserStore(conf)
}

class UserStore(conf: Config) {
  implicit val ctx = new H2JdbcContext(SnakeCase, conf.getConfig("quill.ctx"))
  import ctx._

  run( query[User].insert( lift( User(id = 1, name = "Fred Flintstone") ) ) )
  run( query[User].insert( lift( User(id = 2, name = "Barney Rebel") ) ) )

  def list: List[User] = run( query[User].sortBy(_.name)(Ord.desc) )

  def find(id: Int): Option[User] = run( query[User].filter(_.id == lift(id) ) ).headOption
}