package code.model.oauth2

import scala.concurrent.Future
import scalaoauth2.provider.{AuthInfo, AccessToken}

/**
 * Created by bacnv on 12/08/2015.
 */
trait ProtectedResourceHandler [+U] {

  /**
   * Find authorized information by access token.
   *
   * @param accessToken This value is AccessToken.
   * @return Return authorized information if the parameter is available.
   */
  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[U]]]

  /**
   * Find AccessToken object by access token code.
   *
   * @param token Client sends access token which is created by system.
   * @return Return access token that matched the token.
   */
  def findAccessToken(token: String): Future[Option[AccessToken]]

}