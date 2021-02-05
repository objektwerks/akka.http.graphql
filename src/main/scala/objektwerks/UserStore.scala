package objektwerks

object UserStore {
  def apply(): UserStore = new UserStore()
}

class UserStore {
  private val users = List( User(1, "Fred Flintstone"), User(2, "Barney Rebel") )

  def list: List[User] = users

  def find(id: Int): Option[User] = users.find( user => user.id == id )
}