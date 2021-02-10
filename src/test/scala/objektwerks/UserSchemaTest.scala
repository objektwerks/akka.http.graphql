package objektwerks

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import sangria.execution._
import sangria.marshalling.sprayJson._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class UserSchemaTest extends AnyFunSuite with Matchers {
  import TestConf._
  import UserJsonSupport._

  test("list") {
    val result = Executor.execute(userSchema, listQuery, userStore)
    val json = Await.result(result, 1 second).asJsObject
    val users = jsonToUsers( json.compactPrint )
    println( json )
    println( users )
    users shouldBe Seq(User(1, "Fred Flintstone"), User(2, "Barney Rebel"))
  }

  test("find") {
    val query = Executor.execute(userSchema, findQuery, userStore)
    val json = Await.result(query, 1 second).asJsObject
    val user = jsonToUser( json.compactPrint )
    println( json )
    println( user )
    user shouldBe User(1, "Fred Flintstone")
  }
}