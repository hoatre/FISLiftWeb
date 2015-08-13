package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 13/08/2015.
 */
class GroupFunction private () extends MongoRecord[GroupFunction] with StringPk[GroupFunction] {

  override def meta = GroupFunction

  // An embedded document:
  object  group_id extends StringField(this,1024)
  object func_id extends StringField(this,1024)
  object name extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)


}
object GroupFunction extends GroupFunction with MongoMetaRecord[GroupFunction] {
  override def collectionName = "GroupFunctions"

  override def mongoIdentifier = UsersDb

}