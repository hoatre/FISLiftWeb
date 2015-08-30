package code.rest.oauth2

import code.model.oauth2.{Functions, Group}
import net.liftweb.common.Full
import net.liftweb.http.{JsonResponse, S, OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object GroupAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupAPI)
    Functions.insertBoot("/group/search")
    Functions.insertBoot("/group/insert")
    Functions.insertBoot("/group/update")
    Functions.insertBoot("/group/delete")
    Functions.insertBoot("/group/id")
  }

  serve{
    case "group" ::"search":: Nil  Options _ => OkResponse()
    case "group" ::"insert":: Nil Options _ => OkResponse()
    case "group" ::"update":: Nil Options _ => OkResponse()
    case "group" ::"delete":: q :: Nil Options _ => OkResponse()
    case "group" :: "id" :: q :: Nil Options _ => OkResponse()

    case "group" :: "id" :: q :: Nil JsonGet req => {
      S.respondAsync {
        val s = Group.getbyid(q)
        Full(JsonResponse(s))
      }
    }
    case "group" :: "search" :: q Post req => Group.searh(q)
    case "group" :: "insert" :: Nil JsonPost json -> request => Group.insert(json)
    case "group" :: "update" :: Nil JsonPost json -> request => Group.update(json)
    case "group" :: "delete" :: q :: Nil JsonDelete req => Group.delete(q)

  }

}
