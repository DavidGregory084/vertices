package vertices
package redis

import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.net.SocketAddress
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.redis.RedisClient
import io.vertx.redis.RedisTransaction
import io.vertx.redis.Script
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions
import io.vertx.redis.client.Request
import io.vertx.redis.client.Response
import io.vertx.redis.op.AggregateOptions
import io.vertx.redis.op.BitFieldOptions
import io.vertx.redis.op.BitFieldOverflowOptions
import io.vertx.redis.op.BitOperation
import io.vertx.redis.op.ClientReplyOptions
import io.vertx.redis.op.FailoverOptions
import io.vertx.redis.op.GeoMember
import io.vertx.redis.op.GeoRadiusOptions
import io.vertx.redis.op.GeoUnit
import io.vertx.redis.op.InsertOptions
import io.vertx.redis.op.KillFilter
import io.vertx.redis.op.LimitOptions
import io.vertx.redis.op.MigrateOptions
import io.vertx.redis.op.ObjectCmd
import io.vertx.redis.op.RangeLimitOptions
import io.vertx.redis.op.RangeOptions
import io.vertx.redis.op.ResetOptions
import io.vertx.redis.op.ScanOptions
import io.vertx.redis.op.ScriptDebugOptions
import io.vertx.redis.op.SetOptions
import io.vertx.redis.op.SlotCmd
import io.vertx.redis.op.SortOptions
import io.vertx.redis.sentinel.RedisSentinel
import java.lang.Double
import java.lang.Long
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List
import java.util.Map

