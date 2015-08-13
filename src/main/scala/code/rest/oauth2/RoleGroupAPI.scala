package code.rest.oauth2

import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object RoleGroupAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(RoleGroupAPI)
  }

  serve{
    case "rolegroup" :: Nil Options _ => OkResponse()
    case "rolegroup" :: "getall" :: Nil JsonGet req => OkResponse()

    case "rolegroup" :: "getbyid" :: q ::Nil JsonGet req => OkResponse()
    case "rolegroup" :: "insert" :: Nil JsonPost json -> request => OkResponse()
    case "rolegroup" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "rolegroup" :: "delete" :: Nil JsonPost json -> request => OkResponse()

  }

}
