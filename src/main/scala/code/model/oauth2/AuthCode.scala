package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{LongField, StringField}

/**
 * Created by bacnv on 12/08/2015.
 */
class AuthCode private () extends MongoRecord[AuthCode] with StringPk[AuthCode] {
  override def meta = AuthCode

  object authorization_code extends StringField(this,1024)
  object user_id  extends StringField(this,1024)
  object redirect_uri extends StringField(this,1024)
  object created_at extends LongField(this,15)
  object scope  extends StringField(this,1024)
  object client_id extends StringField(this,1024)
  object expires_in extends LongField(this)


}

object AuthCode extends AuthCode with MongoMetaRecord[AuthCode] {
  override def collectionName = "auth_codes"

  override def mongoIdentifier = UsersDb

}
