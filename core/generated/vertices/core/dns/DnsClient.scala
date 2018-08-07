package vertices
package core.dns

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.dns.MxRecord
import io.vertx.core.dns.SrvRecord
import io.vertx.core.dns.{ DnsClient => JavaDnsClient }
import java.lang.String
import java.util.List
import monix.eval.Task

import scala.language.implicitConversions

case class DnsClient(val unwrap: JavaDnsClient) extends AnyVal {
  // Async handler method
  def lookup(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.lookup(name, handler)
    }

  // Async handler method
  def lookup4(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.lookup4(name, handler)
    }

  // Async handler method
  def lookup6(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.lookup6(name, handler)
    }

  // Async handler method
  def resolveA(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveA(name, handler)
    }

  // Async handler method
  def resolveAAAA(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveAAAA(name, handler)
    }

  // Async handler method
  def resolveCNAME(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveCNAME(name, handler)
    }

  // Async handler method
  def resolveMX(name: String): Task[List[MxRecord]] =
    Task.handle[List[MxRecord]] { handler =>
      unwrap.resolveMX(name, handler)
    }

  // Async handler method
  def resolveTXT(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveTXT(name, handler)
    }

  // Async handler method
  def resolvePTR(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.resolvePTR(name, handler)
    }

  // Async handler method
  def resolveNS(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveNS(name, handler)
    }

  // Async handler method
  def resolveSRV(name: String): Task[List[SrvRecord]] =
    Task.handle[List[SrvRecord]] { handler =>
      unwrap.resolveSRV(name, handler)
    }

  // Async handler method
  def reverseLookup(ipaddress: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.reverseLookup(ipaddress, handler)
    }
}
object DnsClient {
  implicit def javaDnsClientToVerticesDnsClient(j: JavaDnsClient): DnsClient = apply(j)
  implicit def verticesDnsClientToJavaDnsClient(v: DnsClient): JavaDnsClient = v.unwrap


}
