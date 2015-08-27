package code.rest.oauth2

import code.model.oauth2.Functions
import net.liftweb.common.Full
import net.liftweb.http.{JsonResponse, S, OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper

/**
 * Created by bacnv on 13/08/2015.
 */
object FunctionAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(FunctionAPI)
  }

  serve{
    case "function" ::"search":: Nil  Options _ => OkResponse()
    case "function" ::"insert":: Nil Options _ => OkResponse()
    case "function" ::"update":: Nil Options _ => OkResponse()
    case "function" ::"delete":: Nil Options _ => OkResponse()
    case "function" :: "id" :: q :: Nil Options _ => OkResponse()

    case "function" :: "id" :: q :: Nil JsonGet req => {
      S.respondAsync {
        val s = Functions.getbyid(q)
        Full(JsonResponse(s))
      }
    }
    case "function" :: "search" :: q Post req => Functions.searh(q)
    case "function" :: "insert" :: Nil JsonPost json -> request => Functions.insert(json)
    case "function" :: "update" :: Nil JsonPost json -> request => Functions.update(json)
    case "function" :: "delete" :: q :: Nil JsonDelete req => Functions.delete(q)

  }

}
