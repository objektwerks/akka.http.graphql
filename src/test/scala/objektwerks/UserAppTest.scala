package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import spray.json.JsValue

class UserAppTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {
  import TestConf._
  import UserJsonSupport._

  val actorRefFactory = ActorSystem.create(name, conf)

  val router = UserRouter( userSchema, userStore )
  val routes = router.routes
  val server = Http()
    .newServerAt(host, port)
    .bindFlow(routes)

  override protected def afterAll(): Unit = {
    server
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

  "load" in {
    Get("/") ~> routes ~> check {
      status shouldBe StatusCodes.OK
    }
  }

  "list" in {
    val response = """{"data":{"list":[{"id":1,"name":"Fred Flintstone"},{"id":2,"name":"Barney Rebel"}]}}"""
    val validateResponse = (json: String) => {
      json shouldBe response
      jsonToUsers( json ) shouldBe Seq(User(1, "Fred Flintstone"), User(2, "Barney Rebel"))
    }
    Get("/graphql", UserQueries.listQueryAsJsValue) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      validateResponse( responseAs[JsValue].compactPrint )
    }
    Post("/graphql", UserQueries.listQueryAsJsValue) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      validateResponse( responseAs[JsValue].compactPrint )
    }
  }

  "find" in {
    val response = """{"data":{"find":{"id":1,"name":"Fred Flintstone"}}}"""
    val validateResponse = (json: String) => {
      json shouldBe response
      jsonToUser( json ) shouldBe User(1, "Fred Flintstone")
    }
    Get("/graphql", UserQueries.findQueryAsJsValue) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      validateResponse( responseAs[JsValue].compactPrint )
    }
    Post("/graphql", UserQueries.findQueryAsJsValue) ~> routes ~> check {
      status shouldBe StatusCodes.OK
      validateResponse( responseAs[JsValue].compactPrint )
    }
  }

  "error" in {
    Get("/graphql", UserQueries.emptyQueryAsJsValue) ~> routes ~> check {
      status shouldBe StatusCodes.BadRequest
    }
    Post("/graphql", UserQueries.emptyQueryAsJsValue) ~> routes ~> check {
      status shouldBe StatusCodes.BadRequest
    }
  }
}