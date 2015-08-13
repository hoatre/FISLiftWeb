package code.rest.oauth2

import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object FunctionAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(FunctionAPI)
  }

  serve{
    case "function" :: Nil Options _ => OkResponse()
    case "function" :: "getall" :: Nil JsonGet req => OkResponse()

    case "function" :: "getbyid" :: q ::Nil JsonGet req => OkResponse()
    case "function" :: "insert" :: Nil JsonPost json -> request => OkResponse()
    case "function" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "function" :: "delete" :: Nil JsonPost json -> request => OkResponse()

  }

}
