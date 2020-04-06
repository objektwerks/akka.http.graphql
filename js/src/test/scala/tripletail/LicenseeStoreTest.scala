package tripletail

import utest._

object LicenseeStoreTest extends TestSuite {
  def tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global
    test("licensee store") {
      val store = LicenseeStore()
      val licensee = Licensee(emailAddress = "tripletailwerks@gmail.com")
      for {
        optionalLicensee <- store.putLicensee(licensee)
      } yield validate(licensee, optionalLicensee)
      for {
        optionalLicensee <- store.getLicensee
      } yield validate(licensee, optionalLicensee)
    }
  }

  private def validate(licensee: Licensee, optionalLicensee: Option[Licensee]): Unit = optionalLicensee match {
    case Some(value) => println(s"Licensee: $value"); assert(value == licensee)
    case None => println(s"No Licensee!"); assert(false)
  }
}