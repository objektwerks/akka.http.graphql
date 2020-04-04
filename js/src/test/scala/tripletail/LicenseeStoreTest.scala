package tripletail

import utest._

import scala.scalajs.js

object LicenseeStoreTest extends TestSuite {
  assert(js.typeOf(js.Dynamic.global.IDBDatabase) != "undefined")

  def tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global
    test("licensee store") {
      val store = LicenseeStore()
      val licensee = Licensee(emailAddress = "tripletailwerks@gmail.com")
      for {
        optionalLicensee <- store.putLicensee(licensee)
      } yield validate(optionalLicensee)
      for {
        optionalLicensee <- store.getLicensee
      } yield validate(optionalLicensee)
    }
  }

  private def validate(licensee: Option[Licensee]): Unit = licensee match {
    case Some(licensee) => println(s"Licensee: $licensee")
    case None => println(s"No Licensee!")
  }
}