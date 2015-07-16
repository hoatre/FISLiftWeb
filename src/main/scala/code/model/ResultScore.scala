package code.model

import net.liftweb.common.Full
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{BsonRecordListField, StringRefField, StringPk}
import net.liftweb.record.field.{DoubleField, StringField}

/**
 * Created by bacnv on 7/15/15.
 */
class ResultScore private () extends MongoRecord[ResultScore] with StringPk[ResultScore] {

  override def meta = ResultScore

  // An embedded document:
  object modelid extends StringRefField(this, ModelInfo, 512){
    override def options = ModelInfo.findAll.map(rd => (Full(rd.id.is), rd.name.is) )
  }
  object modelname extends StringField(this,1024)
  object listresult extends BsonRecordListField(this,reultIN)

}
object ResultScore extends ResultScore with MongoMetaRecord[ResultScore] {
  override def collectionName = "resulcore"
}

class reultIN private () extends BsonRecord[reultIN] {
  def meta = reultIN
  object factorid extends StringField(this, 1024)
  object factoroptionid extends StringField(this, 1024)
  object factorname extends StringField(this, 1024)
  object score extends DoubleField(this, 10)
}

object reultIN extends reultIN with BsonMetaRecord[reultIN]