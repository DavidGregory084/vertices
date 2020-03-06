package vertices


import monix.eval.Task
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.circuitbreaker.CircuitBreakerState
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import java.lang.Integer
import java.lang.Long
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.function.Function

package object circuitbreaker {
  implicit class VertxCircuitBreakerOps(val target: CircuitBreaker) extends AnyVal {
    /**
     *  Same as {@link #executeWithFallback(Handler, Function)} but using a callback.
     * @param command  the operation
     * @param fallback the fallback
     * @param handler  the completion handler receiving either the operation result or the fallback result. The
     *                  parameter is an {@link AsyncResult} because if the fallback is not called, the error is passed
     *                  to the handler.
     * @param <T>      the type of result
     */
    def executeWithFallbackL[T](command: Handler[Promise[T]], fallback: Function[Throwable,T]): Task[T] =
      Task.handle[T] { handler =>
        target.executeWithFallback(command, fallback, handler)
      }

    /**
     *  Same as {@link #executeWithFallback(Handler, Function)} but using the circuit breaker default fallback.
     * @param command the operation
     * @param handler  the completion handler receiving either the operation result or the fallback result. The
     *                  parameter is an {@link AsyncResult} because if the fallback is not called, the error is passed
     *                  to the handler.
     * @param <T>     the type of result
     */
    def executeL[T](command: Handler[Promise[T]]): Task[T] =
      Task.handle[T] { handler =>
        target.execute(command, handler)
      }
  }


}