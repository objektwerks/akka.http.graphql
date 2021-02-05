package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import sangria.macros.derive._
import sangria.schema._

import spray.json._

trait Identifiable extends Product with Serializable {
  def id: Int
}

case class User(id: Int, name: String) extends Identifiable

trait UserSchema {
  implicit val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity with id field.",
    fields[Unit, Identifiable]( Field("id", IntType, resolve = _.value.id) )
  )

  implicit val UserType = deriveObjectType[Unit, User]( Interfaces( IdentifiableType ) )

  val Id = Argument("id", IntType)

  val UserQueryType = ObjectType("Query", fields[UserStore, Unit](
    Field("find",
      OptionType(UserType),
      description = Some("Returns user by id."),
      arguments = Id :: Nil,
      resolve = context => context.ctx.find(context arg Id)
    ),

    Field("list",
      ListType(UserType),
      description = Some("Returns ist of users."),
      resolve = context => context.ctx.list)
    )
  )

  val UserSchema = Schema(UserQueryType)
}

trait UserJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}