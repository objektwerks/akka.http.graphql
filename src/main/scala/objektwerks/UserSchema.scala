package objektwerks

import sangria.macros.derive.{Interfaces, deriveObjectType}
import sangria.schema.{Argument, Field, IntType, InterfaceType, ListType, ObjectType, OptionType, Schema, fields}

trait Identifiable extends Product with Serializable {
  def id: Int
}

object UserSchema {
  def apply(): UserSchema = new UserSchema
}

class UserSchema {
  val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity with id field.",
    fields[Unit, Identifiable]( Field("id", IntType, resolve = _.value.id) )
  )

  val UserType = deriveObjectType[Unit, User]( Interfaces( IdentifiableType ) )

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
      description = Some("Returns list of users."),
      resolve = context => context.ctx.list)
    )
  )

  val UserSchema = Schema(UserQueryType)
}