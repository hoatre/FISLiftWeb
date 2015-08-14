package code.rest.oauth2

import code.model.oauth2.RoleGroup
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
    case "rolegroup" ::"search":: Nil  Options _ => OkResponse()
    case "rolegroup" ::"insert":: Nil Options _ => OkResponse()
    case "rolegroup" ::"update":: Nil Options _ => OkResponse()
    case "rolegroup" ::"delete":: Nil Options _ => OkResponse()
    case "rolegroup" :: "search" :: q Post req => RoleGroup.searh(q)
    case "rolegroup" :: "insert" :: Nil JsonPost json -> request => RoleGroup.insert(json)
    case "rolegroup" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "rolegroup" :: "delete" :: q :: Nil JsonDelete req => RoleGroup.delete(q)


  }

}
