package code.rest.oauth2

import code.common.Message
import net.liftweb.common.CombinableBox.Result
import net.liftweb.http.{Req, OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.JValue
import code.model.oauth2.{User, MyDataHandler}

import scalaoauth2.provider.{ProtectedResource, AuthInfo, DataHandler}

/**
 * Created by bacnv on 13/08/2015.
 */
object UserAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(UserAPI)
  }

  serve{
    case "user" :: Nil Options _ => OkResponse()
    case "user" :: "search" :: q JsonGet req => User.searh(q)
    case "user" :: "getall" :: Nil JsonGet req => OkResponse()

    case "user" :: "getbyid" :: q ::Nil JsonGet req => OkResponse()
    case "user" :: "insert" :: Nil JsonPost json -> request => User.insert(json)
    case "user" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "user" :: "delete" :: Nil JsonPost json -> request => OkResponse()

  }

}
