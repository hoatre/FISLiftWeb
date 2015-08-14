package code.rest.oauth2

import code.model.oauth2.UserGroup
import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 8/14/15.
 */
object UserGroupAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(UserGroupAPI)
  }

  serve{
    case "usergroup" ::"search":: Nil  Options _ => OkResponse()
    case "usergroup" ::"insert":: Nil Options _ => OkResponse()
    case "usergroup" ::"update":: Nil Options _ => OkResponse()
    case "usergroup" ::"delete":: Nil Options _ => OkResponse()
    case "usergroup" :: "search" :: q Post req => UserGroup.searh(q)
    case "usergroup" :: "insert" :: Nil JsonPost json -> request => UserGroup.insert(json)
    case "usergroup" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "usergroup" :: "delete" :: q :: Nil JsonDelete req => UserGroup.delete(q)


  }

}
