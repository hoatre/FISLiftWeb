package code.model

import net.liftweb.common.Full
import net.liftweb.mongodb.record.{MongoRecord, BsonMetaRecord, BsonRecord, MongoMetaRecord}
import net.liftweb.mongodb.record.field._
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
  object model_name extends StringField(this,1024)
  object customer_name extends  StringField(this,1024)
  object customer_id  extends ObjectIdField(this)
  object session  extends  ObjectIdField(this)
  object timestamp extends StringField(this,15)
  object scoring extends DoubleField(this)
  object rating_code extends StringField(this, 1024)
  object rating_status extends StringField(this, 1024)
  object resultin extends BsonRecordListField(this,resultIN)
  object factor extends BsonRecordListField(this,Factor)
  object model  extends BsonRecordField(this,ModelInfo)
  object rate extends BsonRecordField(this,Rating)
}

object ScoringResult extends ScoringResult with MongoMetaRecord[ScoringResult] {
  override def collectionName = "ScoringResult"
}

class resultIN private () extends BsonRecord[resultIN] {
  def meta = resultIN
//  object path_factor extends BsonRecordListField(this, FactorPath)
  object factor_id extends StringRefField(this, Factor, 512){
    override def options = Factor.findAll.map(rd => (Full(rd.id.is), rd.FactorName.is) )
  }
  object factor_score extends DoubleField(this, 10)
  object factor_name extends StringField(this, 1024)
  object factor_option_id extends StringField(this, 1024)
  object factor_option_name extends StringField(this,1024)
  object factor_option_score extends DoubleField(this, 10)
}

object resultIN extends resultIN with BsonMetaRecord[resultIN]
