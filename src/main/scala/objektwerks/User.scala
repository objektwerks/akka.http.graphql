package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json._

final case class User(id: Int, name: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}