package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.consul.AclToken
import io.vertx.ext.consul.BlockingQueryOptions
import io.vertx.ext.consul.Check
import io.vertx.ext.consul.CheckList
import io.vertx.ext.consul.CheckOptions
import io.vertx.ext.consul.CheckQueryOptions
import io.vertx.ext.consul.CheckStatus
import io.vertx.ext.consul.ConsulClient
import io.vertx.ext.consul.ConsulClientOptions
import io.vertx.ext.consul.CoordinateList
import io.vertx.ext.consul.DcCoordinates
import io.vertx.ext.consul.Event
import io.vertx.ext.consul.EventList
import io.vertx.ext.consul.EventListOptions
import io.vertx.ext.consul.EventOptions
import io.vertx.ext.consul.HealthState
import io.vertx.ext.consul.KeyValue
import io.vertx.ext.consul.KeyValueList
import io.vertx.ext.consul.KeyValueOptions
import io.vertx.ext.consul.MaintenanceOptions
import io.vertx.ext.consul.NodeList
import io.vertx.ext.consul.NodeQueryOptions
import io.vertx.ext.consul.PreparedQueryDefinition
import io.vertx.ext.consul.PreparedQueryExecuteOptions
import io.vertx.ext.consul.PreparedQueryExecuteResponse
import io.vertx.ext.consul.Service
import io.vertx.ext.consul.ServiceEntryList
import io.vertx.ext.consul.ServiceList
import io.vertx.ext.consul.ServiceOptions
import io.vertx.ext.consul.ServiceQueryOptions
import io.vertx.ext.consul.Session
import io.vertx.ext.consul.SessionList
import io.vertx.ext.consul.SessionOptions
import io.vertx.ext.consul.TxnRequest
import io.vertx.ext.consul.TxnResponse
import java.lang.Boolean
import java.lang.String
import java.lang.Void
import java.util.List