package object client {
  implicit class VertxRedisTransactionOps(val target: RedisTransaction) extends AnyVal {
    /**
     *  Close the client - when it is fully closed the handler will be called.
     * @param handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Append a value to a key
     * @param key     Key string
     * @param value   Value to append
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: string
     */
    def appendL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.append(key, value, handler)
      }

    /**
     *  Authenticate to the server
     * @param password Password for authentication
     * @param handler  Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: connection
     */
    def authL(password: String): Task[String] =
      Task.handle[String] { handler =>
        target.auth(password, handler)
      }

    /**
     *  Asynchronously rewrite the append-only file
     * @since Redis 1.0.0
     *  group: server
     */
    def bgrewriteaofL(): Task[String] =
      Task.handle[String] { handler =>
        target.bgrewriteaof(handler)
      }

    /**
     *  Asynchronously save the dataset to disk
     * @since Redis 1.0.0
     *  group: server
     */
    def bgsaveL(): Task[String] =
      Task.handle[String] { handler =>
        target.bgsave(handler)
      }

    /**
     *  Count set bits in a string
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def bitcountL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.bitcount(key, handler)
      }

    /**
     *  Count set bits in a string
     * @param key     Key string
     * @param start   Start index
     * @param end     End index
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def bitcountRangeL(key: String, start: Long, end: Long): Task[String] =
      Task.handle[String] { handler =>
        target.bitcountRange(key, start, end, handler)
      }

    /**
     *  Perform bitwise operations between strings
     * @param operation Bitwise operation to perform
     * @param destkey   Destination key where result is stored
     * @param keys      List of keys on which to perform the operation
     * @param handler   Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def bitopL(operation: BitOperation, destkey: String, keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.bitop(operation, destkey, keys, handler)
      }

    /**
     *  Find first bit set or clear in a string
     * @param key     Key string
     * @param bit     What bit value to look for - must be 1, or 0
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.7
     *  group: string
     */
    def bitposL(key: String, bit: Int): Task[String] =
      Task.handle[String] { handler =>
        target.bitpos(key, bit, handler)
      }

    /**
     *  Find first bit set or clear in a string
     *  <p>
     *  See also bitposRange() method, which takes start, and stop offset.
     * @param key     Key string
     * @param bit     What bit value to look for - must be 1, or 0
     * @param start   Start offset
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.7
     *  group: string
     */
    def bitposFromL(key: String, bit: Int, start: Int): Task[String] =
      Task.handle[String] { handler =>
        target.bitposFrom(key, bit, start, handler)
      }

    /**
     *  Find first bit set or clear in a string
     *  <p>
     *  Note: when both start, and stop offsets are specified,
     *  behaviour is slightly different than if only start is specified
     * @param key     Key string
     * @param bit     What bit value to look for - must be 1, or 0
     * @param start   Start offset
     * @param stop    End offset - inclusive
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.7
     *  group: string
     */
    def bitposRangeL(key: String, bit: Int, start: Int, stop: Int): Task[String] =
      Task.handle[String] { handler =>
        target.bitposRange(key, bit, start, stop, handler)
      }

    /**
     *  Remove and get the first element in a list, or block until one is available
     * @param key     Key string identifying a list to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def blpopL(key: String, seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.blpop(key, seconds, handler)
      }

    /**
     *  Remove and get the first element in any of the lists, or block until one is available
     * @param keys    List of key strings identifying lists to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def blpopManyL(keys: List[String], seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.blpopMany(keys, seconds, handler)
      }

    /**
     *  Remove and get the last element in a list, or block until one is available
     * @param key     Key string identifying a list to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def brpopL(key: String, seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.brpop(key, seconds, handler)
      }

    /**
     *  Remove and get the last element in any of the lists, or block until one is available
     * @param keys    List of key strings identifying lists to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def brpopManyL(keys: List[String], seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.brpopMany(keys, seconds, handler)
      }

    /**
     *  Pop a value from a list, push it to another list and return it; or block until one is available
     * @param key     Key string identifying the source list
     * @param destkey Key string identifying the destination list
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def brpoplpushL(key: String, destkey: String, seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.brpoplpush(key, destkey, seconds, handler)
      }

    /**
     *  Kill the connection of a client
     * @param filter  Filter options
     * @param handler Handler for the result of this call.
     * @since Redis 2.4.0
     *  group: server
     */
    def clientKillL(filter: KillFilter): Task[String] =
      Task.handle[String] { handler =>
        target.clientKill(filter, handler)
      }

    /**
     *  Get the list of client connections
     * @since Redis 2.4.0
     *  group: server
     */
    def clientListL(): Task[String] =
      Task.handle[String] { handler =>
        target.clientList(handler)
      }

    /**
     *  Get the current connection name
     * @since Redis 2.6.9
     *  group: server
     */
    def clientGetnameL(): Task[String] =
      Task.handle[String] { handler =>
        target.clientGetname(handler)
      }

    /**
     *  Stop processing commands from clients for some time
     * @param millis  Pause time in milliseconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.9.50
     *  group: server
     */
    def clientPauseL(millis: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clientPause(millis, handler)
      }

    /**
     *  Set the current connection name
     * @param name    New name for current connection
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.9
     *  group: server
     */
    def clientSetnameL(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.clientSetname(name, handler)
      }

    /**
     *  Assign new hash slots to receiving node.
     * @param slots
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: server
     */
    def clusterAddslotsL(slots: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.clusterAddslots(slots, handler)
      }

    /**
     *  Return the number of failure reports active for a given node.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterCountFailureReportsL(nodeId: String): Task[String] =
      Task.handle[String] { handler =>
        target.clusterCountFailureReports(nodeId, handler)
      }

    /**
     *  Return the number of local keys in the specified hash slot.
     * @param slot
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterCountkeysinslotL(slot: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clusterCountkeysinslot(slot, handler)
      }

    /**
     *  Set hash slots as unbound in receiving node.
     * @param slot
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterDelslotsL(slot: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clusterDelslots(slot, handler)
      }

    /**
     *  Set hash slots as unbound in receiving node.
     * @param slots
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterDelslotsManyL(slots: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.clusterDelslotsMany(slots, handler)
      }

    /**
     *  Forces a slave to perform a manual failover of its master.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterFailoverL(): Task[String] =
      Task.handle[String] { handler =>
        target.clusterFailover(handler)
      }

    /**
     *  Forces a slave to perform a manual failover of its master.
     * @param options
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterFailOverWithOptionsL(options: FailoverOptions): Task[String] =
      Task.handle[String] { handler =>
        target.clusterFailOverWithOptions(options, handler)
      }

    /**
     *  Remove a node from the nodes table.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterForgetL(nodeId: String): Task[String] =
      Task.handle[String] { handler =>
        target.clusterForget(nodeId, handler)
      }

    /**
     *  Return local key names in the specified hash slot.
     * @param slot
     * @param count
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterGetkeysinslotL(slot: Long, count: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clusterGetkeysinslot(slot, count, handler)
      }

    /**
     *  Provides info about Redis Cluster node state.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterInfoL(): Task[String] =
      Task.handle[String] { handler =>
        target.clusterInfo(handler)
      }

    /**
     *  Returns the hash slot of the specified key.
     * @param key
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterKeyslotL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.clusterKeyslot(key, handler)
      }

    /**
     *  Force a node cluster to handshake with another node.
     * @param ip
     * @param port
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterMeetL(ip: String, port: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clusterMeet(ip, port, handler)
      }

    /**
     *  Get Cluster config for the node.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterNodesL(): Task[String] =
      Task.handle[String] { handler =>
        target.clusterNodes(handler)
      }

    /**
     *  Reconfigure a node as a slave of the specified master node.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterReplicateL(nodeId: String): Task[String] =
      Task.handle[String] { handler =>
        target.clusterReplicate(nodeId, handler)
      }

    /**
     *  Reset a Redis Cluster node.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterResetL(): Task[String] =
      Task.handle[String] { handler =>
        target.clusterReset(handler)
      }

    /**
     *  Reset a Redis Cluster node.
     * @param options
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterResetWithOptionsL(options: ResetOptions): Task[String] =
      Task.handle[String] { handler =>
        target.clusterResetWithOptions(options, handler)
      }

    /**
     *  Forces the node to save cluster state on disk.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSaveconfigL(): Task[String] =
      Task.handle[String] { handler =>
        target.clusterSaveconfig(handler)
      }

    /**
     *  Set the configuration epoch in a new node.
     * @param epoch
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSetConfigEpochL(epoch: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clusterSetConfigEpoch(epoch, handler)
      }

    /**
     *  Bind an hash slot to a specific node.
     * @param slot
     * @param subcommand
     * @param handler    Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSetslotL(slot: Long, subcommand: SlotCmd): Task[String] =
      Task.handle[String] { handler =>
        target.clusterSetslot(slot, subcommand, handler)
      }

    /**
     *  Bind an hash slot to a specific node.
     * @param slot
     * @param subcommand
     * @param nodeId
     * @param handler    Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSetslotWithNodeL(slot: Long, subcommand: SlotCmd, nodeId: String): Task[String] =
      Task.handle[String] { handler =>
        target.clusterSetslotWithNode(slot, subcommand, nodeId, handler)
      }

    /**
     *  List slave nodes of the specified master node.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSlavesL(nodeId: String): Task[String] =
      Task.handle[String] { handler =>
        target.clusterSlaves(nodeId, handler)
      }

    /**
     *  Get array of Cluster slot to node mappings
     * @since Redis 3.0.0
     *  group: server
     */
    def clusterSlotsL(): Task[String] =
      Task.handle[String] { handler =>
        target.clusterSlots(handler)
      }

    /**
     *  Get array of Redis command details
     * @since Redis 2.8.13
     *  group: server
     */
    def commandL(): Task[String] =
      Task.handle[String] { handler =>
        target.command(handler)
      }

    /**
     *  Get total number of Redis commands
     * @since Redis 2.8.13
     *  group: server
     */
    def commandCountL(): Task[String] =
      Task.handle[String] { handler =>
        target.commandCount(handler)
      }

    /**
     *  Extract keys given a full Redis command
     * @since Redis 2.8.13
     *  group: server
     */
    def commandGetkeysL(): Task[String] =
      Task.handle[String] { handler =>
        target.commandGetkeys(handler)
      }

    /**
     *  Get array of specific Redis command details
     * @param commands List of commands to get info for
     * @param handler  Handler for the result of this call.
     * @since Redis 2.8.13
     *  group: server
     */
    def commandInfoL(commands: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.commandInfo(commands, handler)
      }

    /**
     *  Get the value of a configuration parameter
     * @param parameter Configuration parameter
     * @param handler   Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: server
     */
    def configGetL(parameter: String): Task[String] =
      Task.handle[String] { handler =>
        target.configGet(parameter, handler)
      }

    /**
     *  Rewrite the configuration file with the in memory configuration
     * @since Redis 2.8.0
     *  group: server
     */
    def configRewriteL(): Task[String] =
      Task.handle[String] { handler =>
        target.configRewrite(handler)
      }

    /**
     *  Set a configuration parameter to the given value
     * @param parameter Configuration parameter
     * @param value     New value
     * @param handler   Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: server
     */
    def configSetL(parameter: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.configSet(parameter, value, handler)
      }

    /**
     *  Reset the stats returned by INFO
     * @since Redis 2.0.0
     *  group: server
     */
    def configResetstatL(): Task[String] =
      Task.handle[String] { handler =>
        target.configResetstat(handler)
      }

    /**
     *  Return the number of keys in the selected database
     * @since Redis 1.0.0
     *  group: server
     */
    def dbsizeL(): Task[String] =
      Task.handle[String] { handler =>
        target.dbsize(handler)
      }

    /**
     *  Get debugging information about a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def debugObjectL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.debugObject(key, handler)
      }

    /**
     *  Make the server crash
     * @since Redis 1.0.0
     *  group: server
     */
    def debugSegfaultL(): Task[String] =
      Task.handle[String] { handler =>
        target.debugSegfault(handler)
      }

    /**
     *  Decrement the integer value of a key by one
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def decrL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.decr(key, handler)
      }

    /**
     *  Decrement the integer value of a key by the given number
     * @param key       Key string
     * @param decrement Value by which to decrement
     * @param handler   Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def decrbyL(key: String, decrement: Long): Task[String] =
      Task.handle[String] { handler =>
        target.decrby(key, decrement, handler)
      }

    /**
     *  Delete a key
     * @param key     Keys to delete
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def delL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.del(key, handler)
      }

    /**
     *  Delete many keys
     * @param keys    List of keys to delete
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def delManyL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.delMany(keys, handler)
      }

    /**
     *  Discard all commands issued after MULTI
     * @since Redis 2.0.0
     *  group: RedisTransactions
     */
    def discardL(): Task[String] =
      Task.handle[String] { handler =>
        target.discard(handler)
      }

    /**
     *  Return a serialized version of the value stored at the specified key.
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def dumpL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.dump(key, handler)
      }

    /**
     *  Echo the given string
     * @param message String to echo
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: connection
     */
    def echoL(message: String): Task[String] =
      Task.handle[String] { handler =>
        target.echo(message, handler)
      }

    /**
     *  Execute a Lua script server side. Due to the dynamic nature of this command any response type could be returned
     *  for This reason and to ensure type safety the reply is always guaranteed to be a JsonArray.
     *  <p>
     *  When a reply if for example a String the handler will be called with a JsonArray with a single element containing
     *  the String.
     * @param script  Lua script to evaluate
     * @param keys    List of keys
     * @param args    List of argument values
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def evalL(script: String, keys: List[String], args: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.eval(script, keys, args, handler)
      }

    /**
     *  Execute a Lua script server side. Due to the dynamic nature of this command any response type could be returned
     *  for This reason and to ensure type safety the reply is always guaranteed to be a JsonArray.
     *  <p>
     *  When a reply if for example a String the handler will be called with a JsonArray with a single element containing
     *  the String.
     * @param sha1    SHA1 digest of the script cached on the server
     * @param keys    List of keys
     * @param values  List of values
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def evalshaL(sha1: String, keys: List[String], values: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.evalsha(sha1, keys, values, handler)
      }

    /**
     *  Execute all commands issued after MULTI
     * @since Redis 1.2.0
     *  group: RedisTransactions
     */
    def execL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.exec(handler)
      }

    /**
     *  Determine if a key exists
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def existsL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.exists(key, handler)
      }

    /**
     *  Determine if one or many keys exist
     * @param keys    List of key strings
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.3
     *  group: generic
     */
    def existsManyL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.existsMany(keys, handler)
      }

    /**
     *  Set a key's time to live in seconds
     * @param key     Key string
     * @param seconds Time to live in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def expireL(key: String, seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.expire(key, seconds, handler)
      }

    /**
     *  Set the expiration for a key as a UNIX timestamp
     * @param key     Key string
     * @param seconds Expiry time as Unix timestamp in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: generic
     */
    def expireatL(key: String, seconds: Long): Task[String] =
      Task.handle[String] { handler =>
        target.expireat(key, seconds, handler)
      }

    /**
     *  Remove all keys from all databases
     * @since Redis 1.0.0
     *  group: server
     */
    def flushallL(): Task[String] =
      Task.handle[String] { handler =>
        target.flushall(handler)
      }

    /**
     *  Remove all keys from the current database
     * @since Redis 1.0.0
     *  group: server
     */
    def flushdbL(): Task[String] =
      Task.handle[String] { handler =>
        target.flushdb(handler)
      }

    /**
     *  Get the value of a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def getL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.get(key, handler)
      }

    /**
     *  Get the value of a key - without decoding as utf-8
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def getBinaryL(key: String): Task[Buffer] =
      Task.handle[Buffer] { handler =>
        target.getBinary(key, handler)
      }

    /**
     *  Returns the bit value at offset in the string value stored at key
     * @param key     Key string
     * @param offset  Offset in bits
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def getbitL(key: String, offset: Long): Task[String] =
      Task.handle[String] { handler =>
        target.getbit(key, offset, handler)
      }

    /**
     *  Get a substring of the string stored at a key
     * @param key     Key string
     * @param start   Start offset
     * @param end     End offset - inclusive
     * @param handler Handler for the result of this call.
     * @since Redis 2.4.0
     *  group: string
     */
    def getrangeL(key: String, start: Long, end: Long): Task[String] =
      Task.handle[String] { handler =>
        target.getrange(key, start, end, handler)
      }

    /**
     *  Set the string value of a key and return its old value
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def getsetL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.getset(key, value, handler)
      }

    /**
     *  Delete one or more hash fields
     * @param key     Key string
     * @param field   Field name
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hdelL(key: String, field: String): Task[String] =
      Task.handle[String] { handler =>
        target.hdel(key, field, handler)
      }

    /**
     *  Delete one or more hash fields
     * @param key     Key string
     * @param fields  Field names
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hdelManyL(key: String, fields: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.hdelMany(key, fields, handler)
      }

    /**
     *  Determine if a hash field exists
     * @param key     Key string
     * @param field   Field name
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hexistsL(key: String, field: String): Task[String] =
      Task.handle[String] { handler =>
        target.hexists(key, field, handler)
      }

    /**
     *  Get the value of a hash field
     * @param key     Key string
     * @param field   Field name
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hgetL(key: String, field: String): Task[String] =
      Task.handle[String] { handler =>
        target.hget(key, field, handler)
      }

    /**
     *  Get all the fields and values in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hgetallL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.hgetall(key, handler)
      }

    /**
     *  Increment the integer value of a hash field by the given number
     * @param key       Key string
     * @param field     Field name
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hincrbyL(key: String, field: String, increment: Long): Task[String] =
      Task.handle[String] { handler =>
        target.hincrby(key, field, increment, handler)
      }

    /**
     *  Increment the float value of a hash field by the given amount
     * @param key       Key string
     * @param field     Field name
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: hash
     */
    def hincrbyfloatL(key: String, field: String, increment: Double): Task[String] =
      Task.handle[String] { handler =>
        target.hincrbyfloat(key, field, increment, handler)
      }

    /**
     *  Get all the fields in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hkeysL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.hkeys(key, handler)
      }

    /**
     *  Get the number of fields in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hlenL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.hlen(key, handler)
      }

    /**
     *  Get the values of all the given hash fields
     * @param key     Key string
     * @param fields  Field names
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hmgetL(key: String, fields: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.hmget(key, fields, handler)
      }

    /**
     *  Set multiple hash fields to multiple values
     * @param key     Key string
     * @param values  Map of field:value pairs
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hmsetL(key: String, values: JsonObject): Task[String] =
      Task.handle[String] { handler =>
        target.hmset(key, values, handler)
      }

    /**
     *  Set the string value of a hash field
     * @param key     Key string
     * @param field   Field name
     * @param value   New value
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hsetL(key: String, field: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.hset(key, field, value, handler)
      }

    /**
     *  Set the value of a hash field, only if the field does not exist
     * @param key     Key string
     * @param field   Field name
     * @param value   New value
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hsetnxL(key: String, field: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.hsetnx(key, field, value, handler)
      }

    /**
     *  Get all the values in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hvalsL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.hvals(key, handler)
      }

    /**
     *  Increment the integer value of a key by one
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def incrL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.incr(key, handler)
      }

    /**
     *  Increment the integer value of a key by the given amount
     * @param key       Key string
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def incrbyL(key: String, increment: Long): Task[String] =
      Task.handle[String] { handler =>
        target.incrby(key, increment, handler)
      }

    /**
     *  Increment the float value of a key by the given amount
     * @param key       Key string
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def incrbyfloatL(key: String, increment: Double): Task[String] =
      Task.handle[String] { handler =>
        target.incrbyfloat(key, increment, handler)
      }

    /**
     *  Get information and statistics about the server
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def infoL(): Task[String] =
      Task.handle[String] { handler =>
        target.info(handler)
      }

    /**
     *  Get information and statistics about the server
     * @param section Specific section of information to return
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def infoSectionL(section: String): Task[String] =
      Task.handle[String] { handler =>
        target.infoSection(section, handler)
      }

    /**
     *  Find all keys matching the given pattern
     * @param pattern Pattern to limit the keys returned
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def keysL(pattern: String): Task[String] =
      Task.handle[String] { handler =>
        target.keys(pattern, handler)
      }

    /**
     *  Get the UNIX time stamp of the last successful save to disk
     * @since Redis 1.0.0
     *  group: server
     */
    def lastsaveL(): Task[String] =
      Task.handle[String] { handler =>
        target.lastsave(handler)
      }

    /**
     *  Get an element from a list by its index
     * @param key     Key string
     * @param index   Index of list element to get
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lindexL(key: String, index: Int): Task[String] =
      Task.handle[String] { handler =>
        target.lindex(key, index, handler)
      }

    /**
     *  Insert an element before or after another element in a list
     * @param key     Key string
     * @param option  BEFORE or AFTER
     * @param pivot   Key to use as a pivot
     * @param value   Value to be inserted before or after the pivot
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def linsertL(key: String, option: InsertOptions, pivot: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.linsert(key, option, pivot, value, handler)
      }

    /**
     *  Get the length of a list
     * @param key     String key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def llenL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.llen(key, handler)
      }

    /**
     *  Remove and get the first element in a list
     * @param key     String key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lpopL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.lpop(key, handler)
      }

    /**
     *  Prepend one or multiple values to a list
     * @param key     Key string
     * @param values  Values to be added at the beginning of the list, one by one
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lpushManyL(key: String, values: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.lpushMany(key, values, handler)
      }

    /**
     *  Prepend one value to a list
     * @param key     Key string
     * @param value   Value to be added at the beginning of the list
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lpushL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.lpush(key, value, handler)
      }

    /**
     *  Prepend a value to a list, only if the list exists
     * @param key     Key string
     * @param value   Value to add at the beginning of the list
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def lpushxL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.lpushx(key, value, handler)
      }

    /**
     *  Get a range of elements from a list
     * @param key     Key string
     * @param from    Start index
     * @param to      Stop index
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lrangeL(key: String, from: Long, to: Long): Task[String] =
      Task.handle[String] { handler =>
        target.lrange(key, from, to, handler)
      }

    /**
     *  Remove elements from a list
     * @param key     Key string
     * @param count   Number of first found occurrences equal to $value to remove from the list
     * @param value   Value to be removed
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lremL(key: String, count: Long, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.lrem(key, count, value, handler)
      }

    /**
     *  Set the value of an element in a list by its index
     * @param key     Key string
     * @param index   Position within list
     * @param value   New value
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lsetL(key: String, index: Long, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.lset(key, index, value, handler)
      }

    /**
     *  Trim a list to the specified range
     * @param key     Key string
     * @param from    Start index
     * @param to      Stop index
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def ltrimL(key: String, from: Long, to: Long): Task[String] =
      Task.handle[String] { handler =>
        target.ltrim(key, from, to, handler)
      }

    /**
     *  Get the value of the given key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def mgetL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.mget(key, handler)
      }

    /**
     *  Get the values of all the given keys
     * @param keys    List of keys to get
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def mgetManyL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.mgetMany(keys, handler)
      }

    /**
     *  Atomically transfer a key from a Redis instance to another one.
     * @param host    Destination host
     * @param port    Destination port
     * @param key     Key to migrate
     * @param destdb  Destination database index
     * @param options Migrate options
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def migrateL(host: String, port: Int, key: String, destdb: Int, timeout: Long, options: MigrateOptions): Task[String] =
      Task.handle[String] { handler =>
        target.migrate(host, port, key, destdb, timeout, options, handler)
      }

    /**
     *  Listen for all requests received by the server in real time
     * @since Redis 1.0.0
     *  group: server
     */
    def monitorL(): Task[String] =
      Task.handle[String] { handler =>
        target.monitor(handler)
      }

    /**
     *  Move a key to another database
     * @param key     Key to migrate
     * @param destdb  Destination database index
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def moveL(key: String, destdb: Int): Task[String] =
      Task.handle[String] { handler =>
        target.move(key, destdb, handler)
      }

    /**
     *  Set multiple keys to multiple values
     * @param keyvals Key value pairs to set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.1
     *  group: string
     */
    def msetL(keyvals: JsonObject): Task[String] =
      Task.handle[String] { handler =>
        target.mset(keyvals, handler)
      }

    /**
     *  Set multiple keys to multiple values, only if none of the keys exist
     * @param keyvals Key value pairs to set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.1
     *  group: string
     */
    def msetnxL(keyvals: JsonObject): Task[String] =
      Task.handle[String] { handler =>
        target.msetnx(keyvals, handler)
      }

    /**
     *  Mark the start of a RedisTransaction block
     * @since Redis 1.2.0
     *  group: RedisTransactions
     */
    def multiL(): Task[String] =
      Task.handle[String] { handler =>
        target.multi(handler)
      }

    /**
     *  Inspect the internals of Redis objects
     * @param key     Key string
     * @param cmd     Object sub command
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.3
     *  group: generic
     */
    def objectL(key: String, cmd: ObjectCmd): Task[String] =
      Task.handle[String] { handler =>
        target.`object`(key, cmd, handler)
      }

    /**
     *  Remove the expiration from a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: generic
     */
    def persistL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.persist(key, handler)
      }

    /**
     *  Set a key's time to live in milliseconds
     * @param key     String key
     * @param millis  Time to live in milliseconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def pexpireL(key: String, millis: Long): Task[String] =
      Task.handle[String] { handler =>
        target.pexpire(key, millis, handler)
      }

    /**
     *  Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @param key     Key string
     * @param millis  Expiry time as Unix timestamp in milliseconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def pexpireatL(key: String, millis: Long): Task[String] =
      Task.handle[String] { handler =>
        target.pexpireat(key, millis, handler)
      }

    /**
     *  Adds the specified element to the specified HyperLogLog.
     * @param key     Key string
     * @param element Element to add
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfaddL(key: String, element: String): Task[String] =
      Task.handle[String] { handler =>
        target.pfadd(key, element, handler)
      }

    /**
     *  Adds the specified elements to the specified HyperLogLog.
     * @param key      Key string
     * @param elements Elementa to add
     * @param handler  Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfaddManyL(key: String, elements: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.pfaddMany(key, elements, handler)
      }

    /**
     *  Return the approximated cardinality of the set observed by the HyperLogLog at key.
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfcountL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.pfcount(key, handler)
      }

    /**
     *  Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     * @param keys    List of keys
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfcountManyL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.pfcountMany(keys, handler)
      }

    /**
     *  Merge N different HyperLogLogs into a single one.
     * @param destkey Destination key
     * @param keys    List of source keys
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfmergeL(destkey: String, keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.pfmerge(destkey, keys, handler)
      }

    /**
     *  Ping the server
     * @since Redis 1.0.0
     *  group: connection
     */
    def pingL(): Task[String] =
      Task.handle[String] { handler =>
        target.ping(handler)
      }

    /**
     *  Set the value and expiration in milliseconds of a key
     * @param key     Key string
     * @param millis  Number of milliseconds until the key expires
     * @param value   New value for key
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def psetexL(key: String, millis: Long, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.psetex(key, millis, value, handler)
      }

    /**
     *  Listen for messages published to channels matching the given pattern
     * @param pattern Pattern string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def psubscribeL(pattern: String): Task[String] =
      Task.handle[String] { handler =>
        target.psubscribe(pattern, handler)
      }

    /**
     *  Listen for messages published to channels matching the given patterns
     * @param patterns List of patterns
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def psubscribeManyL(patterns: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.psubscribeMany(patterns, handler)
      }

    /**
     *  Lists the currently active channels - only those matching the pattern
     * @param pattern A glob-style pattern - an empty string means no pattern
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: pubsub
     */
    def pubsubChannelsL(pattern: String): Task[String] =
      Task.handle[String] { handler =>
        target.pubsubChannels(pattern, handler)
      }

    /**
     *  Returns the number of subscribers (not counting clients subscribed to patterns) for the specified channels
     * @param channels List of channels
     * @param handler  Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: pubsub
     */
    def pubsubNumsubL(channels: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.pubsubNumsub(channels, handler)
      }

    /**
     *  Returns the number of subscriptions to patterns (that are performed using the PSUBSCRIBE command)
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: pubsub
     */
    def pubsubNumpatL(): Task[String] =
      Task.handle[String] { handler =>
        target.pubsubNumpat(handler)
      }

    /**
     *  Get the time to live for a key in milliseconds
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def pttlL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.pttl(key, handler)
      }

    /**
     *  Post a message to a channel
     * @param channel Channel key
     * @param message Message to send to channel
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def publishL(channel: String, message: String): Task[String] =
      Task.handle[String] { handler =>
        target.publish(channel, message, handler)
      }

    /**
     *  Stop listening for messages posted to channels matching the given patterns
     * @param patterns List of patterns to match against
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def punsubscribeL(patterns: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.punsubscribe(patterns, handler)
      }

    /**
     *  Return a random key from the keyspace
     * @since Redis 1.0.0
     *  group: generic
     */
    def randomkeyL(): Task[String] =
      Task.handle[String] { handler =>
        target.randomkey(handler)
      }

    /**
     *  Rename a key
     * @param key     Key string to be renamed
     * @param newkey  New key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def renameL(key: String, newkey: String): Task[String] =
      Task.handle[String] { handler =>
        target.rename(key, newkey, handler)
      }

    /**
     *  Rename a key, only if the new key does not exist
     * @param key     Key string to be renamed
     * @param newkey  New key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def renamenxL(key: String, newkey: String): Task[String] =
      Task.handle[String] { handler =>
        target.renamenx(key, newkey, handler)
      }

    /**
     *  Create a key using the provided serialized value, previously obtained using DUMP.
     * @param key        Key string
     * @param millis     Expiry time in milliseconds to set on the key
     * @param serialized Serialized form of the key value as obtained using DUMP
     * @param handler    Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def restoreL(key: String, millis: Long, serialized: String): Task[String] =
      Task.handle[String] { handler =>
        target.restore(key, millis, serialized, handler)
      }

    /**
     *  Return the role of the instance in the context of replication
     * @since Redis 2.8.12
     *  group: server
     */
    def roleL(): Task[String] =
      Task.handle[String] { handler =>
        target.role(handler)
      }

    /**
     *  Remove and get the last element in a list
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def rpopL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.rpop(key, handler)
      }

    /**
     *  Remove the last element in a list, append it to another list and return it
     * @param key     Key string identifying source list
     * @param destkey Key string identifying destination list
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: list
     */
    def rpoplpushL(key: String, destkey: String): Task[String] =
      Task.handle[String] { handler =>
        target.rpoplpush(key, destkey, handler)
      }

    /**
     *  Append one or multiple values to a list
     * @param key     Key string
     * @param values  List of values to add to the end of the list
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def rpushManyL(key: String, values: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.rpushMany(key, values, handler)
      }

    /**
     *  Append one or multiple values to a list
     * @param key     Key string
     * @param value   Value to be added to the end of the list
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def rpushL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.rpush(key, value, handler)
      }

    /**
     *  Append a value to a list, only if the list exists
     * @param key     Key string
     * @param value   Value to be added to the end of the list
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def rpushxL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.rpushx(key, value, handler)
      }

    /**
     *  Add a member to a set
     * @param key     Key string
     * @param member  Value to be added to the set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def saddL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.sadd(key, member, handler)
      }

    /**
     *  Add one or more members to a set
     * @param key     Key string
     * @param members Values to be added to the set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def saddManyL(key: String, members: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.saddMany(key, members, handler)
      }

    /**
     *  Synchronously save the dataset to disk
     * @since Redis 1.0.0
     *  group: server
     */
    def saveL(): Task[String] =
      Task.handle[String] { handler =>
        target.save(handler)
      }

    /**
     *  Get the number of members in a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def scardL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.scard(key, handler)
      }

    /**
     *  Check existence of script in the script cache.
     * @param script  SHA1 digest identifying a script in the script cache
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptExistsL(script: String): Task[String] =
      Task.handle[String] { handler =>
        target.scriptExists(script, handler)
      }

    /**
     *  Check existence of scripts in the script cache.
     * @param scripts List of SHA1 digests identifying scripts in the script cache
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptExistsManyL(scripts: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.scriptExistsMany(scripts, handler)
      }

    /**
     *  Remove all the scripts from the script cache.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptFlushL(): Task[String] =
      Task.handle[String] { handler =>
        target.scriptFlush(handler)
      }

    /**
     *  Kill the script currently in execution.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptKillL(): Task[String] =
      Task.handle[String] { handler =>
        target.scriptKill(handler)
      }

    /**
     *  Load the specified Lua script into the script cache.
     * @param script  Lua script
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptLoadL(script: String): Task[String] =
      Task.handle[String] { handler =>
        target.scriptLoad(script, handler)
      }

    /**
     *  Subtract multiple sets
     * @param key     Key identifying the set to compare with all other sets combined
     * @param cmpkeys List of keys identifying sets to subtract from the key set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sdiffL(key: String, cmpkeys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sdiff(key, cmpkeys, handler)
      }

    /**
     *  Subtract multiple sets and store the resulting set in a key
     * @param destkey Destination key where the result should be stored
     * @param key     Key identifying the set to compare with all other sets combined
     * @param cmpkeys List of keys identifying sets to subtract from the key set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sdiffstoreL(destkey: String, key: String, cmpkeys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sdiffstore(destkey, key, cmpkeys, handler)
      }

    /**
     *  Change the selected database for the current connection
     * @param dbindex Index identifying the new active database
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: connection
     */
    def selectL(dbindex: Int): Task[String] =
      Task.handle[String] { handler =>
        target.select(dbindex, handler)
      }

    /**
     *  Set the string value of a key
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.set(key, value, handler)
      }

    /**
     *  Set the string value of a key
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param options Set options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setWithOptionsL(key: String, value: String, options: SetOptions): Task[String] =
      Task.handle[String] { handler =>
        target.setWithOptions(key, value, options, handler)
      }

    /**
     *  Set the binary string value of a key - without encoding as utf-8
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setBinaryL(key: String, value: Buffer): Task[String] =
      Task.handle[String] { handler =>
        target.setBinary(key, value, handler)
      }

    /**
     *  Set the string value of a key
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param options Set options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setBinaryWithOptionsL(key: String, value: Buffer, options: SetOptions): Task[String] =
      Task.handle[String] { handler =>
        target.setBinaryWithOptions(key, value, options, handler)
      }

    /**
     *  Sets or clears the bit at offset in the string value stored at key
     * @param key     Key string
     * @param offset  Bit offset
     * @param bit     New value - must be 1 or 0
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def setbitL(key: String, offset: Long, bit: Int): Task[String] =
      Task.handle[String] { handler =>
        target.setbit(key, offset, bit, handler)
      }

    /**
     *  Set the value and expiration of a key
     * @param key     Key string
     * @param seconds Number of seconds until the key expires
     * @param value   New value for key
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: string
     */
    def setexL(key: String, seconds: Long, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.setex(key, seconds, value, handler)
      }

    /**
     *  Set the value of a key, only if the key does not exist
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setnxL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.setnx(key, value, handler)
      }

    /**
     *  Overwrite part of a string at key starting at the specified offset
     * @param key     Key string
     * @param offset  Offset - the maximum offset that you can set is 2^29 -1 (536870911), as Redis Strings are limited to 512 megabytes
     * @param value   Value to overwrite with
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def setrangeL(key: String, offset: Int, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.setrange(key, offset, value, handler)
      }

    /**
     *  Intersect multiple sets
     * @param keys    List of keys to perform intersection on
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sinterL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sinter(keys, handler)
      }

    /**
     *  Intersect multiple sets and store the resulting set in a key
     * @param destkey Key where to store the results
     * @param keys    List of keys to perform intersection on
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sinterstoreL(destkey: String, keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sinterstore(destkey, keys, handler)
      }

    /**
     *  Determine if a given value is a member of a set
     * @param key     Key string
     * @param member  Member to look for
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sismemberL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.sismember(key, member, handler)
      }

    /**
     *  Make the server a slave of another instance
     * @param host    Host to become this server's master
     * @param port    Port of our new master
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def slaveofL(host: String, port: Int): Task[String] =
      Task.handle[String] { handler =>
        target.slaveof(host, port, handler)
      }

    /**
     *  Make this server a master
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def slaveofNooneL(): Task[String] =
      Task.handle[String] { handler =>
        target.slaveofNoone(handler)
      }

    /**
     *  Read the Redis slow queries log
     * @param limit   Number of log entries to return. If value is less than zero all entries are returned
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.12
     *  group: server
     */
    def slowlogGetL(limit: Int): Task[String] =
      Task.handle[String] { handler =>
        target.slowlogGet(limit, handler)
      }

    /**
     *  Get the length of the Redis slow queries log
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.12
     *  group: server
     */
    def slowlogLenL(): Task[String] =
      Task.handle[String] { handler =>
        target.slowlogLen(handler)
      }

    /**
     *  Reset the Redis slow queries log
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.12
     *  group: server
     */
    def slowlogResetL(): Task[String] =
      Task.handle[String] { handler =>
        target.slowlogReset(handler)
      }

    /**
     *  Get all the members in a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def smembersL(key: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.smembers(key, handler)
      }

    /**
     *  Move a member from one set to another
     * @param key     Key of source set currently containing the member
     * @param destkey Key identifying the destination set
     * @param member  Member to move
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def smoveL(key: String, destkey: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.smove(key, destkey, member, handler)
      }

    /**
     *  Sort the elements in a list, set or sorted set
     * @param key     Key string
     * @param options Sort options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def sortL(key: String, options: SortOptions): Task[String] =
      Task.handle[String] { handler =>
        target.sort(key, options, handler)
      }

    /**
     *  Remove and return a random member from a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def spopL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.spop(key, handler)
      }

    /**
     *  Remove and return random members from a set
     * @param key     Key string
     * @param count   Number of members to remove
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def spopManyL(key: String, count: Int): Task[String] =
      Task.handle[String] { handler =>
        target.spopMany(key, count, handler)
      }

    /**
     *  Get one or multiple random members from a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def srandmemberL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.srandmember(key, handler)
      }

    /**
     *  Get one or multiple random members from a set
     * @param key     Key string
     * @param count   Number of members to get
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def srandmemberCountL(key: String, count: Int): Task[String] =
      Task.handle[String] { handler =>
        target.srandmemberCount(key, count, handler)
      }

    /**
     *  Remove one member from a set
     * @param key     Key string
     * @param member  Member to remove
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sremL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.srem(key, member, handler)
      }

    /**
     *  Remove one or more members from a set
     * @param key     Key string
     * @param members Members to remove
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sremManyL(key: String, members: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sremMany(key, members, handler)
      }

    /**
     *  Get the length of the value stored in a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def strlenL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.strlen(key, handler)
      }

    /**
     *  Listen for messages published to the given channels
     * @param channel Channel to subscribe to
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def subscribeL(channel: String): Task[String] =
      Task.handle[String] { handler =>
        target.subscribe(channel, handler)
      }

    /**
     *  Listen for messages published to the given channels
     * @param channels List of channels to subscribe to
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def subscribeManyL(channels: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.subscribeMany(channels, handler)
      }

    /**
     *  Add multiple sets
     * @param keys    List of keys identifying sets to add up
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sunionL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sunion(keys, handler)
      }

    /**
     *  Add multiple sets and store the resulting set in a key
     * @param destkey Destination key
     * @param keys    List of keys identifying sets to add up
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sunionstoreL(destkey: String, keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.sunionstore(destkey, keys, handler)
      }

    /**
     *  Internal command used for replication
     * @since Redis 1.0.0
     *  group: server
     */
    def syncL(): Task[String] =
      Task.handle[String] { handler =>
        target.sync(handler)
      }

    /**
     *  Return the current server time
     * @since Redis 2.6.0
     *  group: server
     */
    def timeL(): Task[String] =
      Task.handle[String] { handler =>
        target.time(handler)
      }

    /**
     *  Get the time to live for a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def ttlL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.ttl(key, handler)
      }

    /**
     *  Determine the type stored at key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def typeL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.`type`(key, handler)
      }

    /**
     *  Stop listening for messages posted to the given channels
     * @param channels List of channels to subscribe to
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def unsubscribeL(channels: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.unsubscribe(channels, handler)
      }

    /**
     *  Forget about all watched keys
     * @since Redis 2.2.0
     *  group: RedisTransactions
     */
    def unwatchL(): Task[String] =
      Task.handle[String] { handler =>
        target.unwatch(handler)
      }

    /**
     *  Wait for the synchronous replication of all the write commands sent in the context of the current connection.
     * @param numSlaves
     * @param timeout
     * @param handler   Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: generic
     */
    def waitL(numSlaves: Long, timeout: Long): Task[String] =
      Task.handle[String] { handler =>
        target.wait(numSlaves, timeout, handler)
      }

    /**
     *  Watch the given keys to determine execution of the MULTI/EXEC block
     * @param key     Key to watch
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: RedisTransactions
     */
    def watchL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.watch(key, handler)
      }

    /**
     *  Watch the given keys to determine execution of the MULTI/EXEC block
     * @param keys    List of keys to watch
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: RedisTransactions
     */
    def watchManyL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.watchMany(keys, handler)
      }

    /**
     *  Add one or more members to a sorted set, or update its score if it already exists
     * @param key     Key string
     * @param score   Score used for sorting
     * @param member  New member key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zaddL(key: String, score: Double, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zadd(key, score, member, handler)
      }

    /**
     *  Add one or more members to a sorted set, or update its score if it already exists
     * @param key     Key string
     * @param members New member keys and their scores
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zaddManyL(key: String, members: Map[String,Double]): Task[String] =
      Task.handle[String] { handler =>
        target.zaddMany(key, members, handler)
      }

    /**
     *  Get the number of members in a sorted set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zcardL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.zcard(key, handler)
      }

    /**
     *  Count the members in a sorted set with scores within the given values
     * @param key     Key string
     * @param min     Minimum score
     * @param max     Maximum score
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zcountL(key: String, min: Double, max: Double): Task[String] =
      Task.handle[String] { handler =>
        target.zcount(key, min, max, handler)
      }

    /**
     *  Increment the score of a member in a sorted set
     * @param key       Key string
     * @param increment Increment amount
     * @param member    Member key
     * @param handler   Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zincrbyL(key: String, increment: Double, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zincrby(key, increment, member, handler)
      }

    /**
     *  Intersect multiple sorted sets and store the resulting sorted set in a new key
     * @param destkey Destination key
     * @param sets    List of keys identifying sorted sets to intersect
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zinterstoreL(destkey: String, sets: List[String], options: AggregateOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zinterstore(destkey, sets, options, handler)
      }

    /**
     *  Intersect multiple sorted sets and store the resulting sorted set in a new key using weights for scoring
     * @param destkey Destination key
     * @param sets    List of keys identifying sorted sets to intersect
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zinterstoreWeighedL(destkey: String, sets: Map[String,Double], options: AggregateOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zinterstoreWeighed(destkey, sets, options, handler)
      }

    /**
     *  Count the number of members in a sorted set between a given lexicographical range
     * @param key     Key string
     * @param min     Pattern to compare against for minimum value
     * @param max     Pattern to compare against for maximum value
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zlexcountL(key: String, min: String, max: String): Task[String] =
      Task.handle[String] { handler =>
        target.zlexcount(key, min, max, handler)
      }

    /**
     *  Return a range of members in a sorted set, by index
     * @param key     Key string
     * @param start   Start index for the range
     * @param stop    Stop index for the range - inclusive
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zrangeL(key: String, start: Long, stop: Long): Task[String] =
      Task.handle[String] { handler =>
        target.zrange(key, start, stop, handler)
      }

    /**
     *  Return a range of members in a sorted set, by index
     * @param key     Key string
     * @param start   Start index for the range
     * @param stop    Stop index for the range - inclusive
     * @param options Range options
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zrangeWithOptionsL(key: String, start: Long, stop: Long, options: RangeOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zrangeWithOptions(key, start, stop, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by lexicographical range
     * @param key     Key string
     * @param min     Pattern representing a minimum allowed value
     * @param max     Pattern representing a maximum allowed value
     * @param options Limit options where limit can be specified
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zrangebylexL(key: String, min: String, max: String, options: LimitOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zrangebylex(key, min, max, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by score
     * @param key     Key string
     * @param min     Pattern defining a minimum value
     * @param max     Pattern defining a maximum value
     * @param options Range and limit options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.5
     *  group: sorted_set
     */
    def zrangebyscoreL(key: String, min: String, max: String, options: RangeLimitOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zrangebyscore(key, min, max, options, handler)
      }

    /**
     *  Determine the index of a member in a sorted set
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zrankL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zrank(key, member, handler)
      }

    /**
     *  Remove one member from a sorted set
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zremL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zrem(key, member, handler)
      }

    /**
     *  Remove one or more members from a sorted set
     * @param key     Key string
     * @param members Members in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zremManyL(key: String, members: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.zremMany(key, members, handler)
      }

    /**
     *  Remove all members in a sorted set between the given lexicographical range
     * @param key     Key string
     * @param min     Pattern defining a minimum value
     * @param max     Pattern defining a maximum value
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zremrangebylexL(key: String, min: String, max: String): Task[String] =
      Task.handle[String] { handler =>
        target.zremrangebylex(key, min, max, handler)
      }

    /**
     *  Remove all members in a sorted set within the given indexes
     * @param key     Key string
     * @param start   Start index
     * @param stop    Stop index
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zremrangebyrankL(key: String, start: Long, stop: Long): Task[String] =
      Task.handle[String] { handler =>
        target.zremrangebyrank(key, start, stop, handler)
      }

    /**
     *  Remove all members in a sorted set within the given scores
     * @param key Key string
     * @param min Pattern defining a minimum value
     * @param max Pattern defining a maximum value
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zremrangebyscoreL(key: String, min: String, max: String): Task[String] =
      Task.handle[String] { handler =>
        target.zremrangebyscore(key, min, max, handler)
      }

    /**
     *  Return a range of members in a sorted set, by index, with scores ordered from high to low
     * @param key     Key string
     * @param start   Start index for the range
     * @param stop    Stop index for the range - inclusive
     * @param options Range options
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zrevrangeL(key: String, start: Long, stop: Long, options: RangeOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zrevrange(key, start, stop, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by score, between the given lexicographical range with scores ordered from high to low
     * @param key     Key string
     * @param max     Pattern defining a maximum value
     * @param min     Pattern defining a minimum value
     * @param options Limit options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zrevrangebylexL(key: String, max: String, min: String, options: LimitOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zrevrangebylex(key, max, min, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by score, with scores ordered from high to low
     * @param key     Key string
     * @param max     Pattern defining a maximum value
     * @param min     Pattern defining a minimum value
     * @param options Range and limit options
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: sorted_set
     */
    def zrevrangebyscoreL(key: String, max: String, min: String, options: RangeLimitOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zrevrangebyscore(key, max, min, options, handler)
      }

    /**
     *  Determine the index of a member in a sorted set, with scores ordered from high to low
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zrevrankL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zrevrank(key, member, handler)
      }

    /**
     *  Get the score associated with the given member in a sorted set
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zscoreL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zscore(key, member, handler)
      }

    /**
     *  Add multiple sorted sets and store the resulting sorted set in a new key
     * @param destkey Destination key
     * @param sets    List of keys identifying sorted sets
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zunionstoreL(destkey: String, sets: List[String], options: AggregateOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zunionstore(destkey, sets, options, handler)
      }

    /**
     *  Add multiple sorted sets using weights, and store the resulting sorted set in a new key
     * @param key     Destination key
     * @param sets    Map containing set-key:weight pairs
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zunionstoreWeighedL(key: String, sets: Map[String,Double], options: AggregateOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zunionstoreWeighed(key, sets, options, handler)
      }

    /**
     *  Incrementally iterate the keys space
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: generic
     */
    def scanL(cursor: String, options: ScanOptions): Task[String] =
      Task.handle[String] { handler =>
        target.scan(cursor, options, handler)
      }

    /**
     *  Incrementally iterate Set elements
     * @param key     Key string
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: set
     */
    def sscanL(key: String, cursor: String, options: ScanOptions): Task[String] =
      Task.handle[String] { handler =>
        target.sscan(key, cursor, options, handler)
      }

    /**
     *  Incrementally iterate hash fields and associated values
     * @param key     Key string
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: hash
     */
    def hscanL(key: String, cursor: String, options: ScanOptions): Task[String] =
      Task.handle[String] { handler =>
        target.hscan(key, cursor, options, handler)
      }

    /**
     *  Incrementally iterate sorted sets elements and associated scores
     * @param key     Key string
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: sorted_set
     */
    def zscanL(key: String, cursor: String, options: ScanOptions): Task[String] =
      Task.handle[String] { handler =>
        target.zscan(key, cursor, options, handler)
      }

    /**
     *  Add one or more geospatial items in the geospatial index represented using a sorted set.
     * @param key     Key string
     * @param longitude  longitude
     * @param latitude latitude
     * @param member member
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoaddL(key: String, longitude: Double, latitude: Double, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.geoadd(key, longitude, latitude, member, handler)
      }

    /**
     *  Add one or more geospatial items in the geospatial index represented using a sorted set.
     * @param key     Key string
     * @param members  list of &lt;lon, lat, member&gt;
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoaddManyL(key: String, members: List[GeoMember]): Task[String] =
      Task.handle[String] { handler =>
        target.geoaddMany(key, members, handler)
      }

    /**
     *  Return valid Geohash strings representing the position of one or more elements in a sorted set value representing
     *  a geospatial index (where elements were added using GEOADD).
     * @param key     Key string
     * @param member member
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geohashL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.geohash(key, member, handler)
      }

    /**
     *  Return valid Geohash strings representing the position of one or more elements in a sorted set value representing
     *  a geospatial index (where elements were added using GEOADD).
     * @param key     Key string
     * @param members  list of members
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geohashManyL(key: String, members: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.geohashMany(key, members, handler)
      }

    /**
     *  Return the positions (longitude,latitude) of all the specified members of the geospatial index represented by the
     *  sorted set at key.
     * @param key     Key string
     * @param member member
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoposL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.geopos(key, member, handler)
      }

    /**
     *  Return the positions (longitude,latitude) of all the specified members of the geospatial index represented by the
     *  sorted set at key.
     * @param key     Key string
     * @param members  list of members
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoposManyL(key: String, members: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.geoposMany(key, members, handler)
      }

    /**
     *  Return the distance between two members in the geospatial index represented by the sorted set.
     * @param key     Key string
     * @param member1 member 1
     * @param member2 member 2
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geodistL(key: String, member1: String, member2: String): Task[String] =
      Task.handle[String] { handler =>
        target.geodist(key, member1, member2, handler)
      }

    /**
     *  Return the distance between two members in the geospatial index represented by the sorted set.
     * @param key     Key string
     * @param member1 member 1
     * @param member2 member 2
     * @param unit geo unit
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geodistWithUnitL(key: String, member1: String, member2: String, unit: GeoUnit): Task[String] =
      Task.handle[String] { handler =>
        target.geodistWithUnit(key, member1, member2, unit, handler)
      }

    /**
     *  Return the members of a sorted set populated with geospatial information using GEOADD, which are within the borders
     *  of the area specified with the center location and the maximum distance from the center (the radius).
     * @param key     Key string
     * @param longitude longitude
     * @param latitude latitude
     * @param radius radius
     * @param unit geo unit
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusL(key: String, longitude: Double, latitude: Double, radius: Double, unit: GeoUnit): Task[String] =
      Task.handle[String] { handler =>
        target.georadius(key, longitude, latitude, radius, unit, handler)
      }

    /**
     *  Return the members of a sorted set populated with geospatial information using GEOADD, which are within the borders
     *  of the area specified with the center location and the maximum distance from the center (the radius).
     * @param key     Key string
     * @param longitude longitude
     * @param latitude latitude
     * @param radius radius
     * @param unit geo unit
     * @param options geo radius options
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusWithOptionsL(key: String, longitude: Double, latitude: Double, radius: Double, unit: GeoUnit, options: GeoRadiusOptions): Task[String] =
      Task.handle[String] { handler =>
        target.georadiusWithOptions(key, longitude, latitude, radius, unit, options, handler)
      }

    /**
     *  This command is exactly like GEORADIUS with the sole difference that instead of taking, as the center of the area
     *  to query, a longitude and latitude value, it takes the name of a member already existing inside the geospatial
     *  index represented by the sorted set.
     * @param key     Key string
     * @param member member
     * @param radius radius
     * @param unit geo unit
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusbymemberL(key: String, member: String, radius: Double, unit: GeoUnit): Task[String] =
      Task.handle[String] { handler =>
        target.georadiusbymember(key, member, radius, unit, handler)
      }

    /**
     *  This command is exactly like GEORADIUS with the sole difference that instead of taking, as the center of the area
     *  to query, a longitude and latitude value, it takes the name of a member already existing inside the geospatial
     *  index represented by the sorted set.
     * @param key     Key string
     * @param member member
     * @param radius radius
     * @param unit geo unit
     * @param options geo radius options
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusbymemberWithOptionsL(key: String, member: String, radius: Double, unit: GeoUnit, options: GeoRadiusOptions): Task[String] =
      Task.handle[String] { handler =>
        target.georadiusbymemberWithOptions(key, member, radius, unit, options, handler)
      }

    /**
     *  Delete a key asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     * @param key Key string
     * @since Redis 4.0.0
     *  group: generic
     */
    def unlinkL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.unlink(key, handler)
      }

    /**
     *  Delete multiple keys asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     * @param keys    List of keys to delete
     * @param handler Handler for the result of this call.
     * @since Redis 4.0.0
     *  group: generic
     */
    def unlinkManyL(keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.unlinkMany(keys, handler)
      }

    /**
     *  Swaps two Redis databases
     * @param index1  index of first database to swap
     * @param index2  index of second database to swap
     * @param handler Handler for the result of this call.
     * @since Redis 4.0.0
     *  group: connection
     */
    def swapdbL(index1: Int, index2: Int): Task[String] =
      Task.handle[String] { handler =>
        target.swapdb(index1, index2, handler)
      }
  }


  implicit class VertxRedisOps(val target: Redis) extends AnyVal {

    def pipeToL(dst: WriteStream[Response]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Connects to the redis server.
     * @param handler  the async result handler
     * @return a reference to this, so the API can be used fluently
     */
    def connectL(): Task[Redis] =
      Task.handle[Redis] { handler =>
        target.connect(handler)
      }


    def sendL(command: Request): Task[Response] =
      Task.handle[Response] { onSend =>
        target.send(command, onSend)
      }


    def batchL(commands: List[Request]): Task[List[Response]] =
      Task.handle[List[Response]] { handler =>
        target.batch(commands, handler)
      }
  }


  implicit class VertxRedisClientOps(val target: RedisClient) extends AnyVal {
    /**
     *  Close the client - when it is fully closed the handler will be called.
     * @param handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Append a value to a key
     * @param key     Key string
     * @param value   Value to append
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: string
     */
    def appendL(key: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.append(key, value, handler)
      }.map(out => out: Long)

    /**
     *  Authenticate to the server
     * @param password Password for authentication
     * @param handler  Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: connection
     */
    def authL(password: String): Task[String] =
      Task.handle[String] { handler =>
        target.auth(password, handler)
      }

    /**
     *  Asynchronously rewrite the append-only file
     * @since Redis 1.0.0
     *  group: server
     */
    def bgrewriteaofL(): Task[String] =
      Task.handle[String] { handler =>
        target.bgrewriteaof(handler)
      }

    /**
     *  Asynchronously save the dataset to disk
     * @since Redis 1.0.0
     *  group: server
     */
    def bgsaveL(): Task[String] =
      Task.handle[String] { handler =>
        target.bgsave(handler)
      }

    /**
     *  Count set bits in a string
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def bitcountL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.bitcount(key, handler)
      }.map(out => out: Long)

    /**
     *  Count set bits in a string
     * @param key     Key string
     * @param start   Start index
     * @param end     End index
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def bitcountRangeL(key: String, start: Long, end: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.bitcountRange(key, start, end, handler)
      }.map(out => out: Long)

    /**
     *  Perform bitwise operations between strings
     * @param operation Bitwise operation to perform
     * @param destkey   Destination key where result is stored
     * @param keys      List of keys on which to perform the operation
     * @param handler   Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def bitopL(operation: BitOperation, destkey: String, keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.bitop(operation, destkey, keys, handler)
      }.map(out => out: Long)

    /**
     *  Find first bit set or clear in a string
     * @param key     Key string
     * @param bit     What bit value to look for - must be 1, or 0
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.7
     *  group: string
     */
    def bitposL(key: String, bit: Int): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.bitpos(key, bit, handler)
      }.map(out => out: Long)

    /**
     *  Find first bit set or clear in a string
     * 
     *  See also bitposRange() method, which takes start, and stop offset.
     * @param key     Key string
     * @param bit     What bit value to look for - must be 1, or 0
     * @param start   Start offset
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.7
     *  group: string
     */
    def bitposFromL(key: String, bit: Int, start: Int): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.bitposFrom(key, bit, start, handler)
      }.map(out => out: Long)

    /**
     *  Find first bit set or clear in a string
     * 
     *  Note: when both start, and stop offsets are specified,
     *  behaviour is slightly different than if only start is specified
     * @param key     Key string
     * @param bit     What bit value to look for - must be 1, or 0
     * @param start   Start offset
     * @param stop    End offset - inclusive
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.7
     *  group: string
     */
    def bitposRangeL(key: String, bit: Int, start: Int, stop: Int): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.bitposRange(key, bit, start, stop, handler)
      }.map(out => out: Long)

    /**
     *  Remove and get the first element in a list, or block until one is available
     * @param key     Key string identifying a list to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def blpopL(key: String, seconds: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.blpop(key, seconds, handler)
      }

    /**
     *  Remove and get the first element in any of the lists, or block until one is available
     * @param keys    List of key strings identifying lists to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def blpopManyL(keys: List[String], seconds: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.blpopMany(keys, seconds, handler)
      }

    /**
     *  Remove and get the last element in a list, or block until one is available
     * @param key     Key string identifying a list to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def brpopL(key: String, seconds: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.brpop(key, seconds, handler)
      }

    /**
     *  Remove and get the last element in any of the lists, or block until one is available
     * @param keys    List of key strings identifying lists to watch
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: list
     */
    def brpopManyL(keys: List[String], seconds: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.brpopMany(keys, seconds, handler)
      }

    /**
     *  Pop a value from a list, push it to another list and return it; or block until one is available
     * @param key     Key string identifying the source list
     * @param destkey Key string identifying the destination list
     * @param seconds Timeout in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def brpoplpushL(key: String, destkey: String, seconds: Int): Task[String] =
      Task.handle[String] { handler =>
        target.brpoplpush(key, destkey, seconds, handler)
      }

    /**
     *  Kill the connection of a client
     * @param filter  Filter options
     * @param handler Handler for the result of this call.
     * @since Redis 2.4.0
     *  group: server
     */
    def clientKillL(filter: KillFilter): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.clientKill(filter, handler)
      }.map(out => out: Long)

    /**
     *  Get the list of client connections
     * @since Redis 2.4.0
     *  group: server
     */
    def clientListL(): Task[String] =
      Task.handle[String] { handler =>
        target.clientList(handler)
      }

    /**
     *  Get the current connection name
     * @since Redis 2.6.9
     *  group: server
     */
    def clientGetnameL(): Task[String] =
      Task.handle[String] { handler =>
        target.clientGetname(handler)
      }

    /**
     *  Stop processing commands from clients for some time
     * @param millis  Pause time in milliseconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.9.50
     *  group: server
     */
    def clientPauseL(millis: Long): Task[String] =
      Task.handle[String] { handler =>
        target.clientPause(millis, handler)
      }

    /**
     *  Set the current connection name
     * @param name    New name for current connection
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.9
     *  group: server
     */
    def clientSetnameL(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.clientSetname(name, handler)
      }

    /**
     *  Assign new hash slots to receiving node.
     * @param slots
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: server
     */
    def clusterAddslotsL(slots: List[Long]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterAddslots(slots, handler)
      }.map(_ => ())

    /**
     *  Return the number of failure reports active for a given node.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterCountFailureReportsL(nodeId: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.clusterCountFailureReports(nodeId, handler)
      }.map(out => out: Long)

    /**
     *  Return the number of local keys in the specified hash slot.
     * @param slot
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterCountkeysinslotL(slot: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.clusterCountkeysinslot(slot, handler)
      }.map(out => out: Long)

    /**
     *  Set hash slots as unbound in receiving node.
     * @param slot
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterDelslotsL(slot: Long): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterDelslots(slot, handler)
      }.map(_ => ())

    /**
     *  Set hash slots as unbound in receiving node.
     * @param slots
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterDelslotsManyL(slots: List[Long]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterDelslotsMany(slots, handler)
      }.map(_ => ())

    /**
     *  Forces a slave to perform a manual failover of its master.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterFailoverL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterFailover(handler)
      }.map(_ => ())

    /**
     *  Forces a slave to perform a manual failover of its master.
     * @param options
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterFailOverWithOptionsL(options: FailoverOptions): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterFailOverWithOptions(options, handler)
      }.map(_ => ())

    /**
     *  Remove a node from the nodes table.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterForgetL(nodeId: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterForget(nodeId, handler)
      }.map(_ => ())

    /**
     *  Return local key names in the specified hash slot.
     * @param slot
     * @param count
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterGetkeysinslotL(slot: Long, count: Long): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.clusterGetkeysinslot(slot, count, handler)
      }

    /**
     *  Provides info about Redis Cluster node state.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterInfoL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.clusterInfo(handler)
      }

    /**
     *  Returns the hash slot of the specified key.
     * @param key
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterKeyslotL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.clusterKeyslot(key, handler)
      }.map(out => out: Long)

    /**
     *  Force a node cluster to handshake with another node.
     * @param ip
     * @param port
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterMeetL(ip: String, port: Long): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterMeet(ip, port, handler)
      }.map(_ => ())

    /**
     *  Get Cluster config for the node.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterNodesL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.clusterNodes(handler)
      }

    /**
     *  Reconfigure a node as a slave of the specified master node.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterReplicateL(nodeId: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterReplicate(nodeId, handler)
      }.map(_ => ())

    /**
     *  Reset a Redis Cluster node.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterResetL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterReset(handler)
      }.map(_ => ())

    /**
     *  Reset a Redis Cluster node.
     * @param options
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterResetWithOptionsL(options: ResetOptions): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterResetWithOptions(options, handler)
      }.map(_ => ())

    /**
     *  Forces the node to save cluster state on disk.
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSaveconfigL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterSaveconfig(handler)
      }.map(_ => ())

    /**
     *  Set the configuration epoch in a new node.
     * @param epoch
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSetConfigEpochL(epoch: Long): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterSetConfigEpoch(epoch, handler)
      }.map(_ => ())

    /**
     *  Bind an hash slot to a specific node.
     * @param slot
     * @param subcommand
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSetslotL(slot: Long, subcommand: SlotCmd): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterSetslot(slot, subcommand, handler)
      }.map(_ => ())

    /**
     *  Bind an hash slot to a specific node.
     * @param slot
     * @param subcommand
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSetslotWithNodeL(slot: Long, subcommand: SlotCmd, nodeId: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.clusterSetslotWithNode(slot, subcommand, nodeId, handler)
      }.map(_ => ())

    /**
     *  List slave nodes of the specified master node.
     * @param nodeId
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: cluster
     */
    def clusterSlavesL(nodeId: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.clusterSlaves(nodeId, handler)
      }

    /**
     *  Get array of Cluster slot to node mappings
     * @since Redis 3.0.0
     *  group: server
     */
    def clusterSlotsL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.clusterSlots(handler)
      }

    /**
     *  Get array of Redis command details
     * @since Redis 2.8.13
     *  group: server
     */
    def commandL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.command(handler)
      }

    /**
     *  Get total number of Redis commands
     * @since Redis 2.8.13
     *  group: server
     */
    def commandCountL(): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.commandCount(handler)
      }.map(out => out: Long)

    /**
     *  Extract keys given a full Redis command
     * @since Redis 2.8.13
     *  group: server
     */
    def commandGetkeysL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.commandGetkeys(handler)
      }

    /**
     *  Get array of specific Redis command details
     * @param commands List of commands to get info for
     * @param handler  Handler for the result of this call.
     * @since Redis 2.8.13
     *  group: server
     */
    def commandInfoL(commands: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.commandInfo(commands, handler)
      }

    /**
     *  Get the value of a configuration parameter
     * @param parameter Configuration parameter
     * @param handler   Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: server
     */
    def configGetL(parameter: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.configGet(parameter, handler)
      }

    /**
     *  Rewrite the configuration file with the in memory configuration
     * @since Redis 2.8.0
     *  group: server
     */
    def configRewriteL(): Task[String] =
      Task.handle[String] { handler =>
        target.configRewrite(handler)
      }

    /**
     *  Set a configuration parameter to the given value
     * @param parameter Configuration parameter
     * @param value     New value
     * @param handler   Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: server
     */
    def configSetL(parameter: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.configSet(parameter, value, handler)
      }

    /**
     *  Reset the stats returned by INFO
     * @since Redis 2.0.0
     *  group: server
     */
    def configResetstatL(): Task[String] =
      Task.handle[String] { handler =>
        target.configResetstat(handler)
      }

    /**
     *  Return the number of keys in the selected database
     * @since Redis 1.0.0
     *  group: server
     */
    def dbsizeL(): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.dbsize(handler)
      }.map(out => out: Long)

    /**
     *  Get debugging information about a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def debugObjectL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.debugObject(key, handler)
      }

    /**
     *  Make the server crash
     * @since Redis 1.0.0
     *  group: server
     */
    def debugSegfaultL(): Task[String] =
      Task.handle[String] { handler =>
        target.debugSegfault(handler)
      }

    /**
     *  Decrement the integer value of a key by one
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def decrL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.decr(key, handler)
      }.map(out => out: Long)

    /**
     *  Decrement the integer value of a key by the given number
     * @param key       Key string
     * @param decrement Value by which to decrement
     * @param handler   Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def decrbyL(key: String, decrement: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.decrby(key, decrement, handler)
      }.map(out => out: Long)

    /**
     *  Delete a key
     * @param key     Keys to delete
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def delL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.del(key, handler)
      }.map(out => out: Long)

    /**
     *  Delete many keys
     * @param keys    List of keys to delete
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def delManyL(keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.delMany(keys, handler)
      }.map(out => out: Long)

    /**
     *  Return a serialized version of the value stored at the specified key.
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def dumpL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.dump(key, handler)
      }

    /**
     *  Echo the given string
     * @param message String to echo
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: connection
     */
    def echoL(message: String): Task[String] =
      Task.handle[String] { handler =>
        target.echo(message, handler)
      }

    /**
     *  Execute a Lua script server side. Due to the dynamic nature of this command any response type could be returned
     *  for This reason and to ensure type safety the reply is always guaranteed to be a JsonArray.
     * 
     *  When a reply if for example a String the handler will be called with a JsonArray with a single element containing
     *  the String.
     * @param script  Lua script to evaluate
     * @param keys    List of keys
     * @param args    List of argument values
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def evalL(script: String, keys: List[String], args: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.eval(script, keys, args, handler)
      }

    /**
     *  Execute a Lua script server side. Due to the dynamic nature of this command any response type could be returned
     *  for This reason and to ensure type safety the reply is always guaranteed to be a JsonArray.
     * 
     *  When a reply if for example a String the handler will be called with a JsonArray with a single element containing
     *  the String.
     * @param sha1    SHA1 digest of the script cached on the server
     * @param keys    List of keys
     * @param values  List of values
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def evalshaL(sha1: String, keys: List[String], values: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.evalsha(sha1, keys, values, handler)
      }

    /**
     *  Execute a Lua script server side. This method is a high level wrapper around EVAL and EVALSHA
     *  using the latter if possible, falling back to EVAL if the script is not cached by the server yet.
     *  According to Redis documentation, executed scripts are guaranteed to be in the script cache of a
     *  given execution of a Redis instance forever, which means typically the overhead incurred by
     *  optimistically sending EVALSHA is minimal, while improving performance and saving bandwidth
     *  compared to using EVAL every time.
     * @see <a href="https://redis.io/commands/eval#script-cache-semantics">Redis - Script cache semantics</a>
     * @param script  Lua script and its SHA1 digest
     * @param keys    List of keys
     * @param args    List of argument values
     * @param handler Handler for the result of this call.
     *  group: scripting
     */
    def evalScriptL(script: Script, keys: List[String], args: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.evalScript(script, keys, args, handler)
      }

    /**
     *  Determine if a key exists
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def existsL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.exists(key, handler)
      }.map(out => out: Long)

    /**
     *  Determine if one or many keys exist
     * @param keys    List of key strings
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.3
     *  group: generic
     */
    def existsManyL(keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.existsMany(keys, handler)
      }.map(out => out: Long)

    /**
     *  Set a key's time to live in seconds
     * @param key     Key string
     * @param seconds Time to live in seconds
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def expireL(key: String, seconds: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.expire(key, seconds, handler)
      }.map(out => out: Long)

    /**
     *  Set the expiration for a key as a UNIX timestamp
     * @param key       Key string
     * @param seconds   Expiry time as Unix timestamp in seconds
     * @param handler   Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: generic
     */
    def expireatL(key: String, seconds: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.expireat(key, seconds, handler)
      }.map(out => out: Long)

    /**
     *  Remove all keys from all databases
     * @since Redis 1.0.0
     *  group: server
     */
    def flushallL(): Task[String] =
      Task.handle[String] { handler =>
        target.flushall(handler)
      }

    /**
     *  Remove all keys from the current database
     * @since Redis 1.0.0
     *  group: server
     */
    def flushdbL(): Task[String] =
      Task.handle[String] { handler =>
        target.flushdb(handler)
      }

    /**
     *  Get the value of a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def getL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.get(key, handler)
      }

    /**
     *  Get the value of a key - without decoding as utf-8
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def getBinaryL(key: String): Task[Buffer] =
      Task.handle[Buffer] { handler =>
        target.getBinary(key, handler)
      }

    /**
     *  Returns the bit value at offset in the string value stored at key
     * @param key     Key string
     * @param offset  Offset in bits
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def getbitL(key: String, offset: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.getbit(key, offset, handler)
      }.map(out => out: Long)

    /**
     *  Get a substring of the string stored at a key
     * @param key     Key string
     * @param start   Start offset
     * @param end     End offset - inclusive
     * @param handler Handler for the result of this call.
     * @since Redis 2.4.0
     *  group: string
     */
    def getrangeL(key: String, start: Long, end: Long): Task[String] =
      Task.handle[String] { handler =>
        target.getrange(key, start, end, handler)
      }

    /**
     *  Set the string value of a key and return its old value
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def getsetL(key: String, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.getset(key, value, handler)
      }

    /**
     *  Delete one or more hash fields
     * @param key     Key string
     * @param field   Field name
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hdelL(key: String, field: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hdel(key, field, handler)
      }.map(out => out: Long)

    /**
     *  Delete one or more hash fields
     * @param key     Key string
     * @param fields  Field names
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hdelManyL(key: String, fields: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hdelMany(key, fields, handler)
      }.map(out => out: Long)

    /**
     *  Determine if a hash field exists
     * @param key     Key string
     * @param field   Field name
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hexistsL(key: String, field: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hexists(key, field, handler)
      }.map(out => out: Long)

    /**
     *  Get the value of a hash field
     * @param key     Key string
     * @param field   Field name
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hgetL(key: String, field: String): Task[String] =
      Task.handle[String] { handler =>
        target.hget(key, field, handler)
      }

    /**
     *  Get all the fields and values in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hgetallL(key: String): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.hgetall(key, handler)
      }

    /**
     *  Increment the integer value of a hash field by the given number
     * @param key       Key string
     * @param field     Field name
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hincrbyL(key: String, field: String, increment: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hincrby(key, field, increment, handler)
      }.map(out => out: Long)

    /**
     *  Increment the float value of a hash field by the given amount
     * @param key       Key string
     * @param field     Field name
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: hash
     */
    def hincrbyfloatL(key: String, field: String, increment: Double): Task[String] =
      Task.handle[String] { handler =>
        target.hincrbyfloat(key, field, increment, handler)
      }

    /**
     *  Get all the fields in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hkeysL(key: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.hkeys(key, handler)
      }

    /**
     *  Get the number of fields in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hlenL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hlen(key, handler)
      }.map(out => out: Long)

    /**
     *  Get the values of all the given hash fields
     * @param key     Key string
     * @param fields  Field names
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hmgetL(key: String, fields: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.hmget(key, fields, handler)
      }

    /**
     *  Set multiple hash fields to multiple values
     * @param key     Key string
     * @param values  Map of field:value pairs
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hmsetL(key: String, values: JsonObject): Task[String] =
      Task.handle[String] { handler =>
        target.hmset(key, values, handler)
      }

    /**
     *  Set the string value of a hash field
     * @param key     Key string
     * @param field   Field name
     * @param value   New value
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hsetL(key: String, field: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hset(key, field, value, handler)
      }.map(out => out: Long)

    /**
     *  Set the value of a hash field, only if the field does not exist
     * @param key     Key string
     * @param field   Field name
     * @param value   New value
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hsetnxL(key: String, field: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hsetnx(key, field, value, handler)
      }.map(out => out: Long)

    /**
     *  Get all the values in a hash
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: hash
     */
    def hvalsL(key: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.hvals(key, handler)
      }

    /**
     *  Increment the integer value of a key by one
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def incrL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.incr(key, handler)
      }.map(out => out: Long)

    /**
     *  Increment the integer value of a key by the given amount
     * @param key       Key string
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def incrbyL(key: String, increment: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.incrby(key, increment, handler)
      }.map(out => out: Long)

    /**
     *  Increment the float value of a key by the given amount
     * @param key       Key string
     * @param increment Value by which to increment
     * @param handler   Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def incrbyfloatL(key: String, increment: Double): Task[String] =
      Task.handle[String] { handler =>
        target.incrbyfloat(key, increment, handler)
      }

    /**
     *  Get information and statistics about the server
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def infoL(): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.info(handler)
      }

    /**
     *  Get information and statistics about the server
     * @param section Specific section of information to return
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def infoSectionL(section: String): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.infoSection(section, handler)
      }

    /**
     *  Find all keys matching the given pattern
     * @param pattern Pattern to limit the keys returned
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def keysL(pattern: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.keys(pattern, handler)
      }

    /**
     *  Get the UNIX time stamp of the last successful save to disk
     * @since Redis 1.0.0
     *  group: server
     */
    def lastsaveL(): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.lastsave(handler)
      }.map(out => out: Long)

    /**
     *  Get an element from a list by its index
     * @param key     Key string
     * @param index   Index of list element to get
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lindexL(key: String, index: Int): Task[String] =
      Task.handle[String] { handler =>
        target.lindex(key, index, handler)
      }

    /**
     *  Insert an element before or after another element in a list
     * @param key     Key string
     * @param option  BEFORE or AFTER
     * @param pivot   Key to use as a pivot
     * @param value   Value to be inserted before or after the pivot
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def linsertL(key: String, option: InsertOptions, pivot: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.linsert(key, option, pivot, value, handler)
      }.map(out => out: Long)

    /**
     *  Get the length of a list
     * @param key     String key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def llenL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.llen(key, handler)
      }.map(out => out: Long)

    /**
     *  Remove and get the first element in a list
     * @param key     String key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lpopL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.lpop(key, handler)
      }

    /**
     *  Prepend one or multiple values to a list
     * @param key     Key string
     * @param values  Values to be added at the beginning of the list, one by one
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lpushManyL(key: String, values: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.lpushMany(key, values, handler)
      }.map(out => out: Long)

    /**
     *  Prepend one value to a list
     * @param key     Key string
     * @param value   Value to be added at the beginning of the list
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lpushL(key: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.lpush(key, value, handler)
      }.map(out => out: Long)

    /**
     *  Prepend a value to a list, only if the list exists
     * @param key     Key string
     * @param value   Value to add at the beginning of the list
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def lpushxL(key: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.lpushx(key, value, handler)
      }.map(out => out: Long)

    /**
     *  Get a range of elements from a list
     * @param key     Key string
     * @param from    Start index
     * @param to      Stop index
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lrangeL(key: String, from: Long, to: Long): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.lrange(key, from, to, handler)
      }

    /**
     *  Remove elements from a list
     * @param key     Key string
     * @param count   Number of first found occurrences equal to $value to remove from the list
     * @param value   Value to be removed
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lremL(key: String, count: Long, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.lrem(key, count, value, handler)
      }.map(out => out: Long)

    /**
     *  Set the value of an element in a list by its index
     * @param key     Key string
     * @param index   Position within list
     * @param value   New value
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def lsetL(key: String, index: Long, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.lset(key, index, value, handler)
      }

    /**
     *  Trim a list to the specified range
     * @param key     Key string
     * @param from    Start index
     * @param to      Stop index
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def ltrimL(key: String, from: Long, to: Long): Task[String] =
      Task.handle[String] { handler =>
        target.ltrim(key, from, to, handler)
      }

    /**
     *  Get the value of the given key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def mgetL(key: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.mget(key, handler)
      }

    /**
     *  Get the values of all the given keys
     * @param keys    List of keys to get
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def mgetManyL(keys: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.mgetMany(keys, handler)
      }

    /**
     *  Atomically transfer a key from a Redis instance to another one.
     * @param host    Destination host
     * @param port    Destination port
     * @param key     Key to migrate
     * @param destdb  Destination database index
     * @param options Migrate options
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def migrateL(host: String, port: Int, key: String, destdb: Int, timeout: Long, options: MigrateOptions): Task[String] =
      Task.handle[String] { handler =>
        target.migrate(host, port, key, destdb, timeout, options, handler)
      }

    /**
     *  Listen for all requests received by the server in real time
     * @since Redis 1.0.0
     *  group: server
     */
    def monitorL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.monitor(handler)
      }.map(_ => ())

    /**
     *  Move a key to another database
     * @param key     Key to migrate
     * @param destdb  Destination database index
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def moveL(key: String, destdb: Int): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.move(key, destdb, handler)
      }.map(out => out: Long)

    /**
     *  Set multiple keys to multiple values
     * @param keyvals Key value pairs to set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.1
     *  group: string
     */
    def msetL(keyvals: JsonObject): Task[String] =
      Task.handle[String] { handler =>
        target.mset(keyvals, handler)
      }

    /**
     *  Set multiple keys to multiple values, only if none of the keys exist
     * @param keyvals Key value pairs to set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.1
     *  group: string
     */
    def msetnxL(keyvals: JsonObject): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.msetnx(keyvals, handler)
      }.map(out => out: Long)

    /**
     *  Inspect the internals of Redis objects
     * @param key     Key string
     * @param cmd     Object sub command
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.3
     *  group: generic
     */
    def objectL(key: String, cmd: ObjectCmd): Task[Unit] =
      Task.handle[Void] { handler =>
        target.`object`(key, cmd, handler)
      }.map(_ => ())

    /**
     *  Remove the expiration from a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: generic
     */
    def persistL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.persist(key, handler)
      }.map(out => out: Long)

    /**
     *  Set a key's time to live in milliseconds
     * @param key     String key
     * @param millis  Time to live in milliseconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def pexpireL(key: String, millis: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pexpire(key, millis, handler)
      }.map(out => out: Long)

    /**
     *  Set the expiration for a key as a UNIX timestamp specified in milliseconds
     * @param key     Key string
     * @param millis  Expiry time as Unix timestamp in milliseconds
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def pexpireatL(key: String, millis: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pexpireat(key, millis, handler)
      }.map(out => out: Long)

    /**
     *  Adds the specified element to the specified HyperLogLog.
     * @param key     Key string
     * @param element Element to add
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfaddL(key: String, element: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pfadd(key, element, handler)
      }.map(out => out: Long)

    /**
     *  Adds the specified elements to the specified HyperLogLog.
     * @param key      Key string
     * @param elements Elementa to add
     * @param handler  Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfaddManyL(key: String, elements: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pfaddMany(key, elements, handler)
      }.map(out => out: Long)

    /**
     *  Return the approximated cardinality of the set observed by the HyperLogLog at key.
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfcountL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pfcount(key, handler)
      }.map(out => out: Long)

    /**
     *  Return the approximated cardinality of the set(s) observed by the HyperLogLog at key(s).
     * @param keys    List of keys
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfcountManyL(keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pfcountMany(keys, handler)
      }.map(out => out: Long)

    /**
     *  Merge N different HyperLogLogs into a single one.
     * @param destkey Destination key
     * @param keys    List of source keys
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: hyperloglog
     */
    def pfmergeL(destkey: String, keys: List[String]): Task[String] =
      Task.handle[String] { handler =>
        target.pfmerge(destkey, keys, handler)
      }

    /**
     *  Ping the server
     * @since Redis 1.0.0
     *  group: connection
     */
    def pingL(): Task[String] =
      Task.handle[String] { handler =>
        target.ping(handler)
      }

    /**
     *  Set the value and expiration in milliseconds of a key
     * @param key     Key string
     * @param millis  Number of milliseconds until the key expires
     * @param value   New value for key
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: string
     */
    def psetexL(key: String, millis: Long, value: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.psetex(key, millis, value, handler)
      }.map(_ => ())

    /**
     *  Listen for messages published to channels matching the given pattern
     * @param pattern Pattern string
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def psubscribeL(pattern: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.psubscribe(pattern, handler)
      }

    /**
     *  Listen for messages published to channels matching the given patterns
     * @param patterns List of patterns
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def psubscribeManyL(patterns: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.psubscribeMany(patterns, handler)
      }

    /**
     *  Lists the currently active channels - only those matching the pattern
     * @param pattern A glob-style pattern - an empty string means no pattern
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: pubsub
     */
    def pubsubChannelsL(pattern: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.pubsubChannels(pattern, handler)
      }

    /**
     *  Returns the number of subscribers (not counting clients subscribed to patterns) for the specified channels
     * @param channels List of channels
     * @param handler  Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: pubsub
     */
    def pubsubNumsubL(channels: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.pubsubNumsub(channels, handler)
      }

    /**
     *  Returns the number of subscriptions to patterns (that are performed using the PSUBSCRIBE command)
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: pubsub
     */
    def pubsubNumpatL(): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pubsubNumpat(handler)
      }.map(out => out: Long)

    /**
     *  Get the time to live for a key in milliseconds
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def pttlL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.pttl(key, handler)
      }.map(out => out: Long)

    /**
     *  Post a message to a channel
     * @param channel Channel key
     * @param message Message to send to channel
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def publishL(channel: String, message: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.publish(channel, message, handler)
      }.map(out => out: Long)

    /**
     *  Stop listening for messages posted to channels matching the given patterns
     * @param patterns List of patterns to match against
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def punsubscribeL(patterns: List[String]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.punsubscribe(patterns, handler)
      }.map(_ => ())

    /**
     *  Return a random key from the keyspace
     * @since Redis 1.0.0
     *  group: generic
     */
    def randomkeyL(): Task[String] =
      Task.handle[String] { handler =>
        target.randomkey(handler)
      }

    /**
     *  Rename a key
     * @param key  Key string to be renamed
     * @param newkey  New key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def renameL(key: String, newkey: String): Task[String] =
      Task.handle[String] { handler =>
        target.rename(key, newkey, handler)
      }

    /**
     *  Rename a key, only if the new key does not exist
     * @param key  Key string to be renamed
     * @param newkey  New key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def renamenxL(key: String, newkey: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.renamenx(key, newkey, handler)
      }.map(out => out: Long)

    /**
     *  Create a key using the provided serialized value, previously obtained using DUMP.
     * @param key        Key string
     * @param millis     Expiry time in milliseconds to set on the key
     * @param serialized Serialized form of the key value as obtained using DUMP
     * @param handler    Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: generic
     */
    def restoreL(key: String, millis: Long, serialized: String): Task[String] =
      Task.handle[String] { handler =>
        target.restore(key, millis, serialized, handler)
      }

    /**
     *  Return the role of the instance in the context of replication
     * @since Redis 2.8.12
     *  group: server
     */
    def roleL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.role(handler)
      }

    /**
     *  Remove and get the last element in a list
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def rpopL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.rpop(key, handler)
      }

    /**
     *  Remove the last element in a list, append it to another list and return it
     * @param key     Key string identifying source list
     * @param destkey Key string identifying destination list
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: list
     */
    def rpoplpushL(key: String, destkey: String): Task[String] =
      Task.handle[String] { handler =>
        target.rpoplpush(key, destkey, handler)
      }

    /**
     *  Append one or multiple values to a list
     * @param key     Key string
     * @param values  List of values to add to the end of the list
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def rpushManyL(key: String, values: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.rpushMany(key, values, handler)
      }.map(out => out: Long)

    /**
     *  Append one or multiple values to a list
     * @param key     Key string
     * @param value   Value to be added to the end of the list
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: list
     */
    def rpushL(key: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.rpush(key, value, handler)
      }.map(out => out: Long)

    /**
     *  Append a value to a list, only if the list exists
     * @param key     Key string
     * @param value   Value to be added to the end of the list
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: list
     */
    def rpushxL(key: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.rpushx(key, value, handler)
      }.map(out => out: Long)

    /**
     *  Add a member to a set
     * @param key     Key string
     * @param member  Value to be added to the set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def saddL(key: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.sadd(key, member, handler)
      }.map(out => out: Long)

    /**
     *  Add one or more members to a set
     * @param key     Key string
     * @param members Values to be added to the set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def saddManyL(key: String, members: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.saddMany(key, members, handler)
      }.map(out => out: Long)

    /**
     *  Synchronously save the dataset to disk
     * @since Redis 1.0.0
     *  group: server
     */
    def saveL(): Task[String] =
      Task.handle[String] { handler =>
        target.save(handler)
      }

    /**
     *  Get the number of members in a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def scardL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.scard(key, handler)
      }.map(out => out: Long)

    /**
     *  Check existence of script in the script cache.
     * @param script  SHA1 digest identifying a script in the script cache
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptExistsL(script: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.scriptExists(script, handler)
      }

    /**
     *  Check existence of scripts in the script cache.
     * @param scripts List of SHA1 digests identifying scripts in the script cache
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptExistsManyL(scripts: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.scriptExistsMany(scripts, handler)
      }

    /**
     *  Remove all the scripts from the script cache.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptFlushL(): Task[String] =
      Task.handle[String] { handler =>
        target.scriptFlush(handler)
      }

    /**
     *  Kill the script currently in execution.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptKillL(): Task[String] =
      Task.handle[String] { handler =>
        target.scriptKill(handler)
      }

    /**
     *  Load the specified Lua script into the script cache.
     * @param script  Lua script
     * @param handler Handler for the result of this call.
     * @since Redis 2.6.0
     *  group: scripting
     */
    def scriptLoadL(script: String): Task[String] =
      Task.handle[String] { handler =>
        target.scriptLoad(script, handler)
      }

    /**
     *  Subtract multiple sets
     * @param key     Key identifying the set to compare with all other sets combined
     * @param cmpkeys List of keys identifying sets to subtract from the key set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sdiffL(key: String, cmpkeys: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.sdiff(key, cmpkeys, handler)
      }

    /**
     *  Subtract multiple sets and store the resulting set in a key
     * @param destkey Destination key where the result should be stored
     * @param key     Key identifying the set to compare with all other sets combined
     * @param cmpkeys List of keys identifying sets to subtract from the key set
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sdiffstoreL(destkey: String, key: String, cmpkeys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.sdiffstore(destkey, key, cmpkeys, handler)
      }.map(out => out: Long)

    /**
     *  Change the selected database for the current connection
     * @param dbindex Index identifying the new active database
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: connection
     */
    def selectL(dbindex: Int): Task[String] =
      Task.handle[String] { handler =>
        target.select(dbindex, handler)
      }

    /**
     *  Set the string value of a key
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setL(key: String, value: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.set(key, value, handler)
      }.map(_ => ())

    /**
     *  Set the string value of a key
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param options Set options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setWithOptionsL(key: String, value: String, options: SetOptions): Task[String] =
      Task.handle[String] { handler =>
        target.setWithOptions(key, value, options, handler)
      }

    /**
     *  Set the binary string value of a key - without encoding as utf-8
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setBinaryL(key: String, value: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.setBinary(key, value, handler)
      }.map(_ => ())

    /**
     *  Set the string value of a key
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param options Set options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setBinaryWithOptionsL(key: String, value: Buffer, options: SetOptions): Task[Unit] =
      Task.handle[Void] { handler =>
        target.setBinaryWithOptions(key, value, options, handler)
      }.map(_ => ())

    /**
     *  Sets or clears the bit at offset in the string value stored at key
     * @param key     Key string
     * @param offset  Bit offset
     * @param bit     New value - must be 1 or 0
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def setbitL(key: String, offset: Long, bit: Int): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.setbit(key, offset, bit, handler)
      }.map(out => out: Long)

    /**
     *  Set the value and expiration of a key
     * @param key     Key string
     * @param seconds Number of seconds until the key expires
     * @param value   New value for key
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: string
     */
    def setexL(key: String, seconds: Long, value: String): Task[String] =
      Task.handle[String] { handler =>
        target.setex(key, seconds, value, handler)
      }

    /**
     *  Set the value of a key, only if the key does not exist
     * @param key     Key of which value to set
     * @param value   New value for the key
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: string
     */
    def setnxL(key: String, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.setnx(key, value, handler)
      }.map(out => out: Long)

    /**
     *  Overwrite part of a string at key starting at the specified offset
     * @param key     Key string
     * @param offset  Offset - the maximum offset that you can set is 2^29 -1 (536870911), as Redis Strings are limited to 512 megabytes
     * @param value   Value to overwrite with
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def setrangeL(key: String, offset: Int, value: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.setrange(key, offset, value, handler)
      }.map(out => out: Long)

    /**
     *  Intersect multiple sets
     * @param keys    List of keys to perform intersection on
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sinterL(keys: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.sinter(keys, handler)
      }

    /**
     *  Intersect multiple sets and store the resulting set in a key
     * @param destkey Key where to store the results
     * @param keys    List of keys to perform intersection on
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sinterstoreL(destkey: String, keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.sinterstore(destkey, keys, handler)
      }.map(out => out: Long)

    /**
     *  Determine if a given value is a member of a set
     * @param key     Key string
     * @param member  Member to look for
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sismemberL(key: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.sismember(key, member, handler)
      }.map(out => out: Long)

    /**
     *  Make the server a slave of another instance
     * @param host    Host to become this server's master
     * @param port    Port of our new master
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def slaveofL(host: String, port: Int): Task[String] =
      Task.handle[String] { handler =>
        target.slaveof(host, port, handler)
      }

    /**
     *  Make this server a master
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: server
     */
    def slaveofNooneL(): Task[String] =
      Task.handle[String] { handler =>
        target.slaveofNoone(handler)
      }

    /**
     *  Read the Redis slow queries log
     * @param limit   Number of log entries to return. If value is less than zero all entries are returned
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.12
     *  group: server
     */
    def slowlogGetL(limit: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.slowlogGet(limit, handler)
      }

    /**
     *  Get the length of the Redis slow queries log
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.12
     *  group: server
     */
    def slowlogLenL(): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.slowlogLen(handler)
      }.map(out => out: Long)

    /**
     *  Reset the Redis slow queries log
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.12
     *  group: server
     */
    def slowlogResetL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.slowlogReset(handler)
      }.map(_ => ())

    /**
     *  Get all the members in a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def smembersL(key: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.smembers(key, handler)
      }

    /**
     *  Move a member from one set to another
     * @param key     Key of source set currently containing the member
     * @param destkey Key identifying the destination set
     * @param member   Member to move
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def smoveL(key: String, destkey: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.smove(key, destkey, member, handler)
      }.map(out => out: Long)

    /**
     *  Sort the elements in a list, set or sorted set
     * @param key     Key string
     * @param options Sort options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def sortL(key: String, options: SortOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.sort(key, options, handler)
      }

    /**
     *  Remove and return a random member from a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def spopL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.spop(key, handler)
      }

    /**
     *  Remove and return random members from a set
     * @param key     Key string
     * @param count   Number of members to remove
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def spopManyL(key: String, count: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.spopMany(key, count, handler)
      }

    /**
     *  Get one or multiple random members from a set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def srandmemberL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.srandmember(key, handler)
      }

    /**
     *  Get one or multiple random members from a set
     * @param key     Key string
     * @param count   Number of members to get
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def srandmemberCountL(key: String, count: Int): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.srandmemberCount(key, count, handler)
      }

    /**
     *  Remove one member from a set
     * @param key     Key string
     * @param member  Member to remove
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sremL(key: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.srem(key, member, handler)
      }.map(out => out: Long)

    /**
     *  Remove one or more members from a set
     * @param key     Key string
     * @param members Members to remove
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sremManyL(key: String, members: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.sremMany(key, members, handler)
      }.map(out => out: Long)

    /**
     *  Get the length of the value stored in a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: string
     */
    def strlenL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.strlen(key, handler)
      }.map(out => out: Long)

    /**
     *  Listen for messages published to the given channels
     * @param channel Channel to subscribe to
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def subscribeL(channel: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.subscribe(channel, handler)
      }

    /**
     *  Listen for messages published to the given channels
     * @param channels List of channels to subscribe to
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def subscribeManyL(channels: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.subscribeMany(channels, handler)
      }

    /**
     *  Add multiple sets
     * @param keys    List of keys identifying sets to add up
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sunionL(keys: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.sunion(keys, handler)
      }

    /**
     *  Add multiple sets and store the resulting set in a key
     * @param destkey Destination key
     * @param keys    List of keys identifying sets to add up
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: set
     */
    def sunionstoreL(destkey: String, keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.sunionstore(destkey, keys, handler)
      }.map(out => out: Long)

    /**
     *  Internal command used for replication
     * @since Redis 1.0.0
     *  group: server
     */
    def syncL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.sync(handler)
      }.map(_ => ())

    /**
     *  Return the current server time
     * @since Redis 2.6.0
     *  group: server
     */
    def timeL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.time(handler)
      }

    /**
     *  Get the time to live for a key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def ttlL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.ttl(key, handler)
      }.map(out => out: Long)

    /**
     *  Determine the type stored at key
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.0
     *  group: generic
     */
    def typeL(key: String): Task[String] =
      Task.handle[String] { handler =>
        target.`type`(key, handler)
      }

    /**
     *  Stop listening for messages posted to the given channels
     * @param channels List of channels to subscribe to
     * @param handler  Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: pubsub
     */
    def unsubscribeL(channels: List[String]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.unsubscribe(channels, handler)
      }.map(_ => ())

    /**
     *  Wait for the synchronous replication of all the write commands sent in the context of the current connection.
     * @param numSlaves
     * @param timeout
     * @param handler Handler for the result of this call.
     * @since Redis 3.0.0
     *  group: generic
     */
    def waitL(numSlaves: Long, timeout: Long): Task[String] =
      Task.handle[String] { handler =>
        target.wait(numSlaves, timeout, handler)
      }

    /**
     *  Add one or more members to a sorted set, or update its score if it already exists
     * @param key     Key string
     * @param score   Score used for sorting
     * @param member  New member key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zaddL(key: String, score: Double, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zadd(key, score, member, handler)
      }.map(out => out: Long)

    /**
     *  Add one or more members to a sorted set, or update its score if it already exists
     * @param key     Key string
     * @param members New member keys and their scores
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zaddManyL(key: String, members: Map[String,Double]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zaddMany(key, members, handler)
      }.map(out => out: Long)

    /**
     *  Get the number of members in a sorted set
     * @param key     Key string
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zcardL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zcard(key, handler)
      }.map(out => out: Long)

    /**
     *  Count the members in a sorted set with scores within the given values
     * @param key     Key string
     * @param min     Minimum score
     * @param max     Maximum score
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zcountL(key: String, min: Double, max: Double): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zcount(key, min, max, handler)
      }.map(out => out: Long)

    /**
     *  Increment the score of a member in a sorted set
     * @param key       Key string
     * @param increment Increment amount
     * @param member    Member key
     * @param handler   Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zincrbyL(key: String, increment: Double, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zincrby(key, increment, member, handler)
      }

    /**
     *  Intersect multiple sorted sets and store the resulting sorted set in a new key
     * @param destkey Destination key
     * @param sets    List of keys identifying sorted sets to intersect
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zinterstoreL(destkey: String, sets: List[String], options: AggregateOptions): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zinterstore(destkey, sets, options, handler)
      }.map(out => out: Long)

    /**
     *  Intersect multiple sorted sets and store the resulting sorted set in a new key using weights for scoring
     * @param destkey Destination key
     * @param sets    List of keys identifying sorted sets to intersect
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zinterstoreWeighedL(destkey: String, sets: Map[String,Double], options: AggregateOptions): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zinterstoreWeighed(destkey, sets, options, handler)
      }.map(out => out: Long)

    /**
     *  Count the number of members in a sorted set between a given lexicographical range
     * @param key     Key string
     * @param min     Pattern to compare against for minimum value
     * @param max     Pattern to compare against for maximum value
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zlexcountL(key: String, min: String, max: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zlexcount(key, min, max, handler)
      }.map(out => out: Long)

    /**
     *  Return a range of members in a sorted set, by index
     * @param key     Key string
     * @param start   Start index for the range
     * @param stop    Stop index for the range - inclusive
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zrangeL(key: String, start: Long, stop: Long): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrange(key, start, stop, handler)
      }

    /**
     *  Return a range of members in a sorted set, by index
     * @param key     Key string
     * @param start   Start index for the range
     * @param stop    Stop index for the range - inclusive
     * @param options Range options
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zrangeWithOptionsL(key: String, start: Long, stop: Long, options: RangeOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrangeWithOptions(key, start, stop, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by lexicographical range
     * @param key     Key string
     * @param min     Pattern representing a minimum allowed value
     * @param max     Pattern representing a maximum allowed value
     * @param options Limit options where limit can be specified
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zrangebylexL(key: String, min: String, max: String, options: LimitOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrangebylex(key, min, max, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by score
     * @param key     Key string
     * @param min     Pattern defining a minimum value
     * @param max     Pattern defining a maximum value
     * @param options Range and limit options
     * @param handler Handler for the result of this call.
     * @since Redis 1.0.5
     *  group: sorted_set
     */
    def zrangebyscoreL(key: String, min: String, max: String, options: RangeLimitOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrangebyscore(key, min, max, options, handler)
      }

    /**
     *  Determine the index of a member in a sorted set
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zrankL(key: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zrank(key, member, handler)
      }.map(out => out: Long)

    /**
     *  Remove one member from a sorted set
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zremL(key: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zrem(key, member, handler)
      }.map(out => out: Long)

    /**
     *  Remove one or more members from a sorted set
     * @param key     Key string
     * @param members Members in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zremManyL(key: String, members: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zremMany(key, members, handler)
      }.map(out => out: Long)

    /**
     *  Remove all members in a sorted set between the given lexicographical range
     * @param key     Key string
     * @param min     Pattern defining a minimum value
     * @param max     Pattern defining a maximum value
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zremrangebylexL(key: String, min: String, max: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zremrangebylex(key, min, max, handler)
      }.map(out => out: Long)

    /**
     *  Remove all members in a sorted set within the given indexes
     * @param key     Key string
     * @param start   Start index
     * @param stop    Stop index
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zremrangebyrankL(key: String, start: Long, stop: Long): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zremrangebyrank(key, start, stop, handler)
      }.map(out => out: Long)

    /**
     *  Remove all members in a sorted set within the given scores
     * @param key     Key string
     * @param min     Pattern defining a minimum value
     * @param max     Pattern defining a maximum value
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zremrangebyscoreL(key: String, min: String, max: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zremrangebyscore(key, min, max, handler)
      }.map(out => out: Long)

    /**
     *  Return a range of members in a sorted set, by index, with scores ordered from high to low
     * @param key     Key string
     * @param start   Start index for the range
     * @param stop    Stop index for the range - inclusive
     * @param options Range options
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zrevrangeL(key: String, start: Long, stop: Long, options: RangeOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrevrange(key, start, stop, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by score, between the given lexicographical range with scores ordered from high to low
     * @param key     Key string
     * @param max     Pattern defining a maximum value
     * @param min     Pattern defining a minimum value
     * @param options Limit options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.9
     *  group: sorted_set
     */
    def zrevrangebylexL(key: String, max: String, min: String, options: LimitOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrevrangebylex(key, max, min, options, handler)
      }

    /**
     *  Return a range of members in a sorted set, by score, with scores ordered from high to low
     * @param key     Key string
     * @param max     Pattern defining a maximum value
     * @param min     Pattern defining a minimum value
     * @param options Range and limit options
     * @param handler Handler for the result of this call.
     * @since Redis 2.2.0
     *  group: sorted_set
     */
    def zrevrangebyscoreL(key: String, max: String, min: String, options: RangeLimitOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zrevrangebyscore(key, max, min, options, handler)
      }

    /**
     *  Determine the index of a member in a sorted set, with scores ordered from high to low
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zrevrankL(key: String, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zrevrank(key, member, handler)
      }.map(out => out: Long)

    /**
     *  Get the score associated with the given member in a sorted set
     * @param key     Key string
     * @param member  Member in the sorted set identified by key
     * @param handler Handler for the result of this call.
     * @since Redis 1.2.0
     *  group: sorted_set
     */
    def zscoreL(key: String, member: String): Task[String] =
      Task.handle[String] { handler =>
        target.zscore(key, member, handler)
      }

    /**
     *  Add multiple sorted sets and store the resulting sorted set in a new key
     * @param destkey Destination key
     * @param sets    List of keys identifying sorted sets
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zunionstoreL(destkey: String, sets: List[String], options: AggregateOptions): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zunionstore(destkey, sets, options, handler)
      }.map(out => out: Long)

    /**
     *  Add multiple sorted sets using weights, and store the resulting sorted set in a new key
     * @param key     Destination key
     * @param sets    Map containing set-key:weight pairs
     * @param options Aggregation options
     * @param handler Handler for the result of this call.
     * @since Redis 2.0.0
     *  group: sorted_set
     */
    def zunionstoreWeighedL(key: String, sets: Map[String,Double], options: AggregateOptions): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.zunionstoreWeighed(key, sets, options, handler)
      }.map(out => out: Long)

    /**
     *  Incrementally iterate the keys space
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: generic
     */
    def scanL(cursor: String, options: ScanOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.scan(cursor, options, handler)
      }

    /**
     *  Incrementally iterate Set elements
     * @param key     Key string
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: set
     */
    def sscanL(key: String, cursor: String, options: ScanOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.sscan(key, cursor, options, handler)
      }

    /**
     *  Incrementally iterate hash fields and associated values
     * @param key     Key string
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: hash
     */
    def hscanL(key: String, cursor: String, options: ScanOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.hscan(key, cursor, options, handler)
      }

    /**
     *  Incrementally iterate sorted sets elements and associated scores
     * @param key     Key string
     * @param cursor  Cursor id
     * @param options Scan options
     * @param handler Handler for the result of this call.
     * @since Redis 2.8.0
     *  group: sorted_set
     */
    def zscanL(key: String, cursor: String, options: ScanOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.zscan(key, cursor, options, handler)
      }

    /**
     *  Add one or more geospatial items in the geospatial index represented using a sorted set.
     * @param key     Key string
     * @param longitude  longitude
     * @param latitude latitude
     * @param member member
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoaddL(key: String, longitude: Double, latitude: Double, member: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.geoadd(key, longitude, latitude, member, handler)
      }.map(out => out: Long)

    /**
     *  Add one or more geospatial items in the geospatial index represented using a sorted set.
     * @param key     Key string
     * @param members  list of &lt;lon, lat, member&gt;
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoaddManyL(key: String, members: List[GeoMember]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.geoaddMany(key, members, handler)
      }.map(out => out: Long)

    /**
     *  Return valid Geohash strings representing the position of one or more elements in a sorted set value representing
     *  a geospatial index (where elements were added using GEOADD).
     * @param key     Key string
     * @param member member
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geohashL(key: String, member: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.geohash(key, member, handler)
      }

    /**
     *  Return valid Geohash strings representing the position of one or more elements in a sorted set value representing
     *  a geospatial index (where elements were added using GEOADD).
     * @param key     Key string
     * @param members  list of members
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geohashManyL(key: String, members: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.geohashMany(key, members, handler)
      }

    /**
     *  Return the positions (longitude,latitude) of all the specified members of the geospatial index represented by the
     *  sorted set at key.
     * @param key     Key string
     * @param member member
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoposL(key: String, member: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.geopos(key, member, handler)
      }

    /**
     *  Return the positions (longitude,latitude) of all the specified members of the geospatial index represented by the
     *  sorted set at key.
     * @param key     Key string
     * @param members  list of members
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geoposManyL(key: String, members: List[String]): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.geoposMany(key, members, handler)
      }

    /**
     *  Return the distance between two members in the geospatial index represented by the sorted set.
     * @param key     Key string
     * @param member1 member 1
     * @param member2 member 2
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geodistL(key: String, member1: String, member2: String): Task[String] =
      Task.handle[String] { handler =>
        target.geodist(key, member1, member2, handler)
      }

    /**
     *  Return the distance between two members in the geospatial index represented by the sorted set.
     * @param key     Key string
     * @param member1 member 1
     * @param member2 member 2
     * @param unit geo unit
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def geodistWithUnitL(key: String, member1: String, member2: String, unit: GeoUnit): Task[String] =
      Task.handle[String] { handler =>
        target.geodistWithUnit(key, member1, member2, unit, handler)
      }

    /**
     *  Return the members of a sorted set populated with geospatial information using GEOADD, which are within the borders
     *  of the area specified with the center location and the maximum distance from the center (the radius).
     * @param key     Key string
     * @param longitude longitude
     * @param latitude latitude
     * @param radius radius
     * @param unit geo unit
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusL(key: String, longitude: Double, latitude: Double, radius: Double, unit: GeoUnit): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.georadius(key, longitude, latitude, radius, unit, handler)
      }

    /**
     *  Return the members of a sorted set populated with geospatial information using GEOADD, which are within the borders
     *  of the area specified with the center location and the maximum distance from the center (the radius).
     * @param key     Key string
     * @param longitude longitude
     * @param latitude latitude
     * @param radius radius
     * @param unit geo unit
     * @param options geo radius options
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusWithOptionsL(key: String, longitude: Double, latitude: Double, radius: Double, unit: GeoUnit, options: GeoRadiusOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.georadiusWithOptions(key, longitude, latitude, radius, unit, options, handler)
      }

    /**
     *  This command is exactly like GEORADIUS with the sole difference that instead of taking, as the center of the area
     *  to query, a longitude and latitude value, it takes the name of a member already existing inside the geospatial
     *  index represented by the sorted set.
     * @param key     Key string
     * @param member member
     * @param radius radius
     * @param unit geo unit
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusbymemberL(key: String, member: String, radius: Double, unit: GeoUnit): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.georadiusbymember(key, member, radius, unit, handler)
      }

    /**
     *  This command is exactly like GEORADIUS with the sole difference that instead of taking, as the center of the area
     *  to query, a longitude and latitude value, it takes the name of a member already existing inside the geospatial
     *  index represented by the sorted set.
     * @param key     Key string
     * @param member member
     * @param radius radius
     * @param unit geo unit
     * @param options geo radius options
     * @param handler Handler for the result of this call.
     * @since Redis 3.2.0
     *  group: geo
     */
    def georadiusbymemberWithOptionsL(key: String, member: String, radius: Double, unit: GeoUnit, options: GeoRadiusOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.georadiusbymemberWithOptions(key, member, radius, unit, options, handler)
      }

    /**
     *  Instruct the server whether to reply to commands.
     * @since Redis 3.2.0
     *  group: server
     */
    def clientReplyL(options: ClientReplyOptions): Task[String] =
      Task.handle[String] { handler =>
        target.clientReply(options, handler)
      }

    /**
     *  Get the length of the value of a hash field.
     * @param key Key String
     * @param field field
     * @since Redis 3.2.0
     *  group: hash
     */
    def hstrlenL(key: String, field: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.hstrlen(key, field, handler)
      }.map(out => out: Long)

    /**
     *  Alters the last access time of a key(s). Returns the number of existing keys specified.
     * @param key Key String
     * @since Redis 3.2.1
     *  group: generic
     */
    def touchL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.touch(key, handler)
      }.map(out => out: Long)

    /**
     *  Alters the last access time of a key(s). Returns the number of existing keys specified.
     * @param keys list of keys
     * @since Redis 3.2.1
     *  group: generic
     */
    def touchManyL(keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.touchMany(keys, handler)
      }.map(out => out: Long)

    /**
     *  Set the debug mode for executed scripts.
     * @param scriptDebugOptions the option
     * @since Redis 3.2.0
     *  group: generic
     */
    def scriptDebugL(scriptDebugOptions: ScriptDebugOptions): Task[String] =
      Task.handle[String] { handler =>
        target.scriptDebug(scriptDebugOptions, handler)
      }

    /**
     *  Perform arbitrary bitfield integer operations on strings.
     * @param key     Key string
     * @since Redis 3.2.0
     *  group: string
     */
    def bitfieldL(key: String, bitFieldOptions: BitFieldOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.bitfield(key, bitFieldOptions, handler)
      }

    /**
     *  Perform arbitrary bitfield integer operations on strings.
     * @param key     Key string
     * @since Redis 3.2.0
     *  group: string
     */
    def bitfieldWithOverflowL(key: String, commands: BitFieldOptions, overflow: BitFieldOverflowOptions): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.bitfieldWithOverflow(key, commands, overflow, handler)
      }

    /**
     *  Delete a key asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     * @param key     Key to delete
     * @param handler Handler for the result of this call.
     * @since Redis 4.0.0
     *  group: generic
     */
    def unlinkL(key: String): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.unlink(key, handler)
      }.map(out => out: Long)

    /**
     *  Delete multiple keys asynchronously in another thread. Otherwise it is just as DEL, but non blocking.
     * @param keys    List of keys to delete
     * @param handler Handler for the result of this call.
     * @since Redis 4.0.0
     *  group: generic
     */
    def unlinkManyL(keys: List[String]): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.unlinkMany(keys, handler)
      }.map(out => out: Long)

    /**
     *  Swaps two Redis databases
     * @param index1  index of first database to swap
     * @param index2  index of second database to swap
     * @param handler Handler for the result of this call.
     * @since Redis 4.0.0
     *  group: connection
     */
    def swapdbL(index1: Int, index2: Int): Task[String] =
      Task.handle[String] { handler =>
        target.swapdb(index1, index2, handler)
      }
  }


  implicit class VertxRedisSentinelOps(val target: RedisSentinel) extends AnyVal {
    /**
     *  Close the client - when it is fully closed the handler will be called.
     * @param handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Show a list of monitored masters and their state
     * @param handler Handler for the result of this call
     */
    def mastersL(): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.masters(handler)
      }

    /**
     *  Show the state and info of the specified master
     * @param name    master name
     * @param handler Handler for the result of this call
     */
    def masterL(name: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.master(name, handler)
      }

    /**
     *  Show a list of slaves for this master, and their state
     * @param name    master name
     * @param handler Handler for the result of this call
     */
    def slavesL(name: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.slaves(name, handler)
      }

    /**
     *  Show a list of sentinel instances for this master, and their state
     * @param name    master name
     * @param handler Handler for the result of this call
     */
    def sentinelsL(name: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.sentinels(name, handler)
      }

    /**
     *  Return the ip and port number of the master with that name.
     *  If a failover is in progress or terminated successfully for this master
     *  it returns the address and port of the promoted slave
     * @param name    master name
     * @param handler Handler for the result of this call
     */
    def getMasterAddrByNameL(name: String): Task[JsonArray] =
      Task.handle[JsonArray] { handler =>
        target.getMasterAddrByName(name, handler)
      }

    /**
     *  Reset all the masters with matching name.
     *  The pattern argument is a glob-style pattern.
     *  The reset process clears any previous state in a master (including a failover in pro
     * @param pattern pattern String
     * @param handler Handler for the result of this call
     */
    def resetL(pattern: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.reset(pattern, handler)
      }.map(_ => ())

    /**
     *  Force a failover as if the master was not reachable, and without asking for agreement to other Sentinels
     *  (however a new version of the configuration will be published so that the other Sentinels
     *  will update their configurations)
     * @param name    master name
     * @param handler Handler for the result of this call
     */
    def failoverL(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.failover(name, handler)
      }

    /**
     *  Check if the current Sentinel configuration is able to reach the quorum needed to failover a master,
     *  and the majority needed to authorize the failover. This command should be used in monitoring systems
     *  to check if a Sentinel deployment is ok.
     * @param name    master name
     * @param handler Handler for the result of this call
     */
    def ckquorumL(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.ckquorum(name, handler)
      }

    /**
     *  Force Sentinel to rewrite its configuration on disk, including the current Sentinel state.
     *  Normally Sentinel rewrites the configuration every time something changes in its state
     *  (in the context of the subset of the state which is persisted on disk across restart).
     *  However sometimes it is possible that the configuration file is lost because of operation errors,
     *  disk failures, package upgrade scripts or configuration managers. In those cases a way to to force Sentinel to
     *  rewrite the configuration file is handy. This command works even if the previous configuration file
     *  is completely missing.
     * @param handler Handler for the result of this call
     */
    def flushConfigL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.flushConfig(handler)
      }.map(_ => ())
  }


  implicit class VertxRedisAPIOps(val target: RedisAPI) extends AnyVal {

    def appendL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.append(arg0, arg1, handler)
      }


    def askingL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.asking(handler)
      }


    def authL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.auth(arg0, handler)
      }


    def bgrewriteaofL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.bgrewriteaof(handler)
      }


    def bgsaveL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bgsave(args, handler)
      }


    def bitcountL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bitcount(args, handler)
      }


    def bitfieldL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bitfield(args, handler)
      }


    def bitopL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bitop(args, handler)
      }


    def bitposL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bitpos(args, handler)
      }


    def blpopL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.blpop(args, handler)
      }


    def brpopL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.brpop(args, handler)
      }


    def brpoplpushL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.brpoplpush(arg0, arg1, arg2, handler)
      }


    def bzpopmaxL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bzpopmax(args, handler)
      }


    def bzpopminL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.bzpopmin(args, handler)
      }


    def clientL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.client(args, handler)
      }


    def clusterL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.cluster(args, handler)
      }


    def commandL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.command(args, handler)
      }


    def configL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.config(args, handler)
      }


    def dbsizeL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.dbsize(handler)
      }


    def debugL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.debug(args, handler)
      }


    def decrL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.decr(arg0, handler)
      }


    def decrbyL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.decrby(arg0, arg1, handler)
      }


    def delL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.del(args, handler)
      }


    def discardL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.discard(handler)
      }


    def dumpL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.dump(arg0, handler)
      }


    def echoL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.echo(arg0, handler)
      }


    def evalL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.eval(args, handler)
      }


    def evalshaL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.evalsha(args, handler)
      }


    def execL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.exec(handler)
      }


    def existsL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.exists(args, handler)
      }


    def expireL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.expire(arg0, arg1, handler)
      }


    def expireatL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.expireat(arg0, arg1, handler)
      }


    def flushallL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.flushall(args, handler)
      }


    def flushdbL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.flushdb(args, handler)
      }


    def geoaddL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.geoadd(args, handler)
      }


    def geodistL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.geodist(args, handler)
      }


    def geohashL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.geohash(args, handler)
      }


    def geoposL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.geopos(args, handler)
      }


    def georadiusL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.georadius(args, handler)
      }


    def georadiusRoL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.georadiusRo(args, handler)
      }


    def georadiusbymemberL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.georadiusbymember(args, handler)
      }


    def georadiusbymemberRoL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.georadiusbymemberRo(args, handler)
      }


    def getL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.get(arg0, handler)
      }


    def getbitL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.getbit(arg0, arg1, handler)
      }


    def getrangeL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.getrange(arg0, arg1, arg2, handler)
      }


    def getsetL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.getset(arg0, arg1, handler)
      }


    def hdelL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.hdel(args, handler)
      }


    def hexistsL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hexists(arg0, arg1, handler)
      }


    def hgetL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hget(arg0, arg1, handler)
      }


    def hgetallL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hgetall(arg0, handler)
      }


    def hincrbyL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hincrby(arg0, arg1, arg2, handler)
      }


    def hincrbyfloatL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hincrbyfloat(arg0, arg1, arg2, handler)
      }


    def hkeysL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hkeys(arg0, handler)
      }


    def hlenL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hlen(arg0, handler)
      }


    def hmgetL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.hmget(args, handler)
      }


    def hmsetL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.hmset(args, handler)
      }


    def hostL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.host(args, handler)
      }


    def hscanL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.hscan(args, handler)
      }


    def hsetL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.hset(args, handler)
      }


    def hsetnxL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hsetnx(arg0, arg1, arg2, handler)
      }


    def hstrlenL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hstrlen(arg0, arg1, handler)
      }


    def hvalsL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.hvals(arg0, handler)
      }


    def incrL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.incr(arg0, handler)
      }


    def incrbyL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.incrby(arg0, arg1, handler)
      }


    def incrbyfloatL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.incrbyfloat(arg0, arg1, handler)
      }


    def infoL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.info(args, handler)
      }


    def keysL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.keys(arg0, handler)
      }


    def lastsaveL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.lastsave(handler)
      }


    def latencyL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.latency(args, handler)
      }


    def lindexL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.lindex(arg0, arg1, handler)
      }


    def linsertL(arg0: String, arg1: String, arg2: String, arg3: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.linsert(arg0, arg1, arg2, arg3, handler)
      }


    def llenL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.llen(arg0, handler)
      }


    def lolwutL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.lolwut(args, handler)
      }


    def lpopL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.lpop(arg0, handler)
      }


    def lpushL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.lpush(args, handler)
      }


    def lpushxL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.lpushx(args, handler)
      }


    def lrangeL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.lrange(arg0, arg1, arg2, handler)
      }


    def lremL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.lrem(arg0, arg1, arg2, handler)
      }


    def lsetL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.lset(arg0, arg1, arg2, handler)
      }


    def ltrimL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.ltrim(arg0, arg1, arg2, handler)
      }


    def memoryL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.memory(args, handler)
      }


    def mgetL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.mget(args, handler)
      }


    def migrateL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.migrate(args, handler)
      }


    def moduleL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.module(args, handler)
      }


    def monitorL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.monitor(handler)
      }


    def moveL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.move(arg0, arg1, handler)
      }


    def msetL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.mset(args, handler)
      }


    def msetnxL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.msetnx(args, handler)
      }


    def multiL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.multi(handler)
      }


    def objectL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.`object`(args, handler)
      }


    def persistL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.persist(arg0, handler)
      }


    def pexpireL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.pexpire(arg0, arg1, handler)
      }


    def pexpireatL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.pexpireat(arg0, arg1, handler)
      }


    def pfaddL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.pfadd(args, handler)
      }


    def pfcountL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.pfcount(args, handler)
      }


    def pfdebugL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.pfdebug(args, handler)
      }


    def pfmergeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.pfmerge(args, handler)
      }


    def pfselftestL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.pfselftest(handler)
      }


    def pingL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.ping(args, handler)
      }


    def postL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.post(args, handler)
      }


    def psetexL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.psetex(arg0, arg1, arg2, handler)
      }


    def psubscribeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.psubscribe(args, handler)
      }


    def psyncL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.psync(arg0, arg1, handler)
      }


    def pttlL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.pttl(arg0, handler)
      }


    def publishL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.publish(arg0, arg1, handler)
      }


    def pubsubL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.pubsub(args, handler)
      }


    def punsubscribeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.punsubscribe(args, handler)
      }


    def randomkeyL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.randomkey(handler)
      }


    def readonlyL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.readonly(handler)
      }


    def readwriteL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.readwrite(handler)
      }


    def renameL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.rename(arg0, arg1, handler)
      }


    def renamenxL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.renamenx(arg0, arg1, handler)
      }


    def replconfL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.replconf(args, handler)
      }


    def replicaofL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.replicaof(arg0, arg1, handler)
      }


    def restoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.restore(args, handler)
      }


    def restoreAskingL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.restoreAsking(args, handler)
      }


    def roleL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.role(handler)
      }


    def rpopL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.rpop(arg0, handler)
      }


    def rpoplpushL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.rpoplpush(arg0, arg1, handler)
      }


    def rpushL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.rpush(args, handler)
      }


    def rpushxL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.rpushx(args, handler)
      }


    def saddL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sadd(args, handler)
      }


    def saveL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.save(handler)
      }


    def scanL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.scan(args, handler)
      }


    def scardL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.scard(arg0, handler)
      }


    def scriptL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.script(args, handler)
      }


    def sdiffL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sdiff(args, handler)
      }


    def sdiffstoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sdiffstore(args, handler)
      }


    def selectL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.select(arg0, handler)
      }


    def setL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.set(args, handler)
      }


    def setbitL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.setbit(arg0, arg1, arg2, handler)
      }


    def setexL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.setex(arg0, arg1, arg2, handler)
      }


    def setnxL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.setnx(arg0, arg1, handler)
      }


    def setrangeL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.setrange(arg0, arg1, arg2, handler)
      }


    def shutdownL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.shutdown(args, handler)
      }


    def sinterL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sinter(args, handler)
      }


    def sinterstoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sinterstore(args, handler)
      }


    def sismemberL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.sismember(arg0, arg1, handler)
      }


    def slaveofL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.slaveof(arg0, arg1, handler)
      }


    def slowlogL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.slowlog(args, handler)
      }


    def smembersL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.smembers(arg0, handler)
      }


    def smoveL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.smove(arg0, arg1, arg2, handler)
      }


    def sortL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sort(args, handler)
      }


    def spopL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.spop(args, handler)
      }


    def srandmemberL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.srandmember(args, handler)
      }


    def sremL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.srem(args, handler)
      }


    def sscanL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sscan(args, handler)
      }


    def strlenL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.strlen(arg0, handler)
      }


    def subscribeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.subscribe(args, handler)
      }


    def substrL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.substr(arg0, arg1, arg2, handler)
      }


    def sunionL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sunion(args, handler)
      }


    def sunionstoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.sunionstore(args, handler)
      }


    def swapdbL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.swapdb(arg0, arg1, handler)
      }


    def syncL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.sync(handler)
      }


    def timeL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.time(handler)
      }


    def touchL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.touch(args, handler)
      }


    def ttlL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.ttl(arg0, handler)
      }


    def typeL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.`type`(arg0, handler)
      }


    def unlinkL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.unlink(args, handler)
      }


    def unsubscribeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.unsubscribe(args, handler)
      }


    def unwatchL(): Task[Response] =
      Task.handle[Response] { handler =>
        target.unwatch(handler)
      }


    def waitL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.wait(arg0, arg1, handler)
      }


    def watchL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.watch(args, handler)
      }


    def xackL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xack(args, handler)
      }


    def xaddL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xadd(args, handler)
      }


    def xclaimL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xclaim(args, handler)
      }


    def xdelL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xdel(args, handler)
      }


    def xgroupL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xgroup(args, handler)
      }


    def xinfoL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xinfo(args, handler)
      }


    def xlenL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.xlen(arg0, handler)
      }


    def xpendingL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xpending(args, handler)
      }


    def xrangeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xrange(args, handler)
      }


    def xreadL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xread(args, handler)
      }


    def xreadgroupL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xreadgroup(args, handler)
      }


    def xrevrangeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xrevrange(args, handler)
      }


    def xsetidL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.xsetid(arg0, arg1, handler)
      }


    def xtrimL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.xtrim(args, handler)
      }


    def zaddL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zadd(args, handler)
      }


    def zcardL(arg0: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zcard(arg0, handler)
      }


    def zcountL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zcount(arg0, arg1, arg2, handler)
      }


    def zincrbyL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zincrby(arg0, arg1, arg2, handler)
      }


    def zinterstoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zinterstore(args, handler)
      }


    def zlexcountL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zlexcount(arg0, arg1, arg2, handler)
      }


    def zpopmaxL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zpopmax(args, handler)
      }


    def zpopminL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zpopmin(args, handler)
      }


    def zrangeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrange(args, handler)
      }


    def zrangebylexL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrangebylex(args, handler)
      }


    def zrangebyscoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrangebyscore(args, handler)
      }


    def zrankL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrank(arg0, arg1, handler)
      }


    def zremL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrem(args, handler)
      }


    def zremrangebylexL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zremrangebylex(arg0, arg1, arg2, handler)
      }


    def zremrangebyrankL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zremrangebyrank(arg0, arg1, arg2, handler)
      }


    def zremrangebyscoreL(arg0: String, arg1: String, arg2: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zremrangebyscore(arg0, arg1, arg2, handler)
      }


    def zrevrangeL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrevrange(args, handler)
      }


    def zrevrangebylexL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrevrangebylex(args, handler)
      }


    def zrevrangebyscoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrevrangebyscore(args, handler)
      }


    def zrevrankL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zrevrank(arg0, arg1, handler)
      }


    def zscanL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zscan(args, handler)
      }


    def zscoreL(arg0: String, arg1: String): Task[Response] =
      Task.handle[Response] { handler =>
        target.zscore(arg0, arg1, handler)
      }


    def zunionstoreL(args: List[String]): Task[Response] =
      Task.handle[Response] { handler =>
        target.zunionstore(args, handler)
      }
  }


}