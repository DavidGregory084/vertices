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

/**
 *  Provides a way to asynchronously lookup information from DNS servers.
 *  <p>
 *  Please consult the documentation for more information on DNS clients.
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
case class DnsClient(val unwrap: JavaDnsClient) extends AnyVal {
  /**
   *  Try to lookup the A (ipv4) or AAAA (ipv6) record for the given name. The first found will be used.
   * @param name  the name to resolve
   * @param handler  the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
   *                  The handler will get notified with the resolved address if a record was found. If non was found it
   *                  will get notifed with {@code null}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently
   */
  def lookup(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.lookup(name, handler)
    }

  /**
   *  Try to lookup the A (ipv4) record for the given name. The first found will be used.
   * @param name  the name to resolve
   * @param handler  the {@link Handler} to notify with the {@link io.vertx.core.AsyncResult}.
   *                  The handler will get notified with the resolved {@link java.net.Inet4Address} if a record was found.
   *                  If non was found it will get notifed with {@code null}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently
   */
  def lookup4(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.lookup4(name, handler)
    }

  /**
   *  Try to lookup the AAAA (ipv6) record for the given name. The first found will be used.
   * @param name  the name to resolve
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with the resolved {@link java.net.Inet6Address} if a record was found. If non was found
   *                  it will get notifed with {@code null}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently
   */
  def lookup6(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.lookup6(name, handler)
    }

  /**
   *  Try to resolve all A (ipv4) records for the given name.
   * @param name  the name to resolve
   * @param handler  the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
   *                  The handler will get notified with a {@link java.util.List} that contains all the resolved
   *                  {@link java.net.Inet4Address}es. If none was found an empty {@link java.util.List} will be used.
   *                  If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently
   */
  def resolveA(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveA(name, handler)
    }

  /**
   *  Try to resolve all AAAA (ipv6) records for the given name.
   * @param name  the name to resolve
   * @param handler the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
   *                 The handler will get notified with a {@link java.util.List} that contains all the resolved
   *                 {@link java.net.Inet6Address}es. If none was found an empty {@link java.util.List} will be used.
   *                 If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently
   */
  def resolveAAAA(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveAAAA(name, handler)
    }

  /**
   *  Try to resolve the CNAME record for the given name.
   * @param name  the name to resolve the CNAME for
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with the resolved {@link String} if a record was found. If none was found it will
   *                  get notified with {@code null}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def resolveCNAME(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveCNAME(name, handler)
    }

  /**
   *  Try to resolve the MX records for the given name.
   * @param name  the name for which the MX records should be resolved
   * @param handler  the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
   *                  The handler will get notified with a List that contains all resolved {@link MxRecord}s, sorted by
   *                  their {@link MxRecord#priority()}. If non was found it will get notified with an empty
   *                  {@link java.util.List}.  If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def resolveMX(name: String): Task[List[MxRecord]] =
    Task.handle[List[MxRecord]] { handler =>
      unwrap.resolveMX(name, handler)
    }

  /**
   *  Try to resolve the TXT records for the given name.
   * @param name  the name for which the TXT records should be resolved
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with a List that contains all resolved {@link String}s. If none was found it will
   *                  get notified with an empty {@link java.util.List}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def resolveTXT(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveTXT(name, handler)
    }

  /**
   *  Try to resolve the PTR record for the given name.
   * @param name  the name to resolve the PTR for
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with the resolved {@link String} if a record was found. If none was found it will
   *                  get notified with {@code null}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def resolvePTR(name: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.resolvePTR(name, handler)
    }

  /**
   *  Try to resolve the NS records for the given name.
   * @param name  the name for which the NS records should be resolved
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with a List that contains all resolved {@link String}s. If none was found it will
   *                  get notified with an empty {@link java.util.List}.  If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def resolveNS(name: String): Task[List[String]] =
    Task.handle[List[String]] { handler =>
      unwrap.resolveNS(name, handler)
    }

  /**
   *  Try to resolve the SRV records for the given name.
   * @param name  the name for which the SRV records should be resolved
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with a List that contains all resolved {@link SrvRecord}s. If none was found it will
   *                  get notified with an empty {@link java.util.List}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def resolveSRV(name: String): Task[List[SrvRecord]] =
    Task.handle[List[SrvRecord]] { handler =>
      unwrap.resolveSRV(name, handler)
    }

  /**
   *  Try to do a reverse lookup of an IP address. This is basically the same as doing trying to resolve a PTR record
   *  but allows you to just pass in the IP address and not a valid ptr query string.
   * @param ipaddress  the IP address to resolve the PTR for
   * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
   *                  notified with the resolved {@link String} if a record was found. If none was found it will
   *                  get notified with {@code null}. If an error accours it will get failed.
   * @return a reference to this, so the API can be used fluently.
   */
  def reverseLookup(ipaddress: String): Task[String] =
    Task.handle[String] { handler =>
      unwrap.reverseLookup(ipaddress, handler)
    }
}
object DnsClient {
  implicit def javaDnsClientToVerticesDnsClient(j: JavaDnsClient): DnsClient = apply(j)
  implicit def verticesDnsClientToJavaDnsClient(v: DnsClient): JavaDnsClient = v.unwrap


}
