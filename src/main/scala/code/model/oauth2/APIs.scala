package code.model.oauth2

import java.util.UUID

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{LongField, StringField}
import net.liftweb.util.Props

import net.liftweb.mongodb.{Limit, Skip}
import net.liftweb.mongodb.BsonDSL

import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._

/**
 * Created by bacnv on 07/09/2015.
 */
class APIs private () extends MongoRecord[APIs] with StringPk[APIs] {

  override def meta = APIs

  // An embedded document:
  object  api extends StringField(this,1024)
  object  app_id extends StringField(this,1024)
  object description extends StringField(this,1024)
  object status extends StringField(this,1024)
  object note extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)
  object modified_by extends StringField(this,1024)
  object modified_date extends LongField(this)
  object apitype extends LongField(this)


}
object APIs extends APIs with MongoMetaRecord[APIs] {
  override def collectionName = "apis"

  override def mongoIdentifier = UsersDb

  def insertBoot(api: String): Unit = {
    val f = APIs.findAll("api" -> api)
    if (f.size == 0) {
      APIs.createRecord.id(UUID.randomUUID().toString).created_by("system").modified_by("system").app_id(Props.props.apply("app_id")).created_date(System.currentTimeMillis()).description("").modified_date(System.currentTimeMillis()).api(api)
        .note("").status("active").save(true)
    }

  }
}
