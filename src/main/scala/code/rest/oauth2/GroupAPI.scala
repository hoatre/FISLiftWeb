package code.rest.oauth2

import code.model.oauth2.Group
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
    case "group" ::"search":: Nil  Options _ => OkResponse()
    case "group" ::"insert":: Nil Options _ => OkResponse()
    case "group" ::"update":: Nil Options _ => OkResponse()
    case "group" ::"delete":: Nil Options _ => OkResponse()
    case "group" :: "search" :: q Post req => Group.searh(q)
    case "group" :: "insert" :: Nil JsonPost json -> request => Group.insert(json)
    case "group" :: "update" :: Nil JsonPost json -> request => Group.update(json)
    case "group" :: "delete" :: q :: Nil JsonDelete req => Group.delete(q)

  }

}
