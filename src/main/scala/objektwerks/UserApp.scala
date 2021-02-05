package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import com.typesafe.config.ConfigFactory

import org.slf4j.LoggerFactory

import scala.io.StdIn

object UserApp {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(getClass)
    val conf = ConfigFactory.load("user.app.conf")
    val name = conf.getString("app.name")
    val host = conf.getString("app.host")
    val port = conf.getInt("app.port")

    implicit val system = ActorSystem.create(name, conf)
    implicit val executor = system.dispatcher

    val server = Http()
      .newServerAt(host, port)
      .bindFlow(UserRouter.routes)

    logger.info(s"*** $name started at http://$host:$port/\nPress RETURN to stop...")

    StdIn.readLine()
    server
      .flatMap(_.unbind())
      .onComplete { _ =>
        system.terminate()
        logger.info(s"*** $name stopped.")
      }
  }
}