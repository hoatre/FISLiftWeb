package code.rest.oauth2

import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object GroupAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupAPI)
  }

  serve{
    case "group" :: Nil Options _ => OkResponse()
    case "group" :: "getall" :: Nil JsonGet req => OkResponse()

    case "group" :: "getbyid" :: q ::Nil JsonGet req => OkResponse()
    case "group" :: "insert" :: Nil JsonPost json -> request => OkResponse()
    case "group" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "group" :: "delete" :: Nil JsonPost json -> request => OkResponse()

  }

}
