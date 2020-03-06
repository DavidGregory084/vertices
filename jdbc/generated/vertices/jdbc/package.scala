package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.sql.SQLOperations
import java.lang.String

package object jdbc {
  implicit class VertxJDBCClientOps(val target: JDBCClient) extends AnyVal {

    def querySingleL(sql: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.querySingle(sql, handler)
      }


    def querySingleWithParamsL(sql: String, arguments: JsonArray): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.querySingleWithParams(sql, arguments, handler)
      }
  }


}