package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.StringField

/**
 * Created by bacnv on 12/08/2015.
 */
class ClientGrantType private () extends MongoRecord[ClientGrantType] with StringPk[ClientGrantType] {

  override def meta = ClientGrantType

  object client_id extends StringField(this,1024)
  object grant_type_id extends StringField(this,1024)

}

object ClientGrantType extends ClientGrantType with MongoMetaRecord[ClientGrantType] {
  override def collectionName = "client_grant_types"
  override def mongoIdentifier = UsersDb

}
