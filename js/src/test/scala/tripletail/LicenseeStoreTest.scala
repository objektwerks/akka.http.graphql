package tripletail

import org.scalajs.dom
import org.scalajs.dom.console

import scala.concurrent.ExecutionContext.Implicits.global

import utest._

object LicenseeStoreTest extends TestSuite {
  def tests = Tests {
    test("licensee store") {
      val window = dom.window
      console.info(window.name)

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