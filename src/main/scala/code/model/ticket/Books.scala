package code.model.ticket

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdPk, StringPk}
import net.liftweb.record.field.{IntField, DateTimeField, LongField, StringField}

/**
 * Created by bacnv on 26/08/2015.
 */
class Books private () extends MongoRecord[Books] with ObjectIdPk[Books] {

  override def meta = Books

  // An embedded document:
  object  email extends StringField(this,1024)
  object ticketype extends StringField(this,1024)
  object created_time extends DateTimeField(this)
  object status extends StringField(this,1024)
  object _v extends IntField(this)
}
object Books extends Books with MongoMetaRecord[Books] {
  override def collectionName = "Bookstest"
  override def mongoIdentifier = UsersDb

}
