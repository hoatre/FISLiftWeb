package code.model

import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.record.{MongoRecord, MongoMetaRecord}
import net.liftweb.common.Box
import net.liftweb.record.field.{StringField, LongField}

/**
 * Created by bacnv on 11/08/2015.
 */
class AccessToken  private () extends MongoRecord[AccessToken] with StringPk[AccessToken] {

  override def meta = AccessToken

  object  access_token extends StringField(this,1024)
  object  refresh_token extends StringField(this,1024)
  object  client_id extends StringField(this,1024)
  object  user_id extends StringField(this,1024)
  object  scope extends StringField(this,1024)
  object  expires_in extends LongField(this)
  object  created_at extends LongField(this)
}

object AccessToken extends AccessToken with MongoMetaRecord[AccessToken] {
  override def collectionName = "accesstokens"
  override def mongoIdentifier = UsersDb

//  /**
//   * Fetch AccessToken by its ID.
//   * @param id
//   * @return
//   */
//  def get(id:String) : Option[AccessToken] ={
//    AccessToken.find("_id" -> id)
//
//  }
//
//  /**
//   * Find AccessToken by token value
//   * @param accesstoken
//   * @return
//   */
//  def find(accesstoken :String) : Option[AccessToken] = {
//    AccessToken.find("access_token" -> accesstoken)
//  }
//
//  /**
//   * Find AccessToken by User and Client
//   * @param userid
//   * @param clientId
//   * @return
//   */
//  def findByUserAndClient(userid: String, clientId: String): Option[AccessToken] ={
//    AccessToken.find(("user_id" -> userid) ~ ("client_id" -> clientId))
//  }
//  /**
//   * Find Refresh Token by its value
//   * @param refreshToken
//   * @return
//   */
//  def findByRefreshToken(refreshToken: String): Option[AccessToken] ={
//    AccessToken.find("refresh_token" -> refreshToken)
//  }
//
//  /**
//   * Add a new AccessToken
//   * @param token
//   * @return
//   */
//  def insert(token: AccessToken)= {
//    token.id match {
//      case Some(x) => token.save
//    }
//  }
//  /**
//   * Update AccessToken object based for the ID in accessToken object
//   * @param accessToken
//   * @return
//   */
//  def update(accessToken: AccessToken) = {
//    AccessToken.update(accessToken)
//  }



}
