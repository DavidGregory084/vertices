package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.http.HttpClient
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.client.WebClient
import io.vertx.redis.RedisClient
import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.ServiceDiscoveryOptions
import io.vertx.servicediscovery.ServiceReference
import io.vertx.servicediscovery.spi.ServiceExporter
import io.vertx.servicediscovery.spi.ServiceImporter
import io.vertx.servicediscovery.spi.ServicePublisher
import io.vertx.servicediscovery.spi.ServiceType
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.servicediscovery.types.JDBCDataSource
import io.vertx.servicediscovery.types.MessageSource
import io.vertx.servicediscovery.types.MongoDataSource
import io.vertx.servicediscovery.types.RedisDataSource
import java.lang.Boolean
import java.lang.Object
import java.lang.String
import java.lang.Void
import java.util.List
import java.util.Set
import java.util.function.Function

package object servicediscovery {

  implicit class VertxJDBCDataSourceCompanionOps(val target: JDBCDataSource.type) {
    /**
     *  Convenient method that looks for a JDBC datasource source and provides the configured {@link io.vertx.ext.jdbc.JDBCClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param resultHandler The result handler
     */
    def getJDBCClientL(discovery: ServiceDiscovery, filter: JsonObject): Task[JDBCClient] =
      Task.handle[JDBCClient] { resultHandler =>
        JDBCDataSource.getJDBCClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a JDBC datasource source and provides the configured {@link io.vertx.ext.jdbc.JDBCClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter (must not be {@code null})
     * @param resultHandler The result handler
     */
    def getJDBCClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean]): Task[JDBCClient] =
      Task.handle[JDBCClient] { resultHandler =>
        JDBCDataSource.getJDBCClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a JDBC datasource source and provides the configured {@link io.vertx.ext.jdbc.JDBCClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery             The service discovery instance
     * @param filter                The filter, optional
     * @param consumerConfiguration the consumer configuration
     * @param resultHandler         the result handler
     */
    def getJDBCClientL(discovery: ServiceDiscovery, filter: JsonObject, consumerConfiguration: JsonObject): Task[JDBCClient] =
      Task.handle[JDBCClient] { resultHandler =>
        JDBCDataSource.getJDBCClient(discovery, filter, consumerConfiguration, resultHandler)
      }

    /**
     *  Convenient method that looks for a JDBC datasource source and provides the configured {@link io.vertx.ext.jdbc.JDBCClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery             The service discovery instance
     * @param filter                The filter, must not be {@code null}
     * @param consumerConfiguration the consumer configuration
     * @param resultHandler         the result handler
     */
    def getJDBCClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean], consumerConfiguration: JsonObject): Task[JDBCClient] =
      Task.handle[JDBCClient] { resultHandler =>
        JDBCDataSource.getJDBCClient(discovery, filter, consumerConfiguration, resultHandler)
      }
  }


  implicit class VertxHttpEndpointCompanionOps(val target: HttpEndpoint.type) {
    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link HttpClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param resultHandler The result handler
     */
    def getClientL(discovery: ServiceDiscovery, filter: JsonObject): Task[HttpClient] =
      Task.handle[HttpClient] { resultHandler =>
        HttpEndpoint.getClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@linkWebClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param resultHandler The result handler
     */
    def getWebClientL(discovery: ServiceDiscovery, filter: JsonObject): Task[WebClient] =
      Task.handle[WebClient] { resultHandler =>
        HttpEndpoint.getWebClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link HttpClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails. This method accepts a
     *  configuration for the HTTP client
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param conf          the configuration of the client
     * @param resultHandler The result handler
     */
    def getClientL(discovery: ServiceDiscovery, filter: JsonObject, conf: JsonObject): Task[HttpClient] =
      Task.handle[HttpClient] { resultHandler =>
        HttpEndpoint.getClient(discovery, filter, conf, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link WebClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails. This method accepts a
     *  configuration for the HTTP client
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param conf          the configuration of the client
     * @param resultHandler The result handler
     */
    def getWebClientL(discovery: ServiceDiscovery, filter: JsonObject, conf: JsonObject): Task[WebClient] =
      Task.handle[WebClient] { resultHandler =>
        HttpEndpoint.getWebClient(discovery, filter, conf, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link HttpClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter
     * @param resultHandler The result handler
     */
    def getClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean]): Task[HttpClient] =
      Task.handle[HttpClient] { resultHandler =>
        HttpEndpoint.getClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link WebClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter
     * @param resultHandler The result handler
     */
    def getWebClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean]): Task[WebClient] =
      Task.handle[WebClient] { resultHandler =>
        HttpEndpoint.getWebClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link HttpClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails. This method accepts a
     *  configuration for the HTTP client.
     * @param discovery     The service discovery instance
     * @param filter        The filter
     * @param conf          the configuration of the client
     * @param resultHandler The result handler
     */
    def getClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean], conf: JsonObject): Task[HttpClient] =
      Task.handle[HttpClient] { resultHandler =>
        HttpEndpoint.getClient(discovery, filter, conf, resultHandler)
      }

    /**
     *  Convenient method that looks for a HTTP endpoint and provides the configured {@link WebClient}. The async result
     *  is marked as failed is there are no matching services, or if the lookup fails. This method accepts a
     *  configuration for the HTTP client.
     * @param discovery     The service discovery instance
     * @param filter        The filter
     * @param conf          the configuration of the client
     * @param resultHandler The result handler
     */
    def getWebClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean], conf: JsonObject): Task[WebClient] =
      Task.handle[WebClient] { resultHandler =>
        HttpEndpoint.getWebClient(discovery, filter, conf, resultHandler)
      }
  }


  implicit class VertxMessageSourceCompanionOps(val target: MessageSource.type) {
    /**
     *  Convenient method that looks for a message source and provides the configured {@link MessageConsumer}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param resultHandler The result handler
     * @param <T>           The class of the message
     */
    def getConsumerL[T](discovery: ServiceDiscovery, filter: JsonObject): Task[MessageConsumer[T]] =
      Task.handle[MessageConsumer[T]] { resultHandler =>
        MessageSource.getConsumer(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a message source and provides the configured {@link MessageConsumer}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, must not be {@code null}
     * @param resultHandler The result handler
     * @param <T>           The class of the message
     */
    def getConsumerL[T](discovery: ServiceDiscovery, filter: Function[Record,Boolean]): Task[MessageConsumer[T]] =
      Task.handle[MessageConsumer[T]] { resultHandler =>
        MessageSource.getConsumer(discovery, filter, resultHandler)
      }
  }


  implicit class VertxRedisDataSourceCompanionOps(val target: RedisDataSource.type) {
    /**
     *  Convenient method that looks for a Redis data source and provides the configured {@link io.vertx.redis.RedisClient}.
     *  The async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param resultHandler The result handler
     */
    def getRedisClientL(discovery: ServiceDiscovery, filter: JsonObject): Task[RedisClient] =
      Task.handle[RedisClient] { resultHandler =>
        RedisDataSource.getRedisClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a Redis data source and provides the configured {@link io.vertx.redis.RedisClient}.
     *  The async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, cannot be {@code null}
     * @param resultHandler The result handler
     */
    def getRedisClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean]): Task[RedisClient] =
      Task.handle[RedisClient] { resultHandler =>
        RedisDataSource.getRedisClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a Redis data source and provides the configured {@link io.vertx.redis.RedisClient}.
     *  The async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery             The service discovery instance
     * @param filter                The filter, optional
     * @param consumerConfiguration The additional consumer configuration
     * @param resultHandler         The result handler
     */
    def getRedisClientL(discovery: ServiceDiscovery, filter: JsonObject, consumerConfiguration: JsonObject): Task[RedisClient] =
      Task.handle[RedisClient] { resultHandler =>
        RedisDataSource.getRedisClient(discovery, filter, consumerConfiguration, resultHandler)
      }

    /**
     *  Convenient method that looks for a Redis data source and provides the configured {@link io.vertx.redis.RedisClient}.
     *  The async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery             The service discovery instance
     * @param filter                The filter, cannot be {@code null}
     * @param consumerConfiguration The additional consumer configuration
     * @param resultHandler         The result handler
     */
    def getRedisClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean], consumerConfiguration: JsonObject): Task[RedisClient] =
      Task.handle[RedisClient] { resultHandler =>
        RedisDataSource.getRedisClient(discovery, filter, consumerConfiguration, resultHandler)
      }
  }

  implicit class VertxServiceDiscoveryOps(val target: ServiceDiscovery) extends AnyVal {
    /**
     *  Registers a discovery service importer. Importers let you integrate other discovery technologies in this service
     *  discovery.
     * @param importer          the service importer
     * @param configuration     the optional configuration
     * @param completionHandler handler call when the importer has finished its initialization and
     *                           initial imports
     * @return the current {@link ServiceDiscovery}
     */
    def registerServiceImporterL(importer: ServiceImporter, configuration: JsonObject): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.registerServiceImporter(importer, configuration, completionHandler)
      }.map(_ => ())

    /**
     *  Registers a discovery bridge. Exporters let you integrate other discovery technologies in this service
     *  discovery.
     * @param exporter          the service exporter
     * @param configuration     the optional configuration
     * @param completionHandler handler notified when the exporter has been correctly initialized.
     * @return the current {@link ServiceDiscovery}
     */
    def registerServiceExporterL(exporter: ServiceExporter, configuration: JsonObject): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.registerServiceExporter(exporter, configuration, completionHandler)
      }.map(_ => ())

    /**
     *  Publishes a record.
     * @param record        the record
     * @param resultHandler handler called when the operation has completed (successfully or not). In case of success,
     *                       the passed record has a registration id required to modify and un-register the service.
     */
    def publishL(record: Record): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.publish(record, resultHandler)
      }

    /**
     *  Un-publishes a record.
     * @param id            the registration id
     * @param resultHandler handler called when the operation has completed (successfully or not).
     */
    def unpublishL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.unpublish(id, resultHandler)
      }.map(_ => ())

    /**
     *  Lookups for a single record.
     *  <p>
     *  Filters are expressed using a Json object. Each entry of the given filter will be checked against the record.
     *  All entry must match exactly the record. The entry can use the special "*" value to denotes a requirement on the
     *  key, but not on the value.
     *  <p>
     *  Let's take some example:
     *  <pre>
     *    { "name" = "a" } => matches records with name set fo "a"
     *    { "color" = "*" } => matches records with "color" set
     *    { "color" = "red" } => only matches records with "color" set to "red"
     *    { "color" = "red", "name" = "a"} => only matches records with name set to "a", and color set to "red"
     *  </pre>
     *  <p>
     *  If the filter is not set ({@code null} or empty), it accepts all records.
     *  <p>
     *  This method returns the first matching record.
     * @param filter        the filter.
     * @param resultHandler handler called when the lookup has been completed. When there are no matching record, the
     *                       operation succeeds, but the async result has no result ({@code null}).
     */
    def getRecordL(filter: JsonObject): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.getRecord(filter, resultHandler)
      }

    /**
     *  Lookups for a single record.
     *  <p>
     *  The filter is a {@link Function} taking a {@link Record} as argument and returning a boolean. You should see it
     *  as an {@code accept} method of a filter. This method return a record passing the filter.
     *  <p>
     *  This method only looks for records with a {@code UP} status.
     * @param filter        the filter, must not be {@code null}. To return all records, use a function accepting all records
     * @param resultHandler the result handler called when the lookup has been completed. When there are no matching
     *                       record, the operation succeed, but the async result has no result.
     */
    def getRecordL(filter: Function[Record,Boolean]): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.getRecord(filter, resultHandler)
      }

    /**
     *  Lookups for a single record.
     *  <p>
     *  The filter is a {@link Function} taking a {@link Record} as argument and returning a boolean. You should see it
     *  as an {@code accept} method of a filter. This method return a record passing the filter.
     *  <p>
     *  Unlike {@link #getRecord(Function, Handler)}, this method may accept records with a {@code OUT OF SERVICE}
     *  status, if the {@code includeOutOfService} parameter is set to {@code true}.
     * @param filter              the filter, must not be {@code null}. To return all records, use a function accepting all records
     * @param includeOutOfService whether or not the filter accepts  {@code OUT OF SERVICE} records
     * @param resultHandler       the result handler called when the lookup has been completed. When there are no matching
     *                             record, the operation succeed, but the async result has no result.
     */
    def getRecordL(filter: Function[Record,Boolean], includeOutOfService: Boolean): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.getRecord(filter, includeOutOfService, resultHandler)
      }

    /**
     *  Lookups for a set of records. Unlike {@link #getRecord(JsonObject, Handler)}, this method returns all matching
     *  records.
     * @param filter        the filter - see {@link #getRecord(JsonObject, Handler)}
     * @param resultHandler handler called when the lookup has been completed. When there are no matching record, the
     *                       operation succeed, but the async result has an empty list as result.
     */
    def getRecordsL(filter: JsonObject): Task[List[Record]] =
      Task.handle[List[Record]] { resultHandler =>
        target.getRecords(filter, resultHandler)
      }

    /**
     *  Lookups for a set of records. Unlike {@link #getRecord(Function, Handler)}, this method returns all matching
     *  records.
     *  <p>
     *  The filter is a {@link Function} taking a {@link Record} as argument and returning a boolean. You should see it
     *  as an {@code accept} method of a filter. This method return a record passing the filter.
     *  <p>
     *  This method only looks for records with a {@code UP} status.
     * @param filter        the filter, must not be {@code null}. To return all records, use a function accepting all records
     * @param resultHandler handler called when the lookup has been completed. When there are no matching record, the
     *                       operation succeed, but the async result has an empty list as result.
     */
    def getRecordsL(filter: Function[Record,Boolean]): Task[List[Record]] =
      Task.handle[List[Record]] { resultHandler =>
        target.getRecords(filter, resultHandler)
      }

    /**
     *  Lookups for a set of records. Unlike {@link #getRecord(Function, Handler)}, this method returns all matching
     *  records.
     *  <p>
     *  The filter is a {@link Function} taking a {@link Record} as argument and returning a boolean. You should see it
     *  as an {@code accept} method of a filter. This method return a record passing the filter.
     *  <p>
     *  Unlike {@link #getRecords(Function, Handler)}, this method may accept records with a {@code OUT OF SERVICE}
     *  status, if the {@code includeOutOfService} parameter is set to {@code true}.
     * @param filter              the filter, must not be {@code null}. To return all records, use a function accepting all records
     * @param includeOutOfService whether or not the filter accepts  {@code OUT OF SERVICE} records
     * @param resultHandler       handler called when the lookup has been completed. When there are no matching record, the
     *                             operation succeed, but the async result has an empty list as result.
     */
    def getRecordsL(filter: Function[Record,Boolean], includeOutOfService: Boolean): Task[List[Record]] =
      Task.handle[List[Record]] { resultHandler =>
        target.getRecords(filter, includeOutOfService, resultHandler)
      }

    /**
     *  Updates the given record. The record must has been published, and has it's registration id set.
     * @param record        the updated record
     * @param resultHandler handler called when the lookup has been completed.
     */
    def updateL(record: Record): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.update(record, resultHandler)
      }
  }



  implicit class VertxMongoDataSourceCompanionOps(val target: MongoDataSource.type) {
    /**
     *  Convenient method that looks for a Mongo datasource source and provides the configured {@link io.vertx.ext.mongo.MongoClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter, optional
     * @param resultHandler The result handler
     */
    def getMongoClientL(discovery: ServiceDiscovery, filter: JsonObject): Task[MongoClient] =
      Task.handle[MongoClient] { resultHandler =>
        MongoDataSource.getMongoClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a Mongo datasource source and provides the configured
     *  {@link io.vertx.ext.mongo.MongoClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery     The service discovery instance
     * @param filter        The filter
     * @param resultHandler The result handler
     */
    def getMongoClientL(discovery: ServiceDiscovery, filter: Function[Record,Boolean]): Task[MongoClient] =
      Task.handle[MongoClient] { resultHandler =>
        MongoDataSource.getMongoClient(discovery, filter, resultHandler)
      }

    /**
     *  Convenient method that looks for a Mongo datasource source and provides the configured {@link io.vertx.ext.mongo.MongoClient}. The
     *  async result is marked as failed is there are no matching services, or if the lookup fails.
     * @param discovery             The service discovery instance
     * @param filter                The filter, optional
     * @param consumerConfiguration the consumer configuration
     * @param resultHandler         the result handler
     */
    def getMongoClientL(discovery: ServiceDiscovery, filter: JsonObject, consumerConfiguration: JsonObject): Task[MongoClient] =
      Task.handle[MongoClient] { resultHandler =>
        MongoDataSource.getMongoClient(discovery, filter, consumerConfiguration, resultHandler)
      }
  }

  implicit class VertxServicePublisherOps(val target: ServicePublisher) extends AnyVal {
    /**
     *  Publishes a record.
     * @param record        the record
     * @param resultHandler handler called when the operation has completed (successfully or not). In case of success,
     *                       the passed record has a registration id required to modify and un-register the service.
     */
    def publishL(record: Record): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.publish(record, resultHandler)
      }

    /**
     *  Un-publishes a record.
     * @param id            the registration id
     * @param resultHandler handler called when the operation has completed (successfully or not).
     */
    def unpublishL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.unpublish(id, resultHandler)
      }.map(_ => ())

    /**
     *  Updates an existing record.
     * @param record        the record
     * @param resultHandler handler called when the operation has completed (successfully or not). In case of success,
     *                       the passed record has a registration id required to modify and un-register the service.
     */
    def updateL(record: Record): Task[Record] =
      Task.handle[Record] { resultHandler =>
        target.update(record, resultHandler)
      }
  }


}