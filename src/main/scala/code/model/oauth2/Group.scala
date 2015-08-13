package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 13/08/2015.
 */
class Group private () extends MongoRecord[Group] with StringPk[Group] {

  override def meta = Group

  // An embedded document:
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
object Group extends Group with MongoMetaRecord[Group] {
  override def collectionName = "Groups"

  override def mongoIdentifier = UsersDb

}