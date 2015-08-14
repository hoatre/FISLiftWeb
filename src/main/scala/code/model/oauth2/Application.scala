package code.model.oauth2

import java.util.UUID

import bootstrap.liftweb.UsersDb
import code.common.Message
import com.mongodb.QueryBuilder
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb.{Limit, Skip}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdPk, StringPk}
import net.liftweb.record.field.{LongField, StringField}
import org.bson.types.ObjectId
import net.liftweb.json.JsonAST.{JObject, JValue}



import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model._
import net.liftweb.json.Printer._
import net.liftweb.mongodb.{Limit, Skip, JObjectParser}
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import net.liftweb.util.{Helpers, Props}
import org.bson.types.ObjectId

import scala.collection.immutable.HashMap
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Random
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.S


/**
 * Created by bacnv on 13/08/2015.
 */
class Applications private () extends MongoRecord[Applications] with StringPk[Applications] {

  override def meta = Applications

  // An embedded document:
  object name  extends StringField(this,1024)
  object  description extends StringField(this,1024)
  object status extends StringField(this,1024)
  object note extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)
  object modified_by extends StringField(this,1024)
  object modified_date extends LongField(this)


}
object Applications extends Applications with MongoMetaRecord[Applications] {
  override def collectionName = "Applications"

  override def mongoIdentifier = UsersDb



}