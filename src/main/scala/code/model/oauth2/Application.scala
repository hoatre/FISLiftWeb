package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 13/08/2015.
 */
class Application private () extends MongoRecord[Application] with StringPk[Application] {

  override def meta = Application

  // An embedded document:
  object name  extends StringField(this,1024)
  object  desciption extends StringField(this,1024)
  object status extends StringField(this,1024)
  object note extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)
  object modified_by extends StringField(this,1024)
  object modified_date extends LongField(this)


}
object Application extends Application with MongoMetaRecord[Application] {
  override def collectionName = "Applications"

  override def mongoIdentifier = UsersDb

}