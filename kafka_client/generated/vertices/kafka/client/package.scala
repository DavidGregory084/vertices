package vertices
package kafka

import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.kafka.admin.ConsumerGroupListing
import io.vertx.kafka.admin.KafkaAdminClient
import io.vertx.kafka.admin.NewTopic
import io.vertx.kafka.client.common.PartitionInfo
import io.vertx.kafka.client.common.TopicPartition
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.consumer.KafkaConsumerRecords
import io.vertx.kafka.client.consumer.OffsetAndMetadata
import io.vertx.kafka.client.consumer.OffsetAndTimestamp
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.kafka.client.producer.RecordMetadata
import java.lang.Class
import java.lang.Long
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List
import java.util.Map
import java.util.Set

package object client {
  implicit class VertxKafkaConsumerOps[K, V](val target: KafkaConsumer[K, V])  {

    def pipeToL(dst: WriteStream[KafkaConsumerRecord[K,V]]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Subscribe to the given topic to get dynamically assigned partitions.
     *  <p>
     *  Due to internal buffering of messages, when changing the subscribed topic
     *  the old topic may remain in effect
     *  (as observed by the {@linkplain #handler(Handler)} record handler})
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new topic.
     * @param topic  topic to subscribe to
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def subscribeL(topic: String): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.subscribe(topic, completionHandler)
      }.map(_ => ())

    /**
     *  Subscribe to the given list of topics to get dynamically assigned partitions.
     *  <p>
     *  Due to internal buffering of messages, when changing the subscribed topics
     *  the old set of topics may remain in effect
     *  (as observed by the {@linkplain #handler(Handler)} record handler})
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new set of topics.
     * @param topics  topics to subscribe to
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def subscribeL(topics: Set[String]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.subscribe(topics, completionHandler)
      }.map(_ => ())

    /**
     *  Manually assign a partition to this consumer.
     *  <p>
     *  Due to internal buffering of messages, when reassigning
     *  the old partition may remain in effect
     *  (as observed by the {@linkplain #handler(Handler)} record handler)}
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new partition.
     * @param topicPartition  partition which want assigned
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def assignL(topicPartition: TopicPartition): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.assign(topicPartition, completionHandler)
      }.map(_ => ())

    /**
     *  Manually assign a list of partition to this consumer.
     *  <p>
     *  Due to internal buffering of messages, when reassigning
     *  the old set of partitions may remain in effect
     *  (as observed by the {@linkplain #handler(Handler)} record handler)}
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new set of partitions.
     * @param topicPartitions  partitions which want assigned
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def assignL(topicPartitions: Set[TopicPartition]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.assign(topicPartitions, completionHandler)
      }.map(_ => ())

    /**
     *  Get the set of partitions currently assigned to this consumer.
     * @param handler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def assignmentL(): Task[Set[TopicPartition]] =
      Task.handle[Set[TopicPartition]] { handler =>
        target.assignment(handler)
      }

    /**
     *  Unsubscribe from topics currently subscribed with subscribe.
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def unsubscribeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.unsubscribe(completionHandler)
      }.map(_ => ())

    /**
     *  Get the current subscription.
     * @param handler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def subscriptionL(): Task[Set[String]] =
      Task.handle[Set[String]] { handler =>
        target.subscription(handler)
      }

    /**
     *  Suspend fetching from the requested partition.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages from the given {@code topicPartition}
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will not see messages
     *  from the given {@code topicPartition}.
     * @param topicPartition topic partition from which suspend fetching
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def pauseL(topicPartition: TopicPartition): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.pause(topicPartition, completionHandler)
      }.map(_ => ())

    /**
     *  Suspend fetching from the requested partitions.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages from the given {@code topicPartitions}
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will not see messages
     *  from the given {@code topicPartitions}.
     * @param topicPartitions topic partition from which suspend fetching
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def pauseL(topicPartitions: Set[TopicPartition]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.pause(topicPartitions, completionHandler)
      }.map(_ => ())

    /**
     *  Get the set of partitions that were previously paused by a call to pause(Set).
     * @param handler handler called on operation completed
     */
    def pausedL(): Task[Set[TopicPartition]] =
      Task.handle[Set[TopicPartition]] { handler =>
        target.paused(handler)
      }

    /**
     *  Resume specified partition which have been paused with pause.
     * @param topicPartition topic partition from which resume fetching
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def resumeL(topicPartition: TopicPartition): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.resume(topicPartition, completionHandler)
      }.map(_ => ())

    /**
     *  Resume specified partitions which have been paused with pause.
     * @param topicPartitions topic partition from which resume fetching
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def resumeL(topicPartitions: Set[TopicPartition]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.resume(topicPartitions, completionHandler)
      }.map(_ => ())

    /**
     *  Overrides the fetch offsets that the consumer will use on the next poll.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages fetched with respect to the old offset
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new offset.
     * @param topicPartition  topic partition for which seek
     * @param offset  offset to seek inside the topic partition
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def seekL(topicPartition: TopicPartition, offset: Long): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.seek(topicPartition, offset, completionHandler)
      }.map(_ => ())

    /**
     *  Seek to the first offset for each of the given partition.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages fetched with respect to the old offset
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new offset.
     * @param topicPartition topic partition for which seek
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def seekToBeginningL(topicPartition: TopicPartition): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.seekToBeginning(topicPartition, completionHandler)
      }.map(_ => ())

    /**
     *  Seek to the first offset for each of the given partitions.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages fetched with respect to the old offset
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new offset.
     * @param topicPartitions topic partition for which seek
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def seekToBeginningL(topicPartitions: Set[TopicPartition]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.seekToBeginning(topicPartitions, completionHandler)
      }.map(_ => ())

    /**
     *  Seek to the last offset for each of the given partition.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages fetched with respect to the old offset
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new offset.
     * @param topicPartition topic partition for which seek
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def seekToEndL(topicPartition: TopicPartition): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.seekToEnd(topicPartition, completionHandler)
      }.map(_ => ())

    /**
     *  Seek to the last offset for each of the given partitions.
     *  <p>
     *  Due to internal buffering of messages,
     *  the {@linkplain #handler(Handler) record handler} will
     *  continue to observe messages fetched with respect to the old offset
     *  until some time <em>after</em> the given {@code completionHandler}
     *  is called. In contrast, the once the given {@code completionHandler}
     *  is called the {@link #batchHandler(Handler)} will only see messages
     *  consistent with the new offset.
     * @param topicPartitions topic partition for which seek
     * @param completionHandler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def seekToEndL(topicPartitions: Set[TopicPartition]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.seekToEnd(topicPartitions, completionHandler)
      }.map(_ => ())

    /**
     *  Commit current offsets for all the subscribed list of topics and partition.
     * @param completionHandler handler called on operation completed
     */
    def commitL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.commit(completionHandler)
      }.map(_ => ())

    /**
     *  Get the last committed offset for the given partition (whether the commit happened by this process or another).
     * @param topicPartition  topic partition for getting last committed offset
     * @param handler handler called on operation completed
     */
    def committedL(topicPartition: TopicPartition): Task[OffsetAndMetadata] =
      Task.handle[OffsetAndMetadata] { handler =>
        target.committed(topicPartition, handler)
      }

    /**
     *  Get metadata about the partitions for a given topic.
     * @param topic topic partition for which getting partitions info
     * @param handler handler called on operation completed
     * @return  current KafkaConsumer instance
     */
    def partitionsForL(topic: String): Task[List[PartitionInfo]] =
      Task.handle[List[PartitionInfo]] { handler =>
        target.partitionsFor(topic, handler)
      }

    /**
     *  Close the consumer
     * @param completionHandler handler called on operation completed
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
      }.map(_ => ())

    /**
     *  Get the offset of the next record that will be fetched (if a record with that offset exists).
     * @param partition The partition to get the position for
     * @param handler handler called on operation completed
     */
    def positionL(partition: TopicPartition): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.position(partition, handler)
      }.map(out => out: Long)

    /**
     *  Look up the offset for the given partition by timestamp. Note: the result might be null in case
     *  for the given timestamp no offset can be found -- e.g., when the timestamp refers to the future
     * @param topicPartition TopicPartition to query.
     * @param timestamp Timestamp to be used in the query.
     * @param handler handler called on operation completed
     */
    def offsetsForTimesL(topicPartition: TopicPartition, timestamp: Long): Task[OffsetAndTimestamp] =
      Task.handle[OffsetAndTimestamp] { handler =>
        target.offsetsForTimes(topicPartition, timestamp, handler)
      }

    /**
     *  Get the first offset for the given partitions.
     * @param topicPartition the partition to get the earliest offset.
     * @param handler handler called on operation completed. Returns the earliest available offset for the given partition
     */
    def beginningOffsetsL(topicPartition: TopicPartition): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.beginningOffsets(topicPartition, handler)
      }.map(out => out: Long)

    /**
     *  Get the last offset for the given partition. The last offset of a partition is the offset
     *  of the upcoming message, i.e. the offset of the last available message + 1.
     * @param topicPartition the partition to get the end offset.
     * @param handler handler called on operation completed. The end offset for the given partition.
     */
    def endOffsetsL(topicPartition: TopicPartition): Task[Long] =
      Task.handle[java.lang.Long] { handler =>
        target.endOffsets(topicPartition, handler)
      }.map(out => out: Long)

    /**
     *  Executes a poll for getting messages from Kafka
     * @param timeout The time, in milliseconds, spent waiting in poll if data is not available in the buffer.
     *                 If 0, returns immediately with any records that are available currently in the native Kafka consumer's buffer,
     *                 else returns empty. Must not be negative.
     * @param handler handler called after the poll with batch of records (can be empty).
     */
    def pollL(timeout: Long): Task[KafkaConsumerRecords[K,V]] =
      Task.handle[KafkaConsumerRecords[K,V]] { handler =>
        target.poll(timeout, handler)
      }
  }


  implicit class VertxKafkaProducerOps[K, V](val target: KafkaProducer[K, V])  {

    def endL(): Task[Unit] =
      Task.handle[Void] { arg0 =>
        target.end(arg0)
      }.map(_ => ())


    def endL(data: KafkaProducerRecord[K,V]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())


    def writeL(data: KafkaProducerRecord[K,V]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())

    /**
     *  Asynchronously write a record to a topic
     * @param record  record to write
     * @param handler handler called on operation completed
     * @return  current KafkaWriteStream instance
     */
    def sendL(record: KafkaProducerRecord[K,V]): Task[RecordMetadata] =
      Task.handle[RecordMetadata] { handler =>
        target.send(record, handler)
      }

    /**
     *  Get the partition metadata for the give topic.
     * @param topic topic partition for which getting partitions info
     * @param handler handler called on operation completed
     * @return  current KafkaProducer instance
     */
    def partitionsForL(topic: String): Task[List[PartitionInfo]] =
      Task.handle[List[PartitionInfo]] { handler =>
        target.partitionsFor(topic, handler)
      }

    /**
     *  Close the producer
     * @param completionHandler handler called on operation completed
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
      }.map(_ => ())

    /**
     *  Close the producer
     * @param timeout timeout to wait for closing
     * @param completionHandler handler called on operation completed
     */
    def closeL(timeout: Long): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(timeout, completionHandler)
      }.map(_ => ())
  }


  implicit class VertxKafkaAdminClientOps(val target: KafkaAdminClient) extends AnyVal {
    /**
     *  List the topics available in the cluster with the default options.
     * @param completionHandler handler called on operation completed with the topics set
     */
    def listTopicsL(): Task[Set[String]] =
      Task.handle[Set[String]] { completionHandler =>
        target.listTopics(completionHandler)
      }

    /**
     *  Creates a batch of new Kafka topics
     * @param topics topics to create
     * @param completionHandler handler called on operation completed
     */
    def createTopicsL(topics: List[NewTopic]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.createTopics(topics, completionHandler)
      }.map(_ => ())

    /**
     *  Deletes a batch of Kafka topics
     * @param topicNames the names of the topics to delete
     * @param completionHandler handler called on operation completed
     */
    def deleteTopicsL(topicNames: List[String]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.deleteTopics(topicNames, completionHandler)
      }.map(_ => ())

    /**
     *  Get the the consumer groups available in the cluster with the default options
     * @param completionHandler handler called on operation completed with the consumer groups ids
     */
    def listConsumerGroupsL(): Task[List[ConsumerGroupListing]] =
      Task.handle[List[ConsumerGroupListing]] { completionHandler =>
        target.listConsumerGroups(completionHandler)
      }
  }


}