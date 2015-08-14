package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{BooleanField, LongField, StringField}
import net.liftweb.mongodb.BsonDSL._

/**
 * Created by bacnv on 12/08/2015.
 */
class GoogleModel private () extends MongoRecord[GoogleModel] with StringPk[GoogleModel] {

  override def meta = GoogleModel

  object  issuer extends StringField(this,1024)
  object  issued_to extends StringField(this,1024)
  object  audience extends StringField(this,1024)
  object  user_id extends StringField(this,1024)
  object  expires_in extends LongField(this)
  object  issued_at extends LongField(this)
  object  email extends StringField(this,1024)
  object  email_verified extends BooleanField(this)
}

object GoogleModel extends GoogleModel with MongoMetaRecord[GoogleModel] {
  override def collectionName = "googleinfo"

  override def mongoIdentifier = UsersDb


  def updateByUserAndClient(google: GoogleModel) = {

    GoogleModel.delete(("email" -> google.email.toString()) ~ ("user_id" -> google.user_id.toString()))

    google.save(true)

  }

}

