package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import spray.json.JsValue

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
      val response = """{"data":{"list":[{"id":1,"name":"Fred Flintstone"},{"id":2,"name":"Barney Rebel"}]}}"""
      Get("/graphql", UserQueries.listQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[JsValue].compactPrint shouldBe response
      }
      Post("/graphql", UserQueries.listQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[JsValue].compactPrint shouldBe response
      }
    }
  }

  "find" should {
    "find a user" in {
      val response = """{"data":{"find":{"name":"Fred Flintstone"}}}"""
      Get("/graphql", UserQueries.findQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[JsValue].compactPrint shouldBe response
      }
      Post("/graphql", UserQueries.findQueryAsJsValue) ~> routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[JsValue].compactPrint shouldBe response
      }
    }
  }
}