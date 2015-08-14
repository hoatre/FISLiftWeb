package code.model.oauth2

import java.sql.Timestamp
import java.util.{UUID, Date}


import net.liftweb.common.{Box, Full}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.{AuthInfo, ClientCredential, DataHandler}
import net.liftweb.mongodb.BsonDSL._

/**
 * Created by bacnv on 10/08/2015.
 */



//class MyDataHandler {
class MyDataHandler extends DataHandler[User]{

  def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] = Future{
    clientCredential.clientSecret match {
      case Some(x) =>   Client.validate(clientCredential.clientId,x,grantType)
      case None => false
    }

    }




  def findUser(username: String, password: String): Future[Option[User]] = Future{
    User.findByUsernameAndPassword(username,password)

  }
//
  def createAccessToken(authInfo: AuthInfo[User]): Future[AccessToken] = Future{
  val accessTokenExpiresIn = 60 * 60 // 1 hour
  val now = new Date()
  val createdAt = new Timestamp(now.getTime)
  val refreshToken = Bcypted.randomEctyed()
  val accessToken = Bcypted.randomEctyed()
  val scope = authInfo.scope
  val uId = authInfo.user.id.toString()
  val clientId = authInfo.clientId
  var tokenObject : AccessToken = AccessToken.createRecord
  clientId match {
    case Some(x) => {

      val client : net.liftweb.common.Box[Client]= Client.find("client_id" -> x)
      client match {
        case Full(c) => {
          tokenObject = AccessToken.createRecord.id(UUID.randomUUID().toString).access_token(accessToken)
            .refresh_token(refreshToken).client_id(c.client_id.toString()).user_id(uId).scope(scope match { case None => "" case Some(s) => s })
            .expires_in(accessTokenExpiresIn).created_at(createdAt.toString.toLong)

          AccessToken.updateByUserAndClient(tokenObject, uId , c.client_id.toString())
        }
        case _ => throw new UnsupportedOperationException
      }

    }
    case _ => throw new UnsupportedOperationException
  }

  tokenObject


  }
//
  def getStoredAccessToken(authInfo: AuthInfo[User]): Future[Option[AccessToken]] = Future{

  val clientId = authInfo.clientId
  var tokenObject : Option[AccessToken] = Option(AccessToken.createRecord)
  clientId match {
    case Some(x) => {

      val client: net.liftweb.common.Box[Client] = Client.find("client_id" -> x)

      client match {

        case Full(c) => {
          val uid = authInfo.user.id.toString()
          tokenObject = AccessToken.findByUserAndClient(uid , c.client_id.toString())

        }
        case _ => throw new UnsupportedOperationException
      }
    }
  }
  tokenObject
}

  def refreshAccessToken(authInfo: AuthInfo[User], refreshToken: String): Future[AccessToken] = Future{
    var tokenObject : AccessToken = AccessToken.createRecord
    tokenObject
    
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[User]]] = Future{
    var tokenObject : Option[AuthInfo[User]] = Option(AuthInfo(User.createRecord,null,null,null))

   tokenObject
  }

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[User]]] = Future{
    var tokenObject : Option[AuthInfo[User]] = Option(AuthInfo(User.createRecord,null,null,null))
    tokenObject
  }

  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[User]] = Future{
    var tokenObject : Option[User] =  Option(User.createRecord.id(UUID.randomUUID().toString))
    tokenObject
  }

  def deleteAuthCode(code: String): Future[Unit] = Future{

  }

  def findAccessToken(token: String): Future[Option[AccessToken]] = Future{
    var tokenObject : Option[AccessToken] = Option(AccessToken.createRecord)
    tokenObject
  }

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[User]]] = Future{
    var tokenObject : Option[AuthInfo[User]] = Option(AuthInfo(User.createRecord,null,null,null))
    tokenObject
  }

}
