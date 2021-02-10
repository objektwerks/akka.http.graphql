package objektwerks

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import sangria.macros.derive.{Interfaces, deriveObjectType}
import sangria.schema.{Field, ListType, ObjectType, OptionType, Schema, fields}

import spray.json._
import spray.json.lenses.JsonLenses._

case class User(id: Int, name: String) extends Identifiable

object UserSchema {
  import Identifiable._

  val UserType = deriveObjectType[Unit, User]( Interfaces( IdentifiableType ) )

  val UserQueryType = ObjectType("Query", fields[UserStore, Unit](
    Field("find",
      OptionType(UserType),
      description = Some("Returns user by id."),
      arguments = Id :: Nil,
      resolve = context => context.ctx.find(context arg Id)
    ),
    Field("list",
      ListType(UserType),
      description = Some("Returns list of users."),
      resolve = context => context.ctx.list)
    )
  )

  val schema = Schema(UserQueryType)
}

object UserJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
  val listPath = Symbol("data") / Symbol("list") / *
  val findPath = Symbol("data") / Symbol("find")

  def jsonToUsers(json: String): Seq[User] = json.extract[User](listPath)

  def jsonToUser(json: String): User = json.extract[User](findPath)
}