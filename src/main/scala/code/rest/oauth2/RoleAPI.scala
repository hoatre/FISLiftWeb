package code.rest.oauth2


import code.model.oauth2.{Functions, Role}
import net.liftweb.common.Full
import net.liftweb.http.{JsonResponse, S, OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object RoleAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(RoleAPI)
    Functions.insertBoot("/role/search")
    Functions.insertBoot("/role/insert")
    Functions.insertBoot("/role/update")
    Functions.insertBoot("/role/delete")
    Functions.insertBoot("/role/id")
  }

  serve{
    case "role" ::"search":: Nil  Options _ => OkResponse()
    case "role" ::"insert":: Nil Options _ => OkResponse()
    case "role" ::"update":: Nil Options _ => OkResponse()
    case "role" ::"delete":: Nil Options _ => OkResponse()
    case "role" :: "id" :: q :: Nil Options _ => OkResponse()

    case "role" :: "id" :: q :: Nil JsonGet req => {
      S.respondAsync {
        val s = Role.getbyid(q)
        Full(JsonResponse(s))
      }
    }
    case "role" :: "search" :: q Post req => Role.searh(q)
    case "role" :: "insert" :: Nil JsonPost json -> request => Role.insert(json)
    case "role" :: "update" :: Nil JsonPost json -> request => Role.update(json)
    case "role" :: "delete" :: q :: Nil JsonDelete req => Role.delete(q)

  }

}