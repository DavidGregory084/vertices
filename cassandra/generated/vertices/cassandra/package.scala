package vertices


import monix.eval.Task
import com.datastax.driver.core.ColumnDefinitions
import com.datastax.driver.core.ExecutionInfo
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Statement
import io.vertx.cassandra.CassandraClient
import io.vertx.cassandra.CassandraClientOptions
import io.vertx.cassandra.CassandraRowStream
import io.vertx.cassandra.Mapper
import io.vertx.cassandra.ResultSet
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import java.lang.Object
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List

package object cassandra {
  implicit class VertxResultSetOps(val target: ResultSet) extends AnyVal {
    /**
     * 
     * @param handler handler called when result is fetched
     * @see com.datastax.driver.core.ResultSet#fetchMoreResults()
     */
    def fetchMoreResultsL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.fetchMoreResults(handler)
      }.map(_ => ())
  }


  implicit class VertxCassandraRowStreamOps(val target: CassandraRowStream) extends AnyVal {

    def pipeToL(dst: WriteStream[Row]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxCassandraClientOps(val target: CassandraClient) extends AnyVal {
    /**
     *  Execute the query and provide a handler for consuming results.
     * @param resultHandler handler called when result of execution is present, but can be not fully fetched
     * @param query the query to execute
     * @return current Cassandra client instance
     */
    def executeL(query: String): Task[ResultSet] =
      Task.handle[ResultSet] { resultHandler =>
        target.execute(query, resultHandler)
      }

    /**
     *  Executes the given SQL <code>SELECT</code> statement which returns the results of the query as a read stream.
     * @param sql the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param rowStreamHandler the handler which is called once the operation completes. It will return an instance of {@link CassandraRowStream}.
     * @return current Cassandra client instance
     */
    def queryStreamL(sql: String): Task[CassandraRowStream] =
      Task.handle[CassandraRowStream] { rowStreamHandler =>
        target.queryStream(sql, rowStreamHandler)
      }

    /**
     *  Closes this client.
     * @param closeHandler handler called when client is closed
     * @return current Cassandra client instance
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { closeHandler =>
        target.close(closeHandler)
      }.map(_ => ())
  }


  implicit class VertxMapperOps[T](val target: Mapper[T])  {
    /**
     *  Asynchronous save method.
     * @param entity object to be stored in database
     * @param handler result handler
     */
    def saveL(entity: T): Task[Unit] =
      Task.handle[Void] { handler =>
        target.save(entity, handler)
      }.map(_ => ())

    /**
     *  Asynchronous delete method based on the column values of the primary key.
     * @param primaryKey primary key used to find row to delete
     * @param handler result handler
     */
    def deleteL(primaryKey: List[Object]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.delete(primaryKey, handler)
      }.map(_ => ())

    /**
     *  Asynchronous get method based on the column values of the primary key.
     * @param primaryKey primary key used to retrieve row
     * @param handler result handler
     */
    def getL(primaryKey: List[Object]): Task[T] =
      Task.handle[T] { handler =>
        target.get(primaryKey, handler)
      }
  }


}