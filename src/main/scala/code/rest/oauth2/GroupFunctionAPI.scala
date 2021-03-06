package code.rest.oauth2

import code.model.oauth2.{APIs, Functions, GroupFunction, RoleGroup}
import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 8/14/15.
 */
object GroupFunctionAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupFunctionAPI)
    APIs.insertBoot("/groupfunction/search")
    APIs.insertBoot("/groupfunction/insert")
    APIs.insertBoot("/groupfunction/update")
    APIs.insertBoot("/groupfunction/delete")
  }

  serve{
    case "groupfunction" ::"search":: Nil  Options _ => OkResponse()
    case "groupfunction" ::"insert":: Nil Options _ => OkResponse()
    case "groupfunction" ::"update":: Nil Options _ => OkResponse()
    case "groupfunction" ::"delete":: q :: Nil Options _ => OkResponse()
    case "groupfunction" :: "search" :: q Post req => GroupFunction.searh(q)
    case "groupfunction" :: "insert" :: Nil JsonPost json -> request => GroupFunction.insert(json)
    case "groupfunction" :: "update" :: Nil JsonPost json -> request => OkResponse()
    case "groupfunction" :: "delete" :: q :: Nil JsonDelete req => GroupFunction.delete(q)


  }

}
