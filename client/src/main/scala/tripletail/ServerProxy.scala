package tripletail

import akka.actor.ActorSystem

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.Http

import scala.collection.immutable._
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContextExecutor

object ServerProxy {
  def apply(implicit system: ActorSystem, executor: ExecutionContextExecutor): ServerProxy = new ServerProxy()
}

class ServerProxy(implicit system: ActorSystem, executor: ExecutionContextExecutor) {
  import Serializers._
  import upickle.default._

  def post(url: String, license: String, command: Command): Future[Either[Fault, Event]] = {
    (for {
        request <- Marshal(write[Command](command)).to[RequestEntity]
        response <- Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = url, headers = headers(license), entity = request))
        json <- Unmarshal(response.entity).to[String]
        status = response.status
      } yield {
        status.intValue match  {
          case 200 => Try(read[Event](json)).fold(error => Left(log(error)), event => Right(event))
          case 400 | 401 | 500 => Try(read[Fault](json)).fold(error => Left(log(error)), fault => Left(fault))
          case _ => Left( log( Fault(status.reason, status.intValue) ) )
        }
      }).recover { case error => Left( log(Fault(cause = error.getMessage)) ) }
  }

  def post(url: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    (for {
        request <- Marshal(write[Entity](entity)).to[RequestEntity]
        response <- Http().singleRequest(HttpRequest(method = HttpMethods.POST, uri = url, headers = headers(license), entity = request))
        json <- Unmarshal(response.entity).to[String]
        status = response.status
      } yield {
        status.intValue match  {
          case 200 => Try(read[State](json)).fold(error => Left(log(error)), event => Right(event))
          case 400 | 401 | 500 => Try(read[Fault](json)).fold(error => Left(log(error)), fault => Left(fault))
          case _ => Left( log( Fault(status.reason, status.intValue) ) )
        }
      }).recover { case error => Left( log(Fault(cause = error.getMessage)) ) }
  }

  def headers(license: String): Seq[HttpHeader] = Seq(
      RawHeader("Content-Type", "application/json; charset=utf-8"),
      RawHeader("Accept", "application/json"),
      RawHeader(Licensee.headerLicenseKey, license)
    )

  def log(error: Throwable): Fault = log(Fault(error.getMessage))

  def log(fault: Fault): Fault = {
    system.log.error(fault.toString)
    fault
  }
}