package code.model

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{BsonRecordListField, BsonRecordField, StringPk}
import net.liftweb.record.field.{DoubleField, StringField}

/**
 * Created by bacnv on 7/14/15.
 */
class Rating private () extends MongoRecord[Rating] with StringPk[Rating] {

  override def meta = Rating

  // An embedded document:
  object moduleid extends StringField(this,1024)
  object codein extends BsonRecordListField(this,codeIN)

}
object Rating extends Rating with MongoMetaRecord[Rating] {
  override def collectionName = "rating"
}

class codeIN private () extends BsonRecord[codeIN] {
  def meta = codeIN
  object code extends StringField(this, 1024)
  object status extends StringField(this, 1024)
  object statusname extends StringField(this, 1024)
  object scorefrom extends DoubleField(this, 10)
  object scoreto extends DoubleField(this, 10)
}

object codeIN extends codeIN with BsonMetaRecord[codeIN]
