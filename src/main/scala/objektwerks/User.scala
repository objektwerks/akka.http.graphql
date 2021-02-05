package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import sangria.macros.derive._
import sangria.schema._
import spray.json._

trait Identifiable {
  def id: Int
}

final case class User(id: Int, name: String) extends Identifiable

trait IdentifiableType {
  implicit val identifiableType = InterfaceType(
    "Identifiable",
    "Entity that contains an id field.",
    fields[Unit, Identifiable]( Field("id", IntType, resolve = _.value.id) )
  )
}

trait UserType extends IdentifiableType {
  implicit val userType = deriveObjectType[Unit, User]( Interfaces(identifiableType) )
}

trait UserJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}

object UserStore {
  private val users = List( User(1, "Fred Flintstone"), User(2, "Barney Rebel") )

  def list: List[User] = users

  def find(id: Int): Option[User] = users.find( user => user.id == id )
}