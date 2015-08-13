package code.rest.oauth2



import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object RoleAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(RoleAPI)
  }

  serve{
    case "role" :: Nil Options _ => OkResponse()
    case "role" :: "getall" :: Nil JsonGet req => OkResponse()
    case "role" :: "getbyid" :: q ::Nil JsonGet req => OkResponse()
    case "role" :: "insert" :: Nil JsonPost json -> request => OkResponse()
    case "role" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "role" :: "delete" :: Nil JsonPost json -> request => OkResponse()

  }

}