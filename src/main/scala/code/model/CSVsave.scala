package code.model

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdField, StringPk, BsonRecordField, ObjectIdPk}
import net.liftweb.record.field.{DoubleField, StringField}

/**
 * Created by bacnv on 7/23/15.
 */
class CSVsave private () extends MongoRecord[CSVsave] with ObjectIdPk[CSVsave] {

  override def meta = CSVsave

  // An embedded document:
  object session  extends ObjectIdField(this)
  object customer extends StringField(this,1024)
  object customerid extends ObjectIdField(this)
  object score extends DoubleField(this)
  object rating extends StringField(this,1024)
  object status extends StringField(this,1024)


}
object CSVsave extends CSVsave with MongoMetaRecord[CSVsave] {
  override def collectionName = "CSVsave"
}

