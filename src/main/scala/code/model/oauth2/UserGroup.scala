package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 13/08/2015.
 */
class UserGroup private () extends MongoRecord[UserGroup] with StringPk[UserGroup] {

  override def meta = UserGroup

  // An embedded document:
  object user_id extends StringField(this,1024)
  object group_id extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)


}
object UserGroup extends UserGroup with MongoMetaRecord[UserGroup] {
  override def collectionName = "UserGroups"

  override def mongoIdentifier = UsersDb

}