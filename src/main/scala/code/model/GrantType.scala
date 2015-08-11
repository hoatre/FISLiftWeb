package code.model

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.record.{MongoRecord, MongoMetaRecord}
import net.liftweb.record.field.StringField

/**
 * Created by bacnv on 11/08/2015.
 */
class GrantType private () extends MongoRecord[GrantType] with StringPk[GrantType] {

  override def meta = GrantType

  object grant_type extends StringField(this,1024)

}

object GrantType extends GrantType with MongoMetaRecord[GrantType] {
  override def collectionName = "grant_types"
  override def mongoIdentifier = UsersDb

}
