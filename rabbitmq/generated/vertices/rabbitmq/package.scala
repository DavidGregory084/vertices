package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.rabbitmq.QueueOptions
import io.vertx.rabbitmq.RabbitMQClient
import io.vertx.rabbitmq.RabbitMQConsumer
import io.vertx.rabbitmq.RabbitMQMessage
import io.vertx.rabbitmq.RabbitMQOptions
import java.lang.Long
import java.lang.String
import java.lang.Throwable
import java.lang.Void

package object rabbitmq {
  implicit class VertxRabbitMQClientOps(val target: RabbitMQClient) extends AnyVal {
    /**
     *  Acknowledge one or several received messages. Supply the deliveryTag from the AMQP.Basic.GetOk or AMQP.Basic.Deliver
     *  method containing the received message being acknowledged.
     * @see com.rabbitmq.client.Channel#basicAck(long, boolean)
     */
    def basicAckL(deliveryTag: Long, multiple: Boolean): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.basicAck(deliveryTag, multiple, resultHandler)
      }

    /**
     *  Reject one or several received messages.
     * @see com.rabbitmq.client.Channel#basicNack(long, boolean, boolean)
     */
    def basicNackL(deliveryTag: Long, multiple: Boolean, requeue: Boolean): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.basicNack(deliveryTag, multiple, requeue, resultHandler)
      }

    /**
     *  Retrieve a message from a queue using AMQP.Basic.Get
     * @see com.rabbitmq.client.Channel#basicGet(String, boolean)
     */
    def basicGetL(queue: String, autoAck: Boolean): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.basicGet(queue, autoAck, resultHandler)
      }

    /**
     * 
     * @see com.rabbitmq.client.Channel#basicConsume(String, Consumer)
     * @see RabbitMQClient#basicConsumer(String, Handler)
     */
    def basicConsumerL(queue: String): Task[RabbitMQConsumer] =
      Task.handle[RabbitMQConsumer] { resultHandler =>
        target.basicConsumer(queue, resultHandler)
      }

    /**
     *  Create a consumer with the given {@code options}.
     * @param queue          the name of a queue
     * @param options        options for queue
     * @param resultHandler  a handler through which you can find out the operation status;
     *                        if the operation succeeds you can begin to receive messages
     *                        through an instance of {@link RabbitMQConsumer}
     * @see com.rabbitmq.client.Channel#basicConsume(String, boolean, String, Consumer)
     */
    def basicConsumerL(queue: String, options: QueueOptions): Task[RabbitMQConsumer] =
      Task.handle[RabbitMQConsumer] { resultHandler =>
        target.basicConsumer(queue, options, resultHandler)
      }

    /**
     *  Publish a message. Publishing to a non-existent exchange will result in a channel-level protocol exception,
     *  which closes the channel. Invocations of Channel#basicPublish will eventually block if a resource-driven alarm is in effect.
     * @see com.rabbitmq.client.Channel#basicPublish(String, String, AMQP.BasicProperties, byte[])
     */
    def basicPublishL(exchange: String, routingKey: String, message: JsonObject): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.basicPublish(exchange, routingKey, message, resultHandler)
      }.map(_ => ())

    /**
     *  Enables publisher acknowledgements on this channel. Can be called once during client initialisation. Calls to basicPublish()
     *  will have to be confirmed.
     * @see Channel#confirmSelect()
     * @see http://www.rabbitmq.com/confirms.html
     */
    def confirmSelectL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.confirmSelect(resultHandler)
      }.map(_ => ())

    /**
     *  Wait until all messages published since the last call have been either ack'd or nack'd by the broker.
     *  This will incur slight performance loss at the expense of higher write consistency.
     *  If desired, multiple calls to basicPublish() can be batched before confirming.
     * @see Channel#waitForConfirms()
     * @see http://www.rabbitmq.com/confirms.html
     * @throws java.io.IOException Throws an IOException if the message was not written to the queue.
     */
    def waitForConfirmsL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.waitForConfirms(resultHandler)
      }.map(_ => ())

    /**
     *  Wait until all messages published since the last call have been either ack'd or nack'd by the broker; or until timeout elapses. If the timeout expires a TimeoutException is thrown.
     * @param timeout
     * @see io.vertx.rabbitmq.impl.RabbitMQClientImpl#waitForConfirms(Handler)
     * @see http://www.rabbitmq.com/confirms.html
     * @throws java.io.IOException Throws an IOException if the message was not written to the queue.
     */
    def waitForConfirmsL(timeout: Long): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.waitForConfirms(timeout, resultHandler)
      }.map(_ => ())

    /**
     *  Request a specific prefetchCount "quality of service" settings
     *  for this channel.
     * @see #basicQos(int, int, boolean, Handler)
     * @param prefetchCount maximum number of messages that the server
     *  will deliver, 0 if unlimited
     * @param resultHandler handler called when operation is done with a result of the operation
     */
    def basicQosL(prefetchCount: Int): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.basicQos(prefetchCount, resultHandler)
      }.map(_ => ())

    /**
     *  Request a specific prefetchCount "quality of service" settings
     *  for this channel.
     * @see #basicQos(int, int, boolean, Handler)
     * @param prefetchCount maximum number of messages that the server
     *  will deliver, 0 if unlimited
     * @param global true if the settings should be applied to the
     *  entire channel rather than each consumer
     * @param resultHandler handler called when operation is done with a result of the operation
     */
    def basicQosL(prefetchCount: Int, global: Boolean): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.basicQos(prefetchCount, global, resultHandler)
      }.map(_ => ())

    /**
     *  Request specific "quality of service" settings.
     * 
     *  These settings impose limits on the amount of data the server
     *  will deliver to consumers before requiring acknowledgements.
     *  Thus they provide a means of consumer-initiated flow control.
     * @see com.rabbitmq.client.AMQP.Basic.Qos
     * @param prefetchSize maximum amount of content (measured in
     *  octets) that the server will deliver, 0 if unlimited
     * @param prefetchCount maximum number of messages that the server
     *  will deliver, 0 if unlimited
     * @param global true if the settings should be applied to the
     *  entire channel rather than each consumer
     * @param resultHandler handler called when operation is done with a result of the operation
     */
    def basicQosL(prefetchSize: Int, prefetchCount: Int, global: Boolean): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.basicQos(prefetchSize, prefetchCount, global, resultHandler)
      }.map(_ => ())

    /**
     *  Declare an exchange.
     * @see com.rabbitmq.client.Channel#exchangeDeclare(String, String, boolean, boolean, Map)
     */
    def exchangeDeclareL(exchange: String, `type`: String, durable: Boolean, autoDelete: Boolean): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.exchangeDeclare(exchange, `type`, durable, autoDelete, resultHandler)
      }.map(_ => ())

    /**
     *  Declare an exchange with additional parameters such as dead lettering, an alternate exchange or TTL.
     * @see com.rabbitmq.client.Channel#exchangeDeclare(String, String, boolean, boolean, Map)
     */
    def exchangeDeclareL(exchange: String, `type`: String, durable: Boolean, autoDelete: Boolean, config: JsonObject): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.exchangeDeclare(exchange, `type`, durable, autoDelete, config, resultHandler)
      }.map(_ => ())

    /**
     *  Delete an exchange, without regard for whether it is in use or not.
     * @see com.rabbitmq.client.Channel#exchangeDelete(String)
     */
    def exchangeDeleteL(exchange: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.exchangeDelete(exchange, resultHandler)
      }.map(_ => ())

    /**
     *  Bind an exchange to an exchange.
     * @see com.rabbitmq.client.Channel#exchangeBind(String, String, String)
     */
    def exchangeBindL(destination: String, source: String, routingKey: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.exchangeBind(destination, source, routingKey, resultHandler)
      }.map(_ => ())

    /**
     *  Unbind an exchange from an exchange.
     * @see com.rabbitmq.client.Channel#exchangeUnbind(String, String, String)
     */
    def exchangeUnbindL(destination: String, source: String, routingKey: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.exchangeUnbind(destination, source, routingKey, resultHandler)
      }.map(_ => ())

    /**
     *  Actively declare a server-named exclusive, autodelete, non-durable queue.
     * @see com.rabbitmq.client.Channel#queueDeclare()
     */
    def queueDeclareAutoL(): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.queueDeclareAuto(resultHandler)
      }

    /**
     *  Declare a queue
     * @see com.rabbitmq.client.Channel#queueDeclare(String, boolean, boolean, boolean, java.util.Map)
     */
    def queueDeclareL(queue: String, durable: Boolean, exclusive: Boolean, autoDelete: Boolean): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.queueDeclare(queue, durable, exclusive, autoDelete, resultHandler)
      }

    /**
     *  Declare a queue with config options
     * @see com.rabbitmq.client.Channel#queueDeclare(String, boolean, boolean, boolean, java.util.Map)
     */
    def queueDeclareL(queue: String, durable: Boolean, exclusive: Boolean, autoDelete: Boolean, config: JsonObject): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.queueDeclare(queue, durable, exclusive, autoDelete, config, resultHandler)
      }

    /**
     *  Delete a queue, without regard for whether it is in use or has messages on it
     * @see com.rabbitmq.client.Channel#queueDelete(String)
     */
    def queueDeleteL(queue: String): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.queueDelete(queue, resultHandler)
      }

    /**
     *  Delete a queue
     * @see com.rabbitmq.client.Channel#queueDelete(String, boolean, boolean)
     */
    def queueDeleteIfL(queue: String, ifUnused: Boolean, ifEmpty: Boolean): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.queueDeleteIf(queue, ifUnused, ifEmpty, resultHandler)
      }

    /**
     *  Bind a queue to an exchange
     * @see com.rabbitmq.client.Channel#queueBind(String, String, String)
     */
    def queueBindL(queue: String, exchange: String, routingKey: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.queueBind(queue, exchange, routingKey, resultHandler)
      }.map(_ => ())

    /**
     *  Returns the number of messages in a queue ready to be delivered.
     * @see com.rabbitmq.client.Channel#messageCount(String)
     */
    def messageCountL(queue: String): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.messageCount(queue, resultHandler)
      }.map(out => out: Long)

    /**
     *  Start the rabbitMQ client. Create the connection and the chanel.
     * @see com.rabbitmq.client.Connection#createChannel()
     */
    def startL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.start(resultHandler)
      }.map(_ => ())

    /**
     *  Stop the rabbitMQ client. Close the connection and its chanel.
     * @see com.rabbitmq.client.Connection#close()
     */
    def stopL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.stop(resultHandler)
      }.map(_ => ())
  }


  implicit class VertxRabbitMQConsumerOps(val target: RabbitMQConsumer) extends AnyVal {

    def pipeToL(dst: WriteStream[RabbitMQMessage]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Stop message consumption from a queue.
     *  <p>
     *  The operation is asynchronous. When consumption will be stopped, you can by notified via {@link RabbitMQConsumer#endHandler(Handler)}
     * @param cancelResult contains information about operation status: success/fail.
     */
    def cancelL(): Task[Unit] =
      Task.handle[Void] { cancelResult =>
        target.cancel(cancelResult)
      }.map(_ => ())
  }


}