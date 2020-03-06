package vertices


import monix.eval.Task
import io.netty.handler.codec.mqtt.MqttConnectReturnCode
import io.netty.handler.codec.mqtt.MqttQoS
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.SocketAddress
import io.vertx.mqtt.MqttAuth
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.MqttClientOptions
import io.vertx.mqtt.MqttEndpoint
import io.vertx.mqtt.MqttServer
import io.vertx.mqtt.MqttServerOptions
import io.vertx.mqtt.MqttWill
import io.vertx.mqtt.messages.MqttConnAckMessage
import io.vertx.mqtt.messages.MqttPublishMessage
import io.vertx.mqtt.messages.MqttSubAckMessage
import io.vertx.mqtt.messages.MqttSubscribeMessage
import io.vertx.mqtt.messages.MqttUnsubscribeMessage
import java.lang.Integer
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List
import java.util.Map

package object mqtt {
  implicit class VertxMqttClientOps(val target: MqttClient) extends AnyVal {
    /**
     *  Connects to an MQTT server calling connectHandler after connection
     * @param port  port of the MQTT server
     * @param host  hostname/ip address of the MQTT server
     * @param connectHandler  handler called when the asynchronous connect call ends
     * @return  current MQTT client instance
     */
    def connectL(port: Int, host: String): Task[MqttConnAckMessage] =
      Task.handle[MqttConnAckMessage] { connectHandler =>
        target.connect(port, host, connectHandler)
      }

    /**
     *  Connects to an MQTT server calling connectHandler after connection
     * @param port  port of the MQTT server
     * @param host  hostname/ip address of the MQTT server
     * @param serverName  the SNI server name
     * @param connectHandler  handler called when the asynchronous connect call ends
     * @return  current MQTT client instance
     */
    def connectL(port: Int, host: String, serverName: String): Task[MqttConnAckMessage] =
      Task.handle[MqttConnAckMessage] { connectHandler =>
        target.connect(port, host, serverName, connectHandler)
      }

    /**
     *  Disconnects from the MQTT server calling disconnectHandler after disconnection
     * @param disconnectHandler handler called when asynchronous disconnect call ends
     * @return current MQTT client instance
     */
    def disconnectL(): Task[Unit] =
      Task.handle[Void] { disconnectHandler =>
        target.disconnect(disconnectHandler)
      }.map(_ => ())

    /**
     *  Sends the PUBLISH message to the remote MQTT server
     * @param topic    topic on which the message is published
     * @param payload  message payload
     * @param qosLevel QoS level
     * @param isDup    if the message is a duplicate
     * @param isRetain if the message needs to be retained
     * @param publishSentHandler handler called after PUBLISH packet sent with packetid (not when QoS 0)
     * @return current MQTT client instance
     */
    def publishL(topic: String, payload: Buffer, qosLevel: MqttQoS, isDup: Boolean, isRetain: Boolean): Task[Int] =
      Task.handle[java.lang.Integer] { publishSentHandler =>
        target.publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler)
      }.map(out => out: Int)

    /**
     *  Subscribes to the topic with a specified QoS level
     * @param topic                 topic you subscribe on
     * @param qos                   QoS level
     * @param subscribeSentHandler handler called after SUBSCRIBE packet sent with packetid
     * @return current MQTT client instance
     */
    def subscribeL(topic: String, qos: Int): Task[Int] =
      Task.handle[java.lang.Integer] { subscribeSentHandler =>
        target.subscribe(topic, qos, subscribeSentHandler)
      }.map(out => out: Int)

    /**
     *  Subscribes to the topic and adds a handler which will be called after the request is sent
     * @param topics                topics you subscribe on
     * @param subscribeSentHandler  handler called after SUBSCRIBE packet sent with packetid
     * @return current MQTT client instance
     */
    def subscribeL(topics: Map[String,Integer]): Task[Int] =
      Task.handle[java.lang.Integer] { subscribeSentHandler =>
        target.subscribe(topics, subscribeSentHandler)
      }.map(out => out: Int)

    /**
     *  Unsubscribe from receiving messages on given topic
     * @param topic Topic you want to unsubscribe from
     * @param unsubscribeSentHandler  handler called after UNSUBSCRIBE packet sent
     * @return current MQTT client instance
     */
    def unsubscribeL(topic: String): Task[Int] =
      Task.handle[java.lang.Integer] { unsubscribeSentHandler =>
        target.unsubscribe(topic, unsubscribeSentHandler)
      }.map(out => out: Int)
  }


  implicit class VertxMqttEndpointOps(val target: MqttEndpoint) extends AnyVal {
    /**
     *  Sends the PUBLISH message to the remote MQTT server
     * @param topic              topic on which the message is published
     * @param payload            message payload
     * @param qosLevel           QoS level
     * @param isDup              if the message is a duplicate
     * @param isRetain           if the message needs to be retained
     * @param publishSentHandler handler called after PUBLISH packet sent with a packetId
     * @return current MQTT client instance
     */
    def publishL(topic: String, payload: Buffer, qosLevel: MqttQoS, isDup: Boolean, isRetain: Boolean): Task[Int] =
      Task.handle[java.lang.Integer] { publishSentHandler =>
        target.publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler)
      }.map(out => out: Int)

    /**
     *  Sends the PUBLISH message to the remote MQTT server explicitly specifying the messageId
     * @param topic              topic on which the message is published
     * @param payload            message payload
     * @param qosLevel           QoS level
     * @param isDup              if the message is a duplicate
     * @param isRetain           if the message needs to be retained
     * @param messageId          message ID
     * @param publishSentHandler handler called after PUBLISH packet sent with a packetId
     * @return current MQTT client instance
     */
    def publishL(topic: String, payload: Buffer, qosLevel: MqttQoS, isDup: Boolean, isRetain: Boolean, messageId: Int): Task[Int] =
      Task.handle[java.lang.Integer] { publishSentHandler =>
        target.publish(topic, payload, qosLevel, isDup, isRetain, messageId, publishSentHandler)
      }.map(out => out: Int)
  }


  implicit class VertxMqttServerOps(val target: MqttServer) extends AnyVal {
    /**
     *  Start the server listening for incoming connections on the port and host specified
     *  It ignores any options specified through the constructor
     * @param port          the port to listen on
     * @param host          the host to listen on
     * @param listenHandler handler called when the asynchronous listen call ends
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(port: Int, host: String): Task[MqttServer] =
      Task.handle[MqttServer] { listenHandler =>
        target.listen(port, host, listenHandler)
      }

    /**
     *  Start the server listening for incoming connections on the port specified but on
     *  "0.0.0.0" as host. It ignores any options specified through the constructor
     * @param port          the port to listen on
     * @param listenHandler handler called when the asynchronous listen call ends
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(port: Int): Task[MqttServer] =
      Task.handle[MqttServer] { listenHandler =>
        target.listen(port, listenHandler)
      }

    /**
     *  Start the server listening for incoming connections using the specified options
     *  through the constructor
     * @param listenHandler handler called when the asynchronous listen call ends
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(): Task[MqttServer] =
      Task.handle[MqttServer] { listenHandler =>
        target.listen(listenHandler)
      }

    /**
     *  Close the server supplying an handler that will be called when the server is actually closed (or has failed).
     * @param completionHandler the handler called on completion
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
      }.map(_ => ())
  }


}