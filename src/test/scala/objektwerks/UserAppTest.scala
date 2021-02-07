package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserAppTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {
  import TestConf._

  val actorRefFactory = ActorSystem.create(name, conf)

  val router = UserRouter( UserSchema(), userStore )
  val routes = router.routes
  val server = Http()
    .newServerAt(host, port)
    .bindFlow(routes)

  override protected def afterAll(): Unit = {
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  "app" should {
    "load graphql" in {
      Get("/") ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "list" should {
    "list users" in {
      Get("/graphql", UserQueries.listQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
      Post("/graphql", UserQueries.listQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }

  "find" should {
    "find a user" in {
      Get("/graphql", UserQueries.findQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
      Post("/graphql", UserQueries.findQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}