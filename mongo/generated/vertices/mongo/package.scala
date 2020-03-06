package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.streams.ReadStream
import io.vertx.ext.mongo.AggregateOptions
import io.vertx.ext.mongo.BulkOperation
import io.vertx.ext.mongo.BulkWriteOptions
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.IndexOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.MongoClientBulkWriteResult
import io.vertx.ext.mongo.MongoClientDeleteResult
import io.vertx.ext.mongo.MongoClientUpdateResult
import io.vertx.ext.mongo.UpdateOptions
import io.vertx.ext.mongo.WriteOption
import java.lang.Long
import java.lang.String
import java.lang.Void
import java.util.List

package object mongo {
  implicit class VertxMongoClientOps(val target: MongoClient) extends AnyVal {
    /**
     *  Save a document in the specified collection
     *  <p>
     *  This operation might change <i>_id</i> field of <i>document</i> parameter
     * @param collection  the collection
     * @param document  the document
     * @param resultHandler  result handler will be provided with the id if document didn't already have one
     */
    def saveL(collection: String, document: JsonObject): Task[String] =
      Task.handle[String] { resultHandler =>
        target.save(collection, document, resultHandler)
      }

    /**
     *  Save a document in the specified collection with the specified write option
     *  <p>
     *  This operation might change <i>_id</i> field of <i>document</i> parameter
     * @param collection  the collection
     * @param document  the document
     * @param writeOption  the write option to use
     * @param resultHandler  result handler will be provided with the id if document didn't already have one
     */
    def saveWithOptionsL(collection: String, document: JsonObject, writeOption: WriteOption): Task[String] =
      Task.handle[String] { resultHandler =>
        target.saveWithOptions(collection, document, writeOption, resultHandler)
      }

    /**
     *  Insert a document in the specified collection
     *  <p>
     *  This operation might change <i>_id</i> field of <i>document</i> parameter
     * @param collection  the collection
     * @param document  the document
     * @param resultHandler  result handler will be provided with the id if document didn't already have one
     */
    def insertL(collection: String, document: JsonObject): Task[String] =
      Task.handle[String] { resultHandler =>
        target.insert(collection, document, resultHandler)
      }

    /**
     *  Insert a document in the specified collection with the specified write option
     *  <p>
     *  This operation might change <i>_id</i> field of <i>document</i> parameter
     * @param collection  the collection
     * @param document  the document
     * @param writeOption  the write option to use
     * @param resultHandler  result handler will be provided with the id if document didn't already have one
     */
    def insertWithOptionsL(collection: String, document: JsonObject, writeOption: WriteOption): Task[String] =
      Task.handle[String] { resultHandler =>
        target.insertWithOptions(collection, document, writeOption, resultHandler)
      }

    /**
     *  Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result
     * @param collection  the collection
     * @param query  query used to match the documents
     * @param update used to describe how the documents will be updated
     * @param resultHandler will be called when complete
     */
    def updateCollectionL(collection: String, query: JsonObject, update: JsonObject): Task[MongoClientUpdateResult] =
      Task.handle[MongoClientUpdateResult] { resultHandler =>
        target.updateCollection(collection, query, update, resultHandler)
      }

    /**
     *  Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
     * @param collection  the collection
     * @param query  query used to match the documents
     * @param update used to describe how the documents will be updated
     * @param options options to configure the update
     * @param resultHandler will be called when complete
     */
    def updateCollectionWithOptionsL(collection: String, query: JsonObject, update: JsonObject, options: UpdateOptions): Task[MongoClientUpdateResult] =
      Task.handle[MongoClientUpdateResult] { resultHandler =>
        target.updateCollectionWithOptions(collection, query, update, options, resultHandler)
      }

    /**
     *  Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result
     * @param collection  the collection
     * @param query  query used to match the documents
     * @param replace  all matching documents will be replaced with this
     * @param resultHandler will be called when complete
     */
    def replaceDocumentsL(collection: String, query: JsonObject, replace: JsonObject): Task[MongoClientUpdateResult] =
      Task.handle[MongoClientUpdateResult] { resultHandler =>
        target.replaceDocuments(collection, query, replace, resultHandler)
      }

    /**
     *  Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
     * @param collection  the collection
     * @param query  query used to match the documents
     * @param replace  all matching documents will be replaced with this
     * @param options options to configure the replace
     * @param resultHandler will be called when complete
     */
    def replaceDocumentsWithOptionsL(collection: String, query: JsonObject, replace: JsonObject, options: UpdateOptions): Task[MongoClientUpdateResult] =
      Task.handle[MongoClientUpdateResult] { resultHandler =>
        target.replaceDocumentsWithOptions(collection, query, replace, options, resultHandler)
      }

    /**
     *  Execute a bulk operation. Can insert, update, replace, and/or delete multiple documents with one request.
     * @param collection
     *           the collection
     * @param operations
     *           the operations to execute
     * @param resultHandler
     *           will be called with a {@link MongoClientBulkWriteResult} when complete
     */
    def bulkWriteL(collection: String, operations: List[BulkOperation]): Task[MongoClientBulkWriteResult] =
      Task.handle[MongoClientBulkWriteResult] { resultHandler =>
        target.bulkWrite(collection, operations, resultHandler)
      }

    /**
     *  Execute a bulk operation with the specified write options. Can insert, update, replace, and/or delete multiple
     *  documents with one request.
     * @param collection
     *           the collection
     * @param operations
     *           the operations to execute
     * @param bulkWriteOptions
     *           the write options
     * @param resultHandler
     *           will be called with a {@link MongoClientBulkWriteResult} when complete
     */
    def bulkWriteWithOptionsL(collection: String, operations: List[BulkOperation], bulkWriteOptions: BulkWriteOptions): Task[MongoClientBulkWriteResult] =
      Task.handle[MongoClientBulkWriteResult] { resultHandler =>
        target.bulkWriteWithOptions(collection, operations, bulkWriteOptions, resultHandler)
      }

    /**
     *  Find matching documents in the specified collection
     * @param collection  the collection
     * @param query  query used to match documents
     * @param resultHandler  will be provided with list of documents
     */
    def findL(collection: String, query: JsonObject): Task[List[JsonObject]] =
      Task.handle[List[JsonObject]] { resultHandler =>
        target.find(collection, query, resultHandler)
      }

    /**
     *  Find matching documents in the specified collection, specifying options
     * @param collection  the collection
     * @param query  query used to match documents
     * @param options options to configure the find
     * @param resultHandler  will be provided with list of documents
     */
    def findWithOptionsL(collection: String, query: JsonObject, options: FindOptions): Task[List[JsonObject]] =
      Task.handle[List[JsonObject]] { resultHandler =>
        target.findWithOptions(collection, query, options, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param fields  the fields
     * @param resultHandler will be provided with the document, if any
     */
    def findOneL(collection: String, query: JsonObject, fields: JsonObject): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOne(collection, query, fields, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection and update it.
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param update used to describe how the documents will be updated
     * @param resultHandler will be provided with the document, if any
     */
    def findOneAndUpdateL(collection: String, query: JsonObject, update: JsonObject): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOneAndUpdate(collection, query, update, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection and update it.
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param update used to describe how the documents will be updated
     * @param findOptions options to configure the find
     * @param updateOptions options to configure the update
     * @param resultHandler will be provided with the document, if any
     */
    def findOneAndUpdateWithOptionsL(collection: String, query: JsonObject, update: JsonObject, findOptions: FindOptions, updateOptions: UpdateOptions): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOneAndUpdateWithOptions(collection, query, update, findOptions, updateOptions, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection and replace it.
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param replace  the replacement document
     * @param resultHandler will be provided with the document, if any
     */
    def findOneAndReplaceL(collection: String, query: JsonObject, replace: JsonObject): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOneAndReplace(collection, query, replace, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection and replace it.
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param replace  the replacement document
     * @param findOptions options to configure the find
     * @param updateOptions options to configure the update
     * @param resultHandler will be provided with the document, if any
     */
    def findOneAndReplaceWithOptionsL(collection: String, query: JsonObject, replace: JsonObject, findOptions: FindOptions, updateOptions: UpdateOptions): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOneAndReplaceWithOptions(collection, query, replace, findOptions, updateOptions, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection and delete it.
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param resultHandler will be provided with the deleted document, if any
     */
    def findOneAndDeleteL(collection: String, query: JsonObject): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOneAndDelete(collection, query, resultHandler)
      }

    /**
     *  Find a single matching document in the specified collection and delete it.
     *  <p>
     *  This operation might change <i>_id</i> field of <i>query</i> parameter
     * @param collection  the collection
     * @param query  the query used to match the document
     * @param findOptions options to configure the find
     * @param resultHandler will be provided with the deleted document, if any
     */
    def findOneAndDeleteWithOptionsL(collection: String, query: JsonObject, findOptions: FindOptions): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.findOneAndDeleteWithOptions(collection, query, findOptions, resultHandler)
      }

    /**
     *  Count matching documents in a collection.
     * @param collection  the collection
     * @param query  query used to match documents
     * @param resultHandler will be provided with the number of matching documents
     */
    def countL(collection: String, query: JsonObject): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.count(collection, query, resultHandler)
      }.map(out => out: Long)

    /**
     *  Remove matching documents from a collection and return the handler with MongoClientDeleteResult result
     * @param collection  the collection
     * @param query  query used to match documents
     * @param resultHandler will be called when complete
     */
    def removeDocumentsL(collection: String, query: JsonObject): Task[MongoClientDeleteResult] =
      Task.handle[MongoClientDeleteResult] { resultHandler =>
        target.removeDocuments(collection, query, resultHandler)
      }

    /**
     *  Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result
     * @param collection  the collection
     * @param query  query used to match documents
     * @param writeOption  the write option to use
     * @param resultHandler will be called when complete
     */
    def removeDocumentsWithOptionsL(collection: String, query: JsonObject, writeOption: WriteOption): Task[MongoClientDeleteResult] =
      Task.handle[MongoClientDeleteResult] { resultHandler =>
        target.removeDocumentsWithOptions(collection, query, writeOption, resultHandler)
      }

    /**
     *  Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result
     * @param collection  the collection
     * @param query  query used to match document
     * @param resultHandler will be called when complete
     */
    def removeDocumentL(collection: String, query: JsonObject): Task[MongoClientDeleteResult] =
      Task.handle[MongoClientDeleteResult] { resultHandler =>
        target.removeDocument(collection, query, resultHandler)
      }

    /**
     *  Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result
     * @param collection  the collection
     * @param query  query used to match document
     * @param writeOption  the write option to use
     * @param resultHandler will be called when complete
     */
    def removeDocumentWithOptionsL(collection: String, query: JsonObject, writeOption: WriteOption): Task[MongoClientDeleteResult] =
      Task.handle[MongoClientDeleteResult] { resultHandler =>
        target.removeDocumentWithOptions(collection, query, writeOption, resultHandler)
      }

    /**
     *  Create a new collection
     * @param collectionName  the name of the collection
     * @param resultHandler  will be called when complete
     */
    def createCollectionL(collectionName: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.createCollection(collectionName, resultHandler)
      }.map(_ => ())

    /**
     *  Get a list of all collections in the database.
     * @param resultHandler  will be called with a list of collections.
     */
    def getCollectionsL(): Task[List[String]] =
      Task.handle[List[String]] { resultHandler =>
        target.getCollections(resultHandler)
      }

    /**
     *  Drop a collection
     * @param collection  the collection
     * @param resultHandler will be called when complete
     */
    def dropCollectionL(collection: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.dropCollection(collection, resultHandler)
      }.map(_ => ())

    /**
     *  Creates an index.
     * @param collection  the collection
     * @param key  A document that contains the field and value pairs where the field is the index key and the value
     *              describes the type of index for that field. For an ascending index on a field,
     *              specify a value of 1; for descending index, specify a value of -1.
     * @param resultHandler will be called when complete
     */
    def createIndexL(collection: String, key: JsonObject): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.createIndex(collection, key, resultHandler)
      }.map(_ => ())

    /**
     *  Creates an index.
     * @param collection  the collection
     * @param key  A document that contains the field and value pairs where the field is the index key and the value
     *              describes the type of index for that field. For an ascending index on a field,
     *              specify a value of 1; for descending index, specify a value of -1.
     * @param options  the options for the index
     * @param resultHandler will be called when complete
     */
    def createIndexWithOptionsL(collection: String, key: JsonObject, options: IndexOptions): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.createIndexWithOptions(collection, key, options, resultHandler)
      }.map(_ => ())

    /**
     *  Get all the indexes in this collection.
     * @param collection  the collection
     * @param resultHandler will be called when complete
     */
    def listIndexesL(collection: String): Task[JsonArray] =
      Task.handle[JsonArray] { resultHandler =>
        target.listIndexes(collection, resultHandler)
      }

    /**
     *  Drops the index given its name.
     * @param collection  the collection
     * @param indexName the name of the index to remove
     * @param resultHandler will be called when complete
     */
    def dropIndexL(collection: String, indexName: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.dropIndex(collection, indexName, resultHandler)
      }.map(_ => ())

    /**
     *  Run an arbitrary MongoDB command.
     * @param commandName  the name of the command
     * @param command  the command
     * @param resultHandler  will be called with the result.
     */
    def runCommandL(commandName: String, command: JsonObject): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.runCommand(commandName, command, resultHandler)
      }

    /**
     *  Gets the distinct values of the specified field name.
     *  Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
     * @param collection  the collection
     * @param fieldName  the field name
     * @param resultHandler  will be provided with array of values.
     */
    def distinctL(collection: String, fieldName: String, resultClassname: String): Task[JsonArray] =
      Task.handle[JsonArray] { resultHandler =>
        target.distinct(collection, fieldName, resultClassname, resultHandler)
      }

    /**
     *  Gets the distinct values of the specified field name filtered by specified query.
     *  Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
     * @param collection  the collection
     * @param fieldName  the field name
     * @param query the query
     * @param resultHandler  will be provided with array of values.
     */
    def distinctWithQueryL(collection: String, fieldName: String, resultClassname: String, query: JsonObject): Task[JsonArray] =
      Task.handle[JsonArray] { resultHandler =>
        target.distinctWithQuery(collection, fieldName, resultClassname, query, resultHandler)
      }
  }


}