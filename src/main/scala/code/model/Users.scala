package code.model

import net.liftweb.mongodb.record.field.{ObjectIdPk, BsonRecordField, StringPk}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.StringField


/**
 * Created by bacnv on 7/7/15.
 */
class Users private () extends MongoRecord[Users] with ObjectIdPk[Users] {

  override def meta = Users

  // An embedded document:
  object user extends BsonRecordField(this, userIN)
  object _v extends StringField(this, 1024)

}
object Users extends Users with MongoMetaRecord[Users] {
  override def collectionName = "users"
}

class userIN private () extends BsonRecord[userIN] {
  def meta = userIN
  object address extends StringField(this, 1024)
  object name extends StringField(this, 1024)
  object password extends StringField(this, 1024)
  object email extends StringField(this, 1024)
  object username extends StringField(this, 1024)
}

object userIN extends userIN with BsonMetaRecord[userIN]