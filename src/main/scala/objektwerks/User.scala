package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json._
import spray.json.lenses.JsonLenses._

case class User(id: Int, name: String) extends Identifiable

object UserJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
  val listPath = Symbol("data") / Symbol("list") / *
  val findPath = Symbol("data") / Symbol("find")

  def jsonToUsers(json: String): Seq[User] = json.extract[User](listPath)

  def jsonToUser(json: String): User = json.extract[User](findPath)
}