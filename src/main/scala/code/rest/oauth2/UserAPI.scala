package code.rest.oauth2

import code.common.Message
import code.rest.oauth2.GroupAPI._
import net.liftweb.common.CombinableBox.Result
import net.liftweb.common.Full
import net.liftweb.http._
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.JValue
import code.model.oauth2._

import scalaoauth2.provider.{ProtectedResource, AuthInfo, DataHandler}

/**
 * Created by bacnv on 13/08/2015.
 */
object UserAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(UserAPI)
    APIs.insertBoot("/user/search")
    APIs.insertBoot("/user/insert")
    APIs.insertBoot("/user/update")
    APIs.insertBoot("/user/delete")
    APIs.insertBoot("/user/id")
  }

  serve{
    case "user" ::"search":: Nil  Options _ => OkResponse()
    case "user" ::"insert":: Nil Options _ => OkResponse()
    case "user" ::"update":: Nil Options _ => OkResponse()
    case "user" ::"delete":: q ::  Nil Options _ => OkResponse()
    case "user" :: "id" :: q :: Nil Options _ => OkResponse()

    case "user" :: "id" :: q :: Nil JsonGet req => {
      S.respondAsync {
        val s = User.getbyid(q)
        Full(JsonResponse(s))
      }
    }
    case "user" :: "search" :: q Post req => {
      S.respondAsync {
        val s = User.searh(q)
        Full(JsonResponse(s))
      }
    }
    case "user" :: "insert" :: Nil JsonPost json -> request => {
      S.respondAsync {
        val s = User.insert(json)
        Full(JsonResponse(s))
      }
    }
    case "user" :: "update" :: Nil JsonPost json -> request => {
      S.respondAsync {
        val s = User.update(json)
        Full(JsonResponse(s))
      }
    }
    case "user" :: "delete" :: q :: Nil JsonDelete req => {
      S.respondAsync {
        val s = User.delete(q)
        Full(JsonResponse(s))
      }
    }

  }



}