package object consul {
  implicit class VertxConsulClientOps(val target: ConsulClient) extends AnyVal {
    /**
     *  Returns the configuration and member information of the local agent
     * @param resultHandler will be provided with the configuration and member information of the local agent
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent.html#read-configuration">/v1/agent/self</a> endpoint
     */
    def agentInfoL(): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.agentInfo(resultHandler)
      }

    /**
     *  Returns the LAN network coordinates for all nodes in a given DC
     * @param resultHandler will be provided with network coordinates of nodes in datacenter
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/coordinate.html#read-lan-coordinates">/v1/coordinate/nodes</a> endpoint
     */
    def coordinateNodesL(): Task[CoordinateList] =
      Task.handle[CoordinateList] { resultHandler =>
        target.coordinateNodes(resultHandler)
      }

    /**
     *  Returns the LAN network coordinates for all nodes in a given DC
     *  This is blocking query unlike {@link ConsulClient#coordinateNodes(Handler)}
     * @param options       the blocking options
     * @param resultHandler will be provided with network coordinates of nodes in datacenter
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/coordinate.html#read-lan-coordinates">/v1/coordinate/nodes</a> endpoint
     */
    def coordinateNodesWithOptionsL(options: BlockingQueryOptions): Task[CoordinateList] =
      Task.handle[CoordinateList] { resultHandler =>
        target.coordinateNodesWithOptions(options, resultHandler)
      }

    /**
     *  Returns the WAN network coordinates for all Consul servers, organized by DCs
     * @param resultHandler will be provided with network coordinates for all Consul servers
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/coordinate.html#read-wan-coordinates">/v1/coordinate/datacenters</a> endpoint
     */
    def coordinateDatacentersL(): Task[List[DcCoordinates]] =
      Task.handle[List[DcCoordinates]] { resultHandler =>
        target.coordinateDatacenters(resultHandler)
      }

    /**
     *  Returns the list of keys that corresponding to the specified key prefix.
     * @param keyPrefix     the prefix
     * @param resultHandler will be provided with keys list
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#read-key">/v1/kv/:key</a> endpoint
     */
    def getKeysL(keyPrefix: String): Task[List[String]] =
      Task.handle[List[String]] { resultHandler =>
        target.getKeys(keyPrefix, resultHandler)
      }

    /**
     *  Returns the list of keys that corresponding to the specified key prefix.
     * @param keyPrefix     the prefix
     * @param options       the blocking options
     * @param resultHandler will be provided with keys list
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#read-key">/v1/kv/:key</a> endpoint
     */
    def getKeysWithOptionsL(keyPrefix: String, options: BlockingQueryOptions): Task[List[String]] =
      Task.handle[List[String]] { resultHandler =>
        target.getKeysWithOptions(keyPrefix, options, resultHandler)
      }

    /**
     *  Returns key/value pair that corresponding to the specified key.
     *  An empty {@link KeyValue} object will be returned if no such key is found.
     * @param key           the key
     * @param resultHandler will be provided with key/value pair
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#read-key">/v1/kv/:key</a> endpoint
     */
    def getValueL(key: String): Task[KeyValue] =
      Task.handle[KeyValue] { resultHandler =>
        target.getValue(key, resultHandler)
      }

    /**
     *  Returns key/value pair that corresponding to the specified key.
     *  An empty {@link KeyValue} object will be returned if no such key is found.
     *  This is blocking query unlike {@link ConsulClient#getValue(String, Handler)}
     * @param key           the key
     * @param options       the blocking options
     * @param resultHandler will be provided with key/value pair
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#read-key">/v1/kv/:key</a> endpoint
     */
    def getValueWithOptionsL(key: String, options: BlockingQueryOptions): Task[KeyValue] =
      Task.handle[KeyValue] { resultHandler =>
        target.getValueWithOptions(key, options, resultHandler)
      }

    /**
     *  Remove the key/value pair that corresponding to the specified key
     * @param key           the key
     * @param resultHandler will be called on complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#delete-key">/v1/kv/:key</a> endpoint
     */
    def deleteValueL(key: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.deleteValue(key, resultHandler)
      }.map(_ => ())

    /**
     *  Returns the list of key/value pairs that corresponding to the specified key prefix.
     *  An empty {@link KeyValueList} object will be returned if no such key prefix is found.
     * @param keyPrefix     the prefix
     * @param resultHandler will be provided with list of key/value pairs
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#read-key">/v1/kv/:key</a> endpoint
     */
    def getValuesL(keyPrefix: String): Task[KeyValueList] =
      Task.handle[KeyValueList] { resultHandler =>
        target.getValues(keyPrefix, resultHandler)
      }

    /**
     *  Returns the list of key/value pairs that corresponding to the specified key prefix.
     *  An empty {@link KeyValueList} object will be returned if no such key prefix is found.
     *  This is blocking query unlike {@link ConsulClient#getValues(String, Handler)}
     * @param keyPrefix     the prefix
     * @param options       the blocking options
     * @param resultHandler will be provided with list of key/value pairs
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#read-key">/v1/kv/:key</a> endpoint
     */
    def getValuesWithOptionsL(keyPrefix: String, options: BlockingQueryOptions): Task[KeyValueList] =
      Task.handle[KeyValueList] { resultHandler =>
        target.getValuesWithOptions(keyPrefix, options, resultHandler)
      }

    /**
     *  Removes all the key/value pair that corresponding to the specified key prefix
     * @param keyPrefix     the prefix
     * @param resultHandler will be called on complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#delete-key">/v1/kv/:key</a> endpoint
     */
    def deleteValuesL(keyPrefix: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.deleteValues(keyPrefix, resultHandler)
      }.map(_ => ())

    /**
     *  Adds specified key/value pair
     * @param key           the key
     * @param value         the value
     * @param resultHandler will be provided with success of operation
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#create-update-key">/v1/kv/:key</a> endpoint
     */
    def putValueL(key: String, value: String): Task[Boolean] =
      Task.handle[java.lang.Boolean] { resultHandler =>
        target.putValue(key, value, resultHandler)
      }.map(out => out: Boolean)

    /**
     * 
     * @param key           the key
     * @param value         the value
     * @param options       options used to push pair
     * @param resultHandler will be provided with success of operation
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/kv.html#create-update-key">/v1/kv/:key</a> endpoint
     */
    def putValueWithOptionsL(key: String, value: String, options: KeyValueOptions): Task[Boolean] =
      Task.handle[java.lang.Boolean] { resultHandler =>
        target.putValueWithOptions(key, value, options, resultHandler)
      }.map(out => out: Boolean)

    /**
     *  Manages multiple operations inside a single, atomic transaction.
     * @param request       transaction request
     * @param resultHandler will be provided with result of transaction
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/txn.html">/v1/txn</a> endpoint
     */
    def transactionL(request: TxnRequest): Task[TxnResponse] =
      Task.handle[TxnResponse] { resultHandler =>
        target.transaction(request, resultHandler)
      }

    /**
     *  Create new Acl token
     * @param token     properties of the token
     * @param idHandler will be provided with ID of created token
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/acl.html#create-acl-token">/v1/acl/create</a> endpoint
     */
    def createAclTokenL(token: AclToken): Task[String] =
      Task.handle[String] { idHandler =>
        target.createAclToken(token, idHandler)
      }

    /**
     *  Update Acl token
     * @param token     properties of the token to be updated
     * @param idHandler will be provided with ID of updated
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/acl.html#update-acl-token">/v1/acl/update</a> endpoint
     */
    def updateAclTokenL(token: AclToken): Task[String] =
      Task.handle[String] { idHandler =>
        target.updateAclToken(token, idHandler)
      }

    /**
     *  Clone Acl token
     * @param id        the ID of token to be cloned
     * @param idHandler will be provided with ID of cloned token
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/acl.html#clone-acl-token">/v1/acl/clone/:uuid</a> endpoint
     */
    def cloneAclTokenL(id: String): Task[String] =
      Task.handle[String] { idHandler =>
        target.cloneAclToken(id, idHandler)
      }

    /**
     *  Get list of Acl token
     * @param resultHandler will be provided with list of tokens
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/acl.html#list-acls">/v1/acl/list</a> endpoint
     */
    def listAclTokensL(): Task[List[AclToken]] =
      Task.handle[List[AclToken]] { resultHandler =>
        target.listAclTokens(resultHandler)
      }

    /**
     *  Get info of Acl token
     * @param id           the ID of token
     * @param tokenHandler will be provided with token
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/acl.html#read-acl-token">/v1/acl/info/:uuid</a> endpoint
     */
    def infoAclTokenL(id: String): Task[AclToken] =
      Task.handle[AclToken] { tokenHandler =>
        target.infoAclToken(id, tokenHandler)
      }

    /**
     *  Destroy Acl token
     * @param id            the ID of token
     * @param resultHandler will be called on complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/acl.html#delete-acl-token">/v1/acl/destroy/:uuid</a> endpoint
     */
    def destroyAclTokenL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.destroyAclToken(id, resultHandler)
      }.map(_ => ())

    /**
     *  Fires a new user event
     * @param name          name of event
     * @param resultHandler will be provided with properties of event
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/event.html#fire-event">/v1/event/fire/:name</a> endpoint
     */
    def fireEventL(name: String): Task[Event] =
      Task.handle[Event] { resultHandler =>
        target.fireEvent(name, resultHandler)
      }

    /**
     *  Fires a new user event
     * @param name          name of event
     * @param options       options used to create event
     * @param resultHandler will be provided with properties of event
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/event.html#fire-event">/v1/event/fire/:name</a> endpoint
     */
    def fireEventWithOptionsL(name: String, options: EventOptions): Task[Event] =
      Task.handle[Event] { resultHandler =>
        target.fireEventWithOptions(name, options, resultHandler)
      }

    /**
     *  Returns the most recent events known by the agent
     * @param resultHandler will be provided with list of events
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/event.html#list-events">/v1/event/list</a> endpoint
     */
    def listEventsL(): Task[EventList] =
      Task.handle[EventList] { resultHandler =>
        target.listEvents(resultHandler)
      }

    /**
     *  Returns the most recent events known by the agent.
     *  This is blocking query unlike {@link ConsulClient#listEvents(Handler)}. However, the semantics of this endpoint
     *  are slightly different. Most blocking queries provide a monotonic index and block until a newer index is available.
     *  This can be supported as a consequence of the total ordering of the consensus protocol. With gossip,
     *  there is no ordering, and instead {@code X-Consul-Index} maps to the newest event that matches the query.
     *  <p>
     *  In practice, this means the index is only useful when used against a single agent and has no meaning globally.
     *  Because Consul defines the index as being opaque, clients should not be expecting a natural ordering either.
     * @param resultHandler will be provided with list of events
     * @param options       the blocking options
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/event.html#list-events">/v1/event/list</a> endpoint
     */
    def listEventsWithOptionsL(options: EventListOptions): Task[EventList] =
      Task.handle[EventList] { resultHandler =>
        target.listEventsWithOptions(options, resultHandler)
      }

    /**
     *  Adds a new service, with an optional health check, to the local agent.
     * @param serviceOptions the options of new service
     * @param resultHandler  will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/service.html#register-service">/v1/agent/service/register</a> endpoint
     * @see ServiceOptions
     */
    def registerServiceL(serviceOptions: ServiceOptions): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.registerService(serviceOptions, resultHandler)
      }.map(_ => ())

    /**
     *  Places a given service into "maintenance mode"
     * @param maintenanceOptions the maintenance options
     * @param resultHandler      will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/service.html#enable-maintenance-mode">/v1/agent/service/maintenance/:service_id</a> endpoint
     * @see MaintenanceOptions
     */
    def maintenanceServiceL(maintenanceOptions: MaintenanceOptions): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.maintenanceService(maintenanceOptions, resultHandler)
      }.map(_ => ())

    /**
     *  Remove a service from the local agent. The agent will take care of deregistering the service with the Catalog.
     *  If there is an associated check, that is also deregistered.
     * @param id            the ID of service
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/service.html#deregister-service">/v1/agent/service/deregister/:service_id</a> endpoint
     */
    def deregisterServiceL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.deregisterService(id, resultHandler)
      }.map(_ => ())

    /**
     *  Returns the nodes providing a service
     * @param service       name of service
     * @param resultHandler will be provided with list of nodes providing given service
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-nodes-for-service">/v1/catalog/service/:service</a> endpoint
     */
    def catalogServiceNodesL(service: String): Task[ServiceList] =
      Task.handle[ServiceList] { resultHandler =>
        target.catalogServiceNodes(service, resultHandler)
      }

    /**
     *  Returns the nodes providing a service
     * @param service       name of service
     * @param options       options used to request services
     * @param resultHandler will be provided with list of nodes providing given service
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-nodes-for-service">/v1/catalog/service/:service</a> endpoint
     */
    def catalogServiceNodesWithOptionsL(service: String, options: ServiceQueryOptions): Task[ServiceList] =
      Task.handle[ServiceList] { resultHandler =>
        target.catalogServiceNodesWithOptions(service, options, resultHandler)
      }

    /**
     *  Return all the datacenters that are known by the Consul server
     * @param resultHandler will be provided with list of datacenters
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-datacenters">/v1/catalog/datacenters</a> endpoint
     */
    def catalogDatacentersL(): Task[List[String]] =
      Task.handle[List[String]] { resultHandler =>
        target.catalogDatacenters(resultHandler)
      }

    /**
     *  Returns the nodes registered in a datacenter
     * @param resultHandler will be provided with list of nodes
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-nodes">/v1/catalog/nodes</a> endpoint
     */
    def catalogNodesL(): Task[NodeList] =
      Task.handle[NodeList] { resultHandler =>
        target.catalogNodes(resultHandler)
      }

    /**
     *  Returns the nodes registered in a datacenter
     * @param resultHandler will be provided with list of nodes
     * @param options       options used to request nodes
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-nodes">/v1/catalog/nodes</a> endpoint
     */
    def catalogNodesWithOptionsL(options: NodeQueryOptions): Task[NodeList] =
      Task.handle[NodeList] { resultHandler =>
        target.catalogNodesWithOptions(options, resultHandler)
      }

    /**
     *  Returns the checks associated with the service
     * @param service       the service name
     * @param resultHandler will be provided with list of checks
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/health.html#list-checks-for-service">/v1/health/checks/:service</a> endpoint
     */
    def healthChecksL(service: String): Task[CheckList] =
      Task.handle[CheckList] { resultHandler =>
        target.healthChecks(service, resultHandler)
      }

    /**
     *  Returns the checks associated with the service
     * @param service       the service name
     * @param options       options used to request checks
     * @param resultHandler will be provided with list of checks
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/health.html#list-checks-for-service">/v1/health/checks/:service</a> endpoint
     */
    def healthChecksWithOptionsL(service: String, options: CheckQueryOptions): Task[CheckList] =
      Task.handle[CheckList] { resultHandler =>
        target.healthChecksWithOptions(service, options, resultHandler)
      }

    /**
     *  Returns the checks in the specified status
     * @param healthState   the health state
     * @param resultHandler will be provided with list of checks
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/health.html#list-checks-in-state">/v1/health/state/:state</a> endpoint
     */
    def healthStateL(healthState: HealthState): Task[CheckList] =
      Task.handle[CheckList] { resultHandler =>
        target.healthState(healthState, resultHandler)
      }

    /**
     *  Returns the checks in the specified status
     * @param healthState   the health state
     * @param options       options used to request checks
     * @param resultHandler will be provided with list of checks
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/health.html#list-checks-in-state">/v1/health/state/:state</a> endpoint
     */
    def healthStateWithOptionsL(healthState: HealthState, options: CheckQueryOptions): Task[CheckList] =
      Task.handle[CheckList] { resultHandler =>
        target.healthStateWithOptions(healthState, options, resultHandler)
      }

    /**
     *  Returns the nodes providing the service. This endpoint is very similar to the {@link ConsulClient#catalogServiceNodes} endpoint;
     *  however, this endpoint automatically returns the status of the associated health check as well as any system level health checks.
     * @param service       the service name
     * @param passing       if true, filter results to only nodes with all checks in the passing state
     * @param resultHandler will be provided with list of services
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/health.html#list-nodes-for-service">/v1/health/service/:service</a> endpoint
     */
    def healthServiceNodesL(service: String, passing: Boolean): Task[ServiceEntryList] =
      Task.handle[ServiceEntryList] { resultHandler =>
        target.healthServiceNodes(service, passing, resultHandler)
      }

    /**
     *  Returns the nodes providing the service. This endpoint is very similar to the {@link ConsulClient#catalogServiceNodesWithOptions} endpoint;
     *  however, this endpoint automatically returns the status of the associated health check as well as any system level health checks.
     * @param service       the service name
     * @param passing       if true, filter results to only nodes with all checks in the passing state
     * @param options       options used to request services
     * @param resultHandler will be provided with list of services
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/health.html#list-nodes-for-service">/v1/health/service/:service</a> endpoint
     */
    def healthServiceNodesWithOptionsL(service: String, passing: Boolean, options: ServiceQueryOptions): Task[ServiceEntryList] =
      Task.handle[ServiceEntryList] { resultHandler =>
        target.healthServiceNodesWithOptions(service, passing, options, resultHandler)
      }

    /**
     *  Returns the services registered in a datacenter
     * @param resultHandler will be provided with list of services
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-services">/v1/catalog/services</a> endpoint
     */
    def catalogServicesL(): Task[ServiceList] =
      Task.handle[ServiceList] { resultHandler =>
        target.catalogServices(resultHandler)
      }

    /**
     *  Returns the services registered in a datacenter
     *  This is blocking query unlike {@link ConsulClient#catalogServices(Handler)}
     * @param resultHandler will be provided with list of services
     * @param options       the blocking options
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-services">/v1/catalog/services</a> endpoint
     */
    def catalogServicesWithOptionsL(options: BlockingQueryOptions): Task[ServiceList] =
      Task.handle[ServiceList] { resultHandler =>
        target.catalogServicesWithOptions(options, resultHandler)
      }

    /**
     *  Returns the node's registered services
     * @param node          node name
     * @param resultHandler will be provided with list of services
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-services-for-node">/v1/catalog/node/:node</a> endpoint
     */
    def catalogNodeServicesL(node: String): Task[ServiceList] =
      Task.handle[ServiceList] { resultHandler =>
        target.catalogNodeServices(node, resultHandler)
      }

    /**
     *  Returns the node's registered services
     *  This is blocking query unlike {@link ConsulClient#catalogNodeServices(String, Handler)}
     * @param node          node name
     * @param options       the blocking options
     * @param resultHandler will be provided with list of services
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/catalog.html#list-services-for-node">/v1/catalog/node/:node</a> endpoint
     */
    def catalogNodeServicesWithOptionsL(node: String, options: BlockingQueryOptions): Task[ServiceList] =
      Task.handle[ServiceList] { resultHandler =>
        target.catalogNodeServicesWithOptions(node, options, resultHandler)
      }

    /**
     *  Returns list of services registered with the local agent.
     * @param resultHandler will be provided with list of services
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/service.html#list-services">/v1/agent/services</a> endpoint
     */
    def localServicesL(): Task[List[Service]] =
      Task.handle[List[Service]] { resultHandler =>
        target.localServices(resultHandler)
      }

    /**
     *  Return all the checks that are registered with the local agent.
     * @param resultHandler will be provided with list of checks
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#list-checks">/v1/agent/checks</a> endpoint
     */
    def localChecksL(): Task[List[Check]] =
      Task.handle[List[Check]] { resultHandler =>
        target.localChecks(resultHandler)
      }

    /**
     *  Add a new check to the local agent. The agent is responsible for managing the status of the check
     *  and keeping the Catalog in sync.
     * @param checkOptions  options used to register new check
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#register-check">/v1/agent/check/register</a> endpoint
     */
    def registerCheckL(checkOptions: CheckOptions): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.registerCheck(checkOptions, resultHandler)
      }.map(_ => ())

    /**
     *  Remove a check from the local agent. The agent will take care of deregistering the check from the Catalog.
     * @param checkId       the ID of check
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#deregister-check">/v1/agent/check/deregister/:check_id</a> endpoint
     */
    def deregisterCheckL(checkId: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.deregisterCheck(checkId, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to "passing". Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-pass">/v1/agent/check/pass/:check_id</a> endpoint
     * @see CheckStatus
     */
    def passCheckL(checkId: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.passCheck(checkId, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to "passing". Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param note          specifies a human-readable message. This will be passed through to the check's {@code Output} field.
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-pass">/v1/agent/check/pass/:check_id</a> endpoint
     * @see CheckStatus
     */
    def passCheckWithNoteL(checkId: String, note: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.passCheckWithNote(checkId, note, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to "warning". Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-warn">/v1/agent/check/warn/:check_id</a> endpoint
     * @see CheckStatus
     */
    def warnCheckL(checkId: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.warnCheck(checkId, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to "warning". Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param note          specifies a human-readable message. This will be passed through to the check's {@code Output} field.
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-warn">/v1/agent/check/warn/:check_id</a> endpoint
     * @see CheckStatus
     */
    def warnCheckWithNoteL(checkId: String, note: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.warnCheckWithNote(checkId, note, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to "critical". Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-fail">/v1/agent/check/fail/:check_id</a> endpoint
     * @see CheckStatus
     */
    def failCheckL(checkId: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.failCheck(checkId, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to "critical". Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param note          specifies a human-readable message. This will be passed through to the check's {@code Output} field.
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-fail">/v1/agent/check/fail/:check_id</a> endpoint
     * @see CheckStatus
     */
    def failCheckWithNoteL(checkId: String, note: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.failCheckWithNote(checkId, note, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to given status. Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param status        new status of check
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-update">/v1/agent/check/update/:check_id</a> endpoint
     */
    def updateCheckL(checkId: String, status: CheckStatus): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.updateCheck(checkId, status, resultHandler)
      }.map(_ => ())

    /**
     *  Set status of the check to given status. Used with a check that is of the TTL type. The TTL clock will be reset.
     * @param checkId       the ID of check
     * @param status        new status of check
     * @param note          specifies a human-readable message. This will be passed through to the check's {@code Output} field.
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/agent/check.html#ttl-check-update">/v1/agent/check/update/:check_id</a> endpoint
     */
    def updateCheckWithNoteL(checkId: String, status: CheckStatus, note: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.updateCheckWithNote(checkId, status, note, resultHandler)
      }.map(_ => ())

    /**
     *  Get the Raft leader for the datacenter in which the agent is running.
     *  It returns an address in format "<code>10.1.10.12:8300</code>"
     * @param resultHandler will be provided with address of cluster leader
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/status.html#get-raft-leader">/v1/status/leader</a> endpoint
     */
    def leaderStatusL(): Task[String] =
      Task.handle[String] { resultHandler =>
        target.leaderStatus(resultHandler)
      }

    /**
     *  Retrieves the Raft peers for the datacenter in which the the agent is running.
     *  It returns a list of addresses "<code>10.1.10.12:8300</code>", "<code>10.1.10.13:8300</code>"
     * @param resultHandler will be provided with list of peers
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/status.html#list-raft-peers">/v1/status/peers</a> endpoint
     */
    def peersStatusL(): Task[List[String]] =
      Task.handle[List[String]] { resultHandler =>
        target.peersStatus(resultHandler)
      }

    /**
     *  Initialize a new session
     * @param idHandler will be provided with ID of new session
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#create-session">/v1/session/create</a> endpoint
     */
    def createSessionL(): Task[String] =
      Task.handle[String] { idHandler =>
        target.createSession(idHandler)
      }

    /**
     *  Initialize a new session
     * @param options   options used to create session
     * @param idHandler will be provided with ID of new session
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#create-session">/v1/session/create</a> endpoint
     */
    def createSessionWithOptionsL(options: SessionOptions): Task[String] =
      Task.handle[String] { idHandler =>
        target.createSessionWithOptions(options, idHandler)
      }

    /**
     *  Returns the requested session information
     * @param id            the ID of requested session
     * @param resultHandler will be provided with info of requested session
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#read-session">/v1/session/info/:uuid</a> endpoint
     */
    def infoSessionL(id: String): Task[Session] =
      Task.handle[Session] { resultHandler =>
        target.infoSession(id, resultHandler)
      }

    /**
     *  Returns the requested session information
     *  This is blocking query unlike {@link ConsulClient#infoSession(String, Handler)}
     * @param id            the ID of requested session
     * @param options       the blocking options
     * @param resultHandler will be provided with info of requested session
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#read-session">/v1/session/info/:uuid</a> endpoint
     */
    def infoSessionWithOptionsL(id: String, options: BlockingQueryOptions): Task[Session] =
      Task.handle[Session] { resultHandler =>
        target.infoSessionWithOptions(id, options, resultHandler)
      }

    /**
     *  Renews the given session. This is used with sessions that have a TTL, and it extends the expiration by the TTL
     * @param id            the ID of session that should be renewed
     * @param resultHandler will be provided with info of renewed session
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#renew-session">/v1/session/renew/:uuid</a> endpoint
     */
    def renewSessionL(id: String): Task[Session] =
      Task.handle[Session] { resultHandler =>
        target.renewSession(id, resultHandler)
      }

    /**
     *  Returns the active sessions
     * @param resultHandler will be provided with list of sessions
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#list-sessions">/v1/session/list</a> endpoint
     */
    def listSessionsL(): Task[SessionList] =
      Task.handle[SessionList] { resultHandler =>
        target.listSessions(resultHandler)
      }

    /**
     *  Returns the active sessions
     *  This is blocking query unlike {@link ConsulClient#listSessions(Handler)}
     * @param options       the blocking options
     * @param resultHandler will be provided with list of sessions
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#list-sessions">/v1/session/list</a> endpoint
     */
    def listSessionsWithOptionsL(options: BlockingQueryOptions): Task[SessionList] =
      Task.handle[SessionList] { resultHandler =>
        target.listSessionsWithOptions(options, resultHandler)
      }

    /**
     *  Returns the active sessions for a given node
     * @param nodeId        the ID of node
     * @param resultHandler will be provided with list of sessions
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#list-sessions-for-node">/v1/session/node/:node</a> endpoint
     */
    def listNodeSessionsL(nodeId: String): Task[SessionList] =
      Task.handle[SessionList] { resultHandler =>
        target.listNodeSessions(nodeId, resultHandler)
      }

    /**
     *  Returns the active sessions for a given node
     *  This is blocking query unlike {@link ConsulClient#listNodeSessions(String, Handler)}
     * @param nodeId        the ID of node
     * @param options       the blocking options
     * @param resultHandler will be provided with list of sessions
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#list-sessions-for-node">/v1/session/node/:node</a> endpoint
     */
    def listNodeSessionsWithOptionsL(nodeId: String, options: BlockingQueryOptions): Task[SessionList] =
      Task.handle[SessionList] { resultHandler =>
        target.listNodeSessionsWithOptions(nodeId, options, resultHandler)
      }

    /**
     *  Destroys the given session
     * @param id            the ID of session
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/session.html#delete-session">/v1/session/destroy/:uuid</a> endpoint
     */
    def destroySessionL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.destroySession(id, resultHandler)
      }.map(_ => ())

    /**
     * 
     * @param definition    definition of the prepare query
     * @param resultHandler will be provided with id of created prepare query
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#create-prepared-query">/v1/query</a> endpoint
     */
    def createPreparedQueryL(definition: PreparedQueryDefinition): Task[String] =
      Task.handle[String] { resultHandler =>
        target.createPreparedQuery(definition, resultHandler)
      }

    /**
     *  Returns an existing prepared query
     * @param id            the id of the query to read
     * @param resultHandler will be provided with definition of the prepare query
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#read-prepared-query-1">/v1/query/:uuid</a> endpoint
     */
    def getPreparedQueryL(id: String): Task[PreparedQueryDefinition] =
      Task.handle[PreparedQueryDefinition] { resultHandler =>
        target.getPreparedQuery(id, resultHandler)
      }

    /**
     *  Returns a list of all prepared queries.
     * @param resultHandler will be provided with list of definitions of the all prepare queries
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#read-prepared-query">/v1/query</a> endpoint
     */
    def getAllPreparedQueriesL(): Task[List[PreparedQueryDefinition]] =
      Task.handle[List[PreparedQueryDefinition]] { resultHandler =>
        target.getAllPreparedQueries(resultHandler)
      }

    /**
     * 
     * @param definition    definition of the prepare query
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#update-prepared-query">/v1/query/:uuid</a> endpoint
     */
    def updatePreparedQueryL(definition: PreparedQueryDefinition): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.updatePreparedQuery(definition, resultHandler)
      }.map(_ => ())

    /**
     *  Deletes an existing prepared query
     * @param id            the id of the query to delete
     * @param resultHandler will be called when complete
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#delete-prepared-query">/v1/query/:uuid</a> endpoint
     */
    def deletePreparedQueryL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.deletePreparedQuery(id, resultHandler)
      }.map(_ => ())

    /**
     *  Executes an existing prepared query.
     * @param query         the ID of the query to execute. This can also be the name of an existing prepared query,
     *                       or a name that matches a prefix name for a prepared query template.
     * @param resultHandler will be provided with response
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#execute-prepared-query">/v1/query/:uuid/execute</a> endpoint
     */
    def executePreparedQueryL(query: String): Task[PreparedQueryExecuteResponse] =
      Task.handle[PreparedQueryExecuteResponse] { resultHandler =>
        target.executePreparedQuery(query, resultHandler)
      }

    /**
     *  Executes an existing prepared query.
     * @param query         the ID of the query to execute. This can also be the name of an existing prepared query,
     *                       or a name that matches a prefix name for a prepared query template.
     * @param options       the options used to execute prepared query
     * @param resultHandler will be provided with response
     * @return reference to this, for fluency
     * @see <a href="https://www.consul.io/api/query.html#execute-prepared-query">/v1/query/:uuid/execute</a> endpoint
     */
    def executePreparedQueryWithOptionsL(query: String, options: PreparedQueryExecuteOptions): Task[PreparedQueryExecuteResponse] =
      Task.handle[PreparedQueryExecuteResponse] { resultHandler =>
        target.executePreparedQueryWithOptions(query, options, resultHandler)
      }
  }


}