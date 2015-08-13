package code.rest.oauth2

import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object ApplicationAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ApplicationAPI)
  }

  serve{
    case "application" :: Nil Options _ => OkResponse()
    case "application" :: "getall" :: Nil JsonGet req => OkResponse()

    case "application" :: "getbyid" :: q ::Nil JsonGet req => OkResponse()
    case "application" :: "insert" :: Nil JsonPost json -> request => OkResponse()
    case "application" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "application" :: "delete" :: Nil JsonPost json -> request => OkResponse()

  }

}
