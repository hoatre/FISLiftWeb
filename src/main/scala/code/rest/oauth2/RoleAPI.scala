package code.rest.oauth2


import code.model.oauth2.Role
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
    case "role" ::"search":: Nil  Options _ => OkResponse()
    case "role" ::"insert":: Nil Options _ => OkResponse()
    case "role" ::"update":: Nil Options _ => OkResponse()
    case "role" ::"delete":: Nil Options _ => OkResponse()
    case "role" :: "search" :: q Post req => Role.searh(q)
    case "role" :: "insert" :: Nil JsonPost json -> request => Role.insert(json)
    case "role" :: "update" :: Nil JsonPost json -> request => Role.update(json)
    case "role" :: "delete" :: q :: Nil JsonDelete req => Role.delete(q)

  }

}