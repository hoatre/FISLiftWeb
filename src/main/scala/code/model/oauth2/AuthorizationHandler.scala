package code.model.oauth2

import scala.concurrent.Future
import scalaoauth2.provider.ClientCredential

/**
 * Created by bacnv on 12/08/2015.
 */
trait AuthorizationHandler[U] {

  /**
   * Verify proper client with parameters for issue an access token.
   *
   * @param clientCredential Client sends clientId and clientSecret which are registered by application.
   * @param grantType Client sends this value which is registered by application.
   * @return true if request is a regular client, false if request is a illegal client.
   */
  def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean]

  /**
   * Find userId with username and password these are used on your system.
   * If you don't support Resource Owner Password Credentials Grant then doesn't need implementing.
   *
   * @param username Client sends this value which is used on your system.
   * @param password Client sends this value which is used on your system.
   * @return Including UserId to Option if could find the user, None if couldn't find.
   */
  def findUser(username: String, password: String): Future[Option[U]]

  /**
   * Creates a new access token by authorized information.
   *
   * @param authInfo This value is already authorized by system.
   * @return Access token returns to client.
   */
  def createAccessToken(authInfo: AuthInfo[U]): Future[AccessToken]

  /**
   * Returns stored access token by authorized information.
   *
   * If want to create new access token then have to return None
   *
   * @param authInfo This value is already authorized by system.
   * @return Access token returns to client.
   */
  def getStoredAccessToken(authInfo: AuthInfo[U]): Future[Option[AccessToken]]

  /**
   * Creates a new access token by refreshToken.
   *
   * @param authInfo This value is already authorized by system.
   * @return Access token returns to client.
   */
  def refreshAccessToken(authInfo: AuthInfo[U], refreshToken: String): Future[AccessToken]

  /**
   * Find authorized information by authorization code.
   *
   * If you don't support Authorization Code Grant then doesn't need implementing.
   *
   * @param code Client sends authorization code which is registered by system.
   * @return Return authorized information that matched the code.
   */
  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[U]]]

  /**
   * Deletes an authorization code.
   *
   * Called when an AccessToken has been successfully issued via an authorization code.
   *
   * If you don't support Authorization Code Grant, then you don't need to implement this
   * method.
   *
   * @param code Client-sent authorization code
   */
  def deleteAuthCode(code: String): Future[Unit]

  /**
   * Find authorized information by refresh token.
   *
   * If you don't support Refresh Token Grant then doesn't need implementing.
   *
   * @param refreshToken Client sends refresh token which is created by system.
   * @return Return authorized information that matched the refresh token.
   */
  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[U]]]

  /**
   * Find user by clientId and clientSecret.
   *
   * If you don't support Client Credentials Grant then doesn't need implementing.
   *
   * @param clientCredential Client sends clientId and clientSecret which are registered by application.
   * @return Return user that matched both values.
   */
  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[U]]

}