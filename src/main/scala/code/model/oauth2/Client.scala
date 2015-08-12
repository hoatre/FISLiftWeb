package code.model.oauth2

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, PasswordField, StringField}
import net.liftweb.mongodb.BsonDSL._

/**
 * Created by bacnv on 12/08/2015.
 */
class Client private () extends MongoRecord[Client] with StringPk[Client] {

  override def meta = Client

  // An embedded document:
  object client_id  extends StringField(this,1024)
  object  username extends StringField(this,1024)
  object client_secret extends StringField(this,1024)
  object description extends StringField(this,1024)
  object redirect_uri extends StringField(this,1024)
  object scope extends StringField(this,1024)


}
object Client extends Client with MongoMetaRecord[Client] {
  override def collectionName = "clients"

  override def mongoIdentifier = UsersDb

  def validate(clientId: String, clientSecret: String, grantType: String): Boolean = {

    val dbclient = Client.findAll(("client_id" -> clientId) ~ ("client_secret" -> clientSecret))
    val dbgrantype = GrantType.findAll("grant_type" -> grantType)
    if(dbgrantype.size > 0 && dbclient.size > 0){

      for(i<-0 to dbgrantype.size -1){
        val dbclgrant = ClientGrantType.findAll(("client_id" -> clientId) ~ ("grant_type_id" -> dbgrantype(i).id.toString()))
        if(dbclgrant.size>0){
          return true
        }

      }
      return false

    }else{
      return false
    }


  }
}
