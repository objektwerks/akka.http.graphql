package objektwerks

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{JsObject, JsValue}

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
    val validateResponse = (status: StatusCode, json: String) => {
      status shouldBe StatusCodes.OK
      json shouldBe response
      jsonToUsers( json ) shouldBe Seq(User(1, "Fred Flintstone"), User(2, "Barney Rebel"))
    }
    Get("/graphql", listQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsValue].compactPrint )
    }
    Post("/graphql", listQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsValue].compactPrint )
    }
  }

  "find" in {
    val response = """{"data":{"find":{"id":1,"name":"Fred Flintstone"}}}"""
    val validateResponse = (status: StatusCode, json: String) => {
      status shouldBe StatusCodes.OK
      json shouldBe response
      jsonToUser( json ) shouldBe User(1, "Fred Flintstone")
    }
    Get("/graphql", findQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsValue].compactPrint )
    }
    Post("/graphql", findQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsValue].compactPrint )
    }
  }

  "error" in {
    val validateResponse = (status: StatusCode, jsObject: JsObject) => {
      status shouldBe StatusCodes.BadRequest
      println(jsObject.compactPrint)
    }
    Get("/graphql", emptyQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsObject] )
    }
    Post("/graphql", emptyQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsObject] )
    }
  }

  "invalid" in {
    val validateResponse = (status: StatusCode, jsObject: JsObject) => {
      assert( status == StatusCodes.BadRequest || status == StatusCodes.InternalServerError )
      println(jsObject.compactPrint)
    }
    Get("/graphql", invalidQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsObject] )
    }
    Post("/graphql", invalidQueryAsJsValue) ~> routes ~> check {
      validateResponse( status, responseAs[JsObject] )
    }
  }
}