package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.sql.SQLOperations
import io.vertx.ext.sql.SQLOptions
import io.vertx.ext.sql.SQLRowStream
import io.vertx.ext.sql.TransactionIsolation
import io.vertx.ext.sql.UpdateResult
import java.io.Closeable
import java.lang.Integer
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List

package object sql {
  implicit class VertxSQLRowStreamOps(val target: SQLRowStream) extends AnyVal {

    def pipeToL(dst: WriteStream[JsonArray]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Closes the stream/underlying cursor(s). The actual close happens asynchronously.
     * @param handler called when the stream/underlying cursor(s) is(are) closed
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())
  }


  implicit class VertxSQLConnectionOps(val target: SQLConnection) extends AnyVal {
    /**
     *  Execute a one shot SQL statement that returns a single SQL row. This method will reduce the boilerplate code by
     *  getting a connection from the pool (this object) and return it back after the execution. Only the first result
     *  from the result set is returned.
     * @param sql     the statement to execute
     * @param handler the result handler
     * @return self
     */
    def querySingleL(sql: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.querySingle(sql, handler)
      }

    /**
     *  Execute a one shot SQL statement with arguments that returns a single SQL row. This method will reduce the
     *  boilerplate code by getting a connection from the pool (this object) and return it back after the execution.
     *  Only the first result from the result set is returned.
     * @param sql       the statement to execute
     * @param arguments the arguments
     * @param handler   the result handler
     * @return self
     */
    def querySingleWithParamsL(sql: String, arguments: JsonArray): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.querySingleWithParams(sql, arguments, handler)
      }

    /**
     *  Sets the auto commit flag for this connection. True by default.
     * @param autoCommit  the autoCommit flag, true by default.
     * @param resultHandler  the handler which is called once this operation completes.
     * @see java.sql.Connection#setAutoCommit(boolean)
     */
    def setAutoCommitL(autoCommit: Boolean): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.setAutoCommit(autoCommit, resultHandler)
      }.map(_ => ())

    /**
     *  Executes the given SQL statement
     * @param sql  the SQL to execute. For example <code>CREATE TABLE IF EXISTS table ...</code>
     * @param resultHandler  the handler which is called once this operation completes.
     * @see java.sql.Statement#execute(String)
     */
    def executeL(sql: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.execute(sql, resultHandler)
      }.map(_ => ())

    /**
     *  Executes the given SQL <code>SELECT</code> statement which returns the results of the query.
     * @param sql  the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param resultHandler  the handler which is called once the operation completes. It will return a {@code ResultSet}.
     * @see java.sql.Statement#executeQuery(String)
     * @see java.sql.PreparedStatement#executeQuery(String)
     */
    def queryL(sql: String): Task[ResultSet] =
      Task.handle[ResultSet] { resultHandler =>
        target.query(sql, resultHandler)
      }

    /**
     *  Executes the given SQL <code>SELECT</code> statement which returns the results of the query as a read stream.
     * @param sql  the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param handler  the handler which is called once the operation completes. It will return a {@code SQLRowStream}.
     * @see java.sql.Statement#executeQuery(String)
     * @see java.sql.PreparedStatement#executeQuery(String)
     */
    def queryStreamL(sql: String): Task[SQLRowStream] =
      Task.handle[SQLRowStream] { handler =>
        target.queryStream(sql, handler)
      }

    /**
     *  Executes the given SQL <code>SELECT</code> prepared statement which returns the results of the query.
     * @param sql  the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param params  these are the parameters to fill the statement.
     * @param resultHandler  the handler which is called once the operation completes. It will return a {@code ResultSet}.
     * @see java.sql.Statement#executeQuery(String)
     * @see java.sql.PreparedStatement#executeQuery(String)
     */
    def queryWithParamsL(sql: String, params: JsonArray): Task[ResultSet] =
      Task.handle[ResultSet] { resultHandler =>
        target.queryWithParams(sql, params, resultHandler)
      }

    /**
     *  Executes the given SQL <code>SELECT</code> statement which returns the results of the query as a read stream.
     * @param sql  the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param params  these are the parameters to fill the statement.
     * @param handler  the handler which is called once the operation completes. It will return a {@code SQLRowStream}.
     * @see java.sql.Statement#executeQuery(String)
     * @see java.sql.PreparedStatement#executeQuery(String)
     */
    def queryStreamWithParamsL(sql: String, params: JsonArray): Task[SQLRowStream] =
      Task.handle[SQLRowStream] { handler =>
        target.queryStreamWithParams(sql, params, handler)
      }

    /**
     *  Executes the given SQL statement which may be an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code>
     *  statement.
     * @param sql  the SQL to execute. For example <code>INSERT INTO table ...</code>
     * @param resultHandler  the handler which is called once the operation completes.
     * @see java.sql.Statement#executeUpdate(String)
     * @see java.sql.PreparedStatement#executeUpdate(String)
     */
    def updateL(sql: String): Task[UpdateResult] =
      Task.handle[UpdateResult] { resultHandler =>
        target.update(sql, resultHandler)
      }

    /**
     *  Executes the given prepared statement which may be an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code>
     *  statement with the given parameters
     * @param sql  the SQL to execute. For example <code>INSERT INTO table ...</code>
     * @param params  these are the parameters to fill the statement.
     * @param resultHandler  the handler which is called once the operation completes.
     * @see java.sql.Statement#executeUpdate(String)
     * @see java.sql.PreparedStatement#executeUpdate(String)
     */
    def updateWithParamsL(sql: String, params: JsonArray): Task[UpdateResult] =
      Task.handle[UpdateResult] { resultHandler =>
        target.updateWithParams(sql, params, resultHandler)
      }

    /**
     *  Calls the given SQL <code>PROCEDURE</code> which returns the result from the procedure.
     * @param sql  the SQL to execute. For example <code>{call getEmpName}</code>.
     * @param resultHandler  the handler which is called once the operation completes. It will return a {@code ResultSet}.
     * @see java.sql.CallableStatement#execute(String)
     */
    def callL(sql: String): Task[ResultSet] =
      Task.handle[ResultSet] { resultHandler =>
        target.call(sql, resultHandler)
      }

    /**
     *  Calls the given SQL <code>PROCEDURE</code> which returns the result from the procedure.
     * 
     *  The index of params and outputs are important for both arrays, for example when dealing with a prodecure that
     *  takes the first 2 arguments as input values and the 3 arg as an output then the arrays should be like:
     * 
     *  <pre>
     *    params = [VALUE1, VALUE2, null]
     *    outputs = [null, null, "VARCHAR"]
     *  </pre>
     * @param sql  the SQL to execute. For example <code>{call getEmpName (?, ?)}</code>.
     * @param params  these are the parameters to fill the statement.
     * @param outputs  these are the outputs to fill the statement.
     * @param resultHandler  the handler which is called once the operation completes. It will return a {@code ResultSet}.
     * @see java.sql.CallableStatement#execute(String)
     */
    def callWithParamsL(sql: String, params: JsonArray, outputs: JsonArray): Task[ResultSet] =
      Task.handle[ResultSet] { resultHandler =>
        target.callWithParams(sql, params, outputs, resultHandler)
      }

    /**
     *  Closes the connection. Important to always close the connection when you are done so it's returned to the pool.
     * @param handler the handler called when this operation completes.
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Commits all changes made since the previous commit/rollback.
     * @param handler the handler called when this operation completes.
     */
    def commitL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.commit(handler)
      }.map(_ => ())

    /**
     *  Rolls back all changes made since the previous commit/rollback.
     * @param handler the handler called when this operation completes.
     */
    def rollbackL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.rollback(handler)
      }.map(_ => ())

    /**
     *  Batch simple SQL strings and execute the batch where the async result contains a array of Integers.
     * @param sqlStatements sql statement
     * @param handler the result handler
     */
    def batchL(sqlStatements: List[String]): Task[List[Integer]] =
      Task.handle[List[Integer]] { handler =>
        target.batch(sqlStatements, handler)
      }

    /**
     *  Batch a prepared statement with all entries from the args list. Each entry is a batch.
     *  The operation completes with the execution of the batch where the async result contains a array of Integers.
     * @param sqlStatement sql statement
     * @param args the prepared statement arguments
     * @param handler the result handler
     */
    def batchWithParamsL(sqlStatement: String, args: List[JsonArray]): Task[List[Integer]] =
      Task.handle[List[Integer]] { handler =>
        target.batchWithParams(sqlStatement, args, handler)
      }

    /**
     *  Batch a callable statement with all entries from the args list. Each entry is a batch.
     *  The size of the lists inArgs and outArgs MUST be the equal.
     *  The operation completes with the execution of the batch where the async result contains a array of Integers.
     * @param sqlStatement sql statement
     * @param inArgs the callable statement input arguments
     * @param outArgs the callable statement output arguments
     * @param handler the result handler
     */
    def batchCallableWithParamsL(sqlStatement: String, inArgs: List[JsonArray], outArgs: List[JsonArray]): Task[List[Integer]] =
      Task.handle[List[Integer]] { handler =>
        target.batchCallableWithParams(sqlStatement, inArgs, outArgs, handler)
      }

    /**
     *  Attempts to change the transaction isolation level for this Connection object to the one given.
     * 
     *  The constants defined in the interface Connection are the possible transaction isolation levels.
     * @param isolation the level of isolation
     * @param handler the handler called when this operation completes.
     */
    def setTransactionIsolationL(isolation: TransactionIsolation): Task[Unit] =
      Task.handle[Void] { handler =>
        target.setTransactionIsolation(isolation, handler)
      }.map(_ => ())

    /**
     *  Attempts to return the transaction isolation level for this Connection object to the one given.
     * @param handler the handler called when this operation completes.
     */
    def getTransactionIsolationL(): Task[TransactionIsolation] =
      Task.handle[TransactionIsolation] { handler =>
        target.getTransactionIsolation(handler)
      }
  }


  implicit class VertxSQLClientOps(val target: SQLClient) extends AnyVal {
    /**
     *  Execute a one shot SQL statement that returns a single SQL row. This method will reduce the boilerplate code by
     *  getting a connection from the pool (this object) and return it back after the execution. Only the first result
     *  from the result set is returned.
     * @param sql     the statement to execute
     * @param handler the result handler
     * @return self
     */
    def querySingleL(sql: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.querySingle(sql, handler)
      }

    /**
     *  Execute a one shot SQL statement with arguments that returns a single SQL row. This method will reduce the
     *  boilerplate code by getting a connection from the pool (this object) and return it back after the execution.
     *  Only the first result from the result set is returned.
     * @param sql       the statement to execute
     * @param arguments the arguments
     * @param handler   the result handler
     * @return self
     */
    def querySingleWithParamsL(sql: String, arguments: JsonArray): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.querySingleWithParams(sql, arguments, handler)
      }

    /**
     *  Returns a connection that can be used to perform SQL operations on. It's important to remember
     *  to close the connection when you are done, so it is returned to the pool.
     * @param handler the handler which is called when the <code>JdbcConnection</code> object is ready for use.
     */
    def getConnectionL(): Task[SQLConnection] =
      Task.handle[SQLConnection] { handler =>
        target.getConnection(handler)
      }

    /**
     *  Close the client and release all resources.
     *  Call the handler when close is complete.
     * @param handler the handler that will be called when close is complete
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Execute a single SQL statement, this method acquires a connection from the the pool and executes the SQL
     *  statement and returns it back after the execution.
     * @param sql     the statement to execute
     * @param handler the result handler
     * @return self
     */
    def queryL(sql: String): Task[ResultSet] =
      Task.handle[ResultSet] { handler =>
        target.query(sql, handler)
      }

    /**
     *  Executes the given SQL <code>SELECT</code> statement which returns the results of the query as a read stream.
     * @param sql  the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param handler  the handler which is called once the operation completes. It will return a {@code SQLRowStream}.
     * @see java.sql.Statement#executeQuery(String)
     * @see java.sql.PreparedStatement#executeQuery(String)
     */
    def queryStreamL(sql: String): Task[SQLRowStream] =
      Task.handle[SQLRowStream] { handler =>
        target.queryStream(sql, handler)
      }

    /**
     *  Executes the given SQL <code>SELECT</code> statement which returns the results of the query as a read stream.
     * @param sql  the SQL to execute. For example <code>SELECT * FROM table ...</code>.
     * @param params  these are the parameters to fill the statement.
     * @param handler  the handler which is called once the operation completes. It will return a {@code SQLRowStream}.
     * @see java.sql.Statement#executeQuery(String)
     * @see java.sql.PreparedStatement#executeQuery(String)
     */
    def queryStreamWithParamsL(sql: String, params: JsonArray): Task[SQLRowStream] =
      Task.handle[SQLRowStream] { handler =>
        target.queryStreamWithParams(sql, params, handler)
      }

    /**
     *  Execute a single SQL prepared statement, this method acquires a connection from the the pool and executes the SQL
     *  prepared statement and returns it back after the execution.
     * @param sql       the statement to execute
     * @param arguments the arguments to the statement
     * @param handler   the result handler
     * @return self
     */
    def queryWithParamsL(sql: String, arguments: JsonArray): Task[ResultSet] =
      Task.handle[ResultSet] { handler =>
        target.queryWithParams(sql, arguments, handler)
      }

    /**
     *  Executes the given SQL statement which may be an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code>
     *  statement.
     * @param sql  the SQL to execute. For example <code>INSERT INTO table ...</code>
     * @param handler  the handler which is called once the operation completes.
     * @see java.sql.Statement#executeUpdate(String)
     * @see java.sql.PreparedStatement#executeUpdate(String)
     */
    def updateL(sql: String): Task[UpdateResult] =
      Task.handle[UpdateResult] { handler =>
        target.update(sql, handler)
      }

    /**
     *  Executes the given prepared statement which may be an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code>
     *  statement with the given parameters
     * @param sql  the SQL to execute. For example <code>INSERT INTO table ...</code>
     * @param params  these are the parameters to fill the statement.
     * @param handler  the handler which is called once the operation completes.
     * @see java.sql.Statement#executeUpdate(String)
     * @see java.sql.PreparedStatement#executeUpdate(String)
     */
    def updateWithParamsL(sql: String, params: JsonArray): Task[UpdateResult] =
      Task.handle[UpdateResult] { handler =>
        target.updateWithParams(sql, params, handler)
      }

    /**
     *  Calls the given SQL <code>PROCEDURE</code> which returns the result from the procedure.
     * @param sql  the SQL to execute. For example <code>{call getEmpName}</code>.
     * @param handler  the handler which is called once the operation completes. It will return a {@code ResultSet}.
     * @see java.sql.CallableStatement#execute(String)
     */
    def callL(sql: String): Task[ResultSet] =
      Task.handle[ResultSet] { handler =>
        target.call(sql, handler)
      }

    /**
     *  Calls the given SQL <code>PROCEDURE</code> which returns the result from the procedure.
     * 
     *  The index of params and outputs are important for both arrays, for example when dealing with a prodecure that
     *  takes the first 2 arguments as input values and the 3 arg as an output then the arrays should be like:
     * 
     *  <pre>
     *    params = [VALUE1, VALUE2, null]
     *    outputs = [null, null, "VARCHAR"]
     *  </pre>
     * @param sql  the SQL to execute. For example <code>{call getEmpName (?, ?)}</code>.
     * @param params  these are the parameters to fill the statement.
     * @param outputs  these are the outputs to fill the statement.
     * @param handler  the handler which is called once the operation completes. It will return a {@code ResultSet}.
     * @see java.sql.CallableStatement#execute(String)
     */
    def callWithParamsL(sql: String, params: JsonArray, outputs: JsonArray): Task[ResultSet] =
      Task.handle[ResultSet] { handler =>
        target.callWithParams(sql, params, outputs, handler)
      }
  }


}