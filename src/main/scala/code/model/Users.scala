package code.model


import net.liftweb.mongodb.record.field.{ObjectIdPk, BsonRecordField, StringPk}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{BooleanField, LongField, StringField}


/**
 * Created by bacnv on 7/7/15.
 */
class Users private () extends MongoRecord[Users] with ObjectIdPk[Users] {

  override def meta = Users

  // An embedded document:
  object name extends StringField(this,1024)
  object goole extends BsonRecordField(this, googleIN)
  object user extends BsonRecordField(this, userIN)



}
object Users extends Users with MongoMetaRecord[Users] {
  override def collectionName = "users"
}

class googleIN private () extends BsonRecord[googleIN]{
  def meta = googleIN
  object iss extends StringField(this,1024)
  object sub extends StringField(this,1024)
  object azp extends StringField(this,1024)
  object email extends StringField(this,1024)
  object at_hash extends StringField(this,1024)
  object email_verified extends BooleanField(this)
  object aud  extends StringField(this,1024)
  object iat  extends LongField(this)
  object exp  extends LongField(this)
  object token  extends StringField(this,1024)
}
object googleIN extends googleIN with BsonMetaRecord[googleIN]

class userIN private () extends BsonRecord[userIN] {
  def meta = userIN
  object address extends StringField(this, 1024)
  object name extends StringField(this, 1024)
  object password extends StringField(this, 1024)
  object email extends StringField(this, 1024)
  object username extends StringField(this, 1024)
}

object userIN extends userIN with BsonMetaRecord[userIN]