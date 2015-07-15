package code.model

import net.liftweb.common.Full
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{StringRefField, BsonRecordListField, BsonRecordField, StringPk}
import net.liftweb.record.field.{DoubleField, StringField}

/**
 * Created by bacnv on 7/14/15.
 */
class Rating private () extends MongoRecord[Rating] with StringPk[Rating] {

  override def meta = Rating

  // An embedded document:
  object modelid extends StringRefField(this, ModelInfo, 512){
    override def options = ModelInfo.findAll.map(rd => (Full(rd.id.is), rd.name.is) )
  }
  object modelname extends StringField(this,1024)
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
  object note extends StringField(this,1024)
}

object codeIN extends codeIN with BsonMetaRecord[codeIN]
