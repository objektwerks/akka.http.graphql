package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json._

final case class User(id: Int, name: String)

trait UserJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}