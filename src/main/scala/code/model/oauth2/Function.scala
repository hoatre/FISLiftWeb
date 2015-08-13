package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 13/08/2015.
 */
class Function private () extends MongoRecord[Function] with StringPk[Function] {

  override def meta = Function

  // An embedded document:
  object parent_id  extends StringField(this,1024)
  object  app_id extends StringField(this,1024)
  object name extends StringField(this,1024)
  object description extends StringField(this,1024)
  object status extends StringField(this,1024)
  object note extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)
  object modified_by extends StringField(this,1024)
  object modified_date extends LongField(this)


}
object Function extends Function with MongoMetaRecord[Function] {
  override def collectionName = "Functions"

  override def mongoIdentifier = UsersDb

}