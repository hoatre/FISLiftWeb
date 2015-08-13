package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 13/08/2015.
 */
class RoleGroup private () extends MongoRecord[RoleGroup] with StringPk[RoleGroup] {

  override def meta = RoleGroup

  // An embedded document:
  object role_id extends StringField(this,1024)
  object group_id extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)


}
object RoleGroup extends RoleGroup with MongoMetaRecord[RoleGroup] {
  override def collectionName = "RoleGroups"

  override def mongoIdentifier = UsersDb

}