/*
 * Copyright 2019 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb.mongodb

import scala.reflect._
// import scala.reflect.runtime.universe._
import java.util.concurrent.ConcurrentHashMap

import org.mongodb.scala._
// import com.mongodb.MongoException

import net.liftweb.util.ConnectionIdentifier

import scala.concurrent.Promise

/**
  * Async version of MongoDB that uses the mong-scala-driver.
  *
  * You should only have one instance of MongoClient in a JVM.
  *
  * Example:
  *
  * {{{
  * import com.mongodb.MongoClientSettings
  * import com.mongodb.async.client.MongoClients
  * import net.liftweb.util.{ConnectionIdentifier, DefaultConnectionIdentifier}
  * import org.bson.codecs.configuration.CodecRegistries
  *
  * val client = MongoClients.create("mongodb://127.0.0.1:27017")
  *
  * // main database
  * MongoScalaAsync.defineDb(DefaultConnectionIdentifier, client.getDatabase("mydb"))
  *
  * // admin database
  * case object AdminIdentifier extends ConnectionIdentifier {
  *   val jndiName = "admin"
  * }
  *
  * val codecRegistry = CodecRegistries.fromRegistries(
  *   MongoClientSettings.getDefaultCodecRegistry(),
  *   CodecRegistries.fromCodecs(new LongPrimitiveCodec, new IntegerPrimitiveCodec)
  * )

  * val admin = client.getDatabase("admin").withCodecRegistry(codecRegistry)
  * MongoScalaAsync.defineDb(AdminIdentifier, admin)
  *
  * }}}
  */
object MongoScalaAsync {

  /**
    * HashMap of MongoDatabase instances keyed by ConnectionIdentifier
    */
  private[this] val dbs = new ConcurrentHashMap[ConnectionIdentifier, MongoDatabase]

  /**
    * Define a Mongo db using a MongoDatabase instance.
    */
  def defineDb(id: ConnectionIdentifier, db: MongoDatabase): Unit = {
    dbs.put(id, db)
  }

  /**
    * Get a MongoDatabase reference
    */
  private[this] def getDatabase(name: ConnectionIdentifier): Option[MongoDatabase] = {
    Option(dbs.get(name))
  }

  /**
    * Executes function {@code f} with the mongo database identified by {@code name}.
    */
  def use[T](name: ConnectionIdentifier)(f: (MongoDatabase) => T): T = {
    val db = getDatabase(name) match {
      case Some(mongo) =>
        mongo
      case _ =>
        throw new MongoException("Mongo not found: "+name.toString)
    }
    f(db)
  }

  /**
    * Executes function {@code f} with the collection named {@code collectionName} from
    * the mongo database identified by {@code name}.
    */
  def useCollection[TDocument, T](name: ConnectionIdentifier, collectionName: String, documentClass: ClassTag[TDocument])(f: (MongoCollection[TDocument]) => T): T = {
    val coll = getCollection[TDocument](name, collectionName)(documentClass) match {
      case Some(collection) =>
        collection
      case _ =>
        throw new MongoException("Mongo not found: "+collectionName+". ConnectionIdentifier: "+name.toString)
    }

    f(coll)
  }

  private[this] def getCollection[TDocument: ClassTag](name: ConnectionIdentifier, collectionName: String): Option[MongoCollection[TDocument]] = {
    getDatabase(name).map(_.getCollection[TDocument](collectionName))
  }

  /**
    * Clear the HashMap.
    */
  def clear(): Unit = {
    dbs.clear()
  }

  /**
    * Remove a specific ConnectionIdentifier from the HashMap.
    */
  def remove(id: ConnectionIdentifier): Option[MongoDatabase] = {
    Option(dbs.remove(id))
  }
}
