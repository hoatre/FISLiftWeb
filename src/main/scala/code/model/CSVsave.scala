package code.model

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord, MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{BsonRecordField, ObjectIdPk}
import net.liftweb.record.field.StringField

/**
 * Created by bacnv on 7/23/15.
 */
class CSVsave private () extends MongoRecord[CSVsave] with ObjectIdPk[CSVsave] {

  override def meta = CSVsave

  // An embedded document:

  object _v extends StringField(this, 1024)

}
object CSVsave extends CSVsave with MongoMetaRecord[CSVsave] {
  override def collectionName = "CSVsave"
}

