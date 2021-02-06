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
  test("list") {
    val result = Executor.execute(UserSchema().UserSchema, UserQueries.listQuery, UserStore())
    val json = Await.result(result, 1 second).asJsObject
    println( json )
    json.fields.nonEmpty shouldBe true
  }

  test("find") {
    val query = Executor.execute(UserSchema().UserSchema, UserQueries.findQuery, UserStore())
    val json = Await.result(query, 1 second).asJsObject
    println( json )
    json.fields.nonEmpty shouldBe true
  }
}