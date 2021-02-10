package objektwerks

import sangria.schema.{Argument, Field, IntType, InterfaceType, fields}

trait Identifiable extends Product with Serializable {
  def id: Int
}

object Identifiable {
  val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity with id field.",
    fields[Unit, Identifiable]( Field("id", IntType, resolve = _.value.id) )
  )

  val Id = Argument("id", IntType)
}