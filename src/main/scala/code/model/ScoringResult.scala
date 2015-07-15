package code.model

import net.liftweb.common.Full
import net.liftweb.mongodb.record.{MongoRecord, BsonMetaRecord, BsonRecord, MongoMetaRecord}
import net.liftweb.mongodb.record.field.{StringPk, BsonRecordListField, StringRefField}
import net.liftweb.record.field.{LongField, DateTimeField, DoubleField, StringField}

/**
 * Created by Administrator on 7/15/2015.
 */
class ScoringResult private () extends MongoRecord[ScoringResult] with StringPk[ScoringResult] {

  override def meta = ScoringResult

  // An embedded document:
  object modelid extends StringRefField(this, ModelInfo, 512){
    override def options = ModelInfo.findAll.map(rd => (Full(rd.id.is), rd.name.is) )
  }
  object Timestamp extends LongField(this)
  object Scoring extends DoubleField(this)
  object RatingCode extends StringField(this, 1024)
  object ResultIN extends BsonRecordListField(this,resultIN)
}

object ScoringResult extends ScoringResult with MongoMetaRecord[ScoringResult] {
  override def collectionName = "ScoringResult"
}

class resultIN private () extends BsonRecord[resultIN] {
  def meta = resultIN
  object PathFactor extends BsonRecordListField(this, FactorPath)
  object FactorId extends StringRefField(this, Factor, 512){
    override def options = Factor.findAll.map(rd => (Full(rd.id.is), rd.FactorName.is) )
  }
  object FactorOptionId extends StringField(this, 1024)
  object FactorOptionName extends StringField(this, 1024)
  object FactorOptionScore extends DoubleField(this, 10)
}

object resultIN extends resultIN with BsonMetaRecord[resultIN]
