package code.model

import net.liftweb.common.Full
import net.liftweb.mongodb.record.{MongoRecord, BsonMetaRecord, BsonRecord, MongoMetaRecord}
import net.liftweb.mongodb.record.field.{ObjectIdPk, StringPk, BsonRecordListField, StringRefField}
import net.liftweb.record.field.{LongField, DateTimeField, DoubleField, StringField}

/**
 * Created by Administrator on 7/15/2015.
 */
class ScoringResult private () extends MongoRecord[ScoringResult] with ObjectIdPk[ScoringResult] {

  override def meta = ScoringResult

  // An embedded document:
  object modelid extends StringRefField(this, ModelInfo, 512){
    override def options = ModelInfo.findAll.map(rd => (Full(rd.id.is), rd.name.is) )
  }
  object time_stamp extends LongField(this)
  object scoring extends DoubleField(this)
  object rating_code extends StringField(this, 1024)
  object rating_status extends StringField(this, 1024)
  object resulin extends BsonRecordListField(this,resultIN)
  object factor extends BsonRecordListField(this,Factor)
}

object ScoringResult extends ScoringResult with MongoMetaRecord[ScoringResult] {
  override def collectionName = "ScoringResult"
}

class resultIN private () extends BsonRecord[resultIN] {
  def meta = resultIN
  object path_factor extends BsonRecordListField(this, FactorPath)
  object factor_id extends StringRefField(this, Factor, 512){
    override def options = Factor.findAll.map(rd => (Full(rd.id.is), rd.FactorName.is) )
  }
  object factor_option_id extends StringField(this, 1024)
  object factor_name extends StringField(this, 1024)
  object factor_option_score extends DoubleField(this, 10)
}

object resultIN extends resultIN with BsonMetaRecord[resultIN]
