package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest

import com.typesafe.config.ConfigFactory

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserAppTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {
  val conf = ConfigFactory.load("user.app.conf")
  val name = conf.getString("app.name")
  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")

  val actorRefFactory = ActorSystem.create(name, conf)

  val routes = UserRouter(executor).routes
  val server = Http()
    .newServerAt(host, port)
    .bindFlow(routes)

  override protected def afterAll(): Unit = {
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  println(s"list: ${UserQueries.listAsJson}")
  println(s"find: ${UserQueries.findAsJson}")

  "app" should {
    "load graphql" in {
      Get("/") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "list" should {
    "list" in {
      Post("/graphql", UserQueries.listAsJson) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "find" should {
    "find" in {
      Post("/graphql", UserQueries.findAsJson) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}