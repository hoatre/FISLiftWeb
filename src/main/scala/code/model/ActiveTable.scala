package code.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{ObjectIdField, ObjectIdPk}
import net.liftweb.record.field.{DoubleField, StringField}

/**
 * Created by bacnv on 06/08/2015.
 */
class ActiveTable private () extends MongoRecord[ActiveTable] with ObjectIdPk[ActiveTable] {

  override def meta = ActiveTable

  // An embedded document:
  object modelid extends StringField(this,1024)
}
object ActiveTable extends ActiveTable with MongoMetaRecord[ActiveTable] {
  override def collectionName = "activetable"
}

