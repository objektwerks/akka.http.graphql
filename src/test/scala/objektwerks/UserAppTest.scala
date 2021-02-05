package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest

import com.typesafe.config.ConfigFactory

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserAppTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll with UserRouter {
  val conf = ConfigFactory.load("user.app.conf")
  val name = conf.getString("app.name")
  val actorRefFactory = ActorSystem.create(name, conf)

  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")
  val server = Http()
    .newServerAt(host, port)
    .bindFlow(routes)

  override protected def afterAll(): Unit = {
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  "UserApp" should {
    "graphql" in {
      Get("/") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}