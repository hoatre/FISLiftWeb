package code.rest.oauth2

import code.model.oauth2.Applications
import code.model.oauth2.Functions
import net.liftweb.common.Full
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{JsonResponse, OkResponse, LiftRules, S}
import net.liftweb.json.JsonAST.JValue
import code.snippet.{Applications => AppSnip}
import net.liftweb.mongodb.BsonDSL

import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model._
import net.liftweb.json.Printer._
import net.liftweb.mongodb.{Limit, Skip, JObjectParser}
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import net.liftweb.util.{Helpers, Props}
import org.bson.types.ObjectId

import scala.collection.immutable.HashMap
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Random
import net.liftweb.http.provider.HTTPRequest

/**
 * Created by bacnv on 13/08/2015.
 */
object ApplicationAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ApplicationAPI)

    Functions.insertBoot("/application/search")
    Functions.insertBoot("/application/insert")
    Functions.insertBoot("/application/update")
    Functions.insertBoot("/application/delete")
    Functions.insertBoot("/application/id")

  }

  serve {
    case "application" :: "search" :: Nil Options _ => OkResponse()
    case "application" :: "insert" :: Nil Options _ => OkResponse()
    case "application" :: "update" :: Nil Options _ => OkResponse()
    case "application" :: "delete":: q :: Nil Options _ => OkResponse()
    case "application" :: "id" :: q :: Nil Options _ => OkResponse()

    case "application" :: "id" :: q :: Nil JsonGet req => {
      S.respondAsync {
        val s = AppSnip.getbyid(q)
        Full(JsonResponse(s))
      }
    }
    case "application" :: "search" :: q Post req => {
      S.respondAsync {
        val s = AppSnip.searh(q)
        Full(JsonResponse(s))
      }
    }
    case "application" :: "insert" :: Nil JsonPost json -> request => {
      S.respondAsync {
        val s = AppSnip.insert(json)
        Full(JsonResponse(s))
      }
    }
    case "application" :: "update" :: Nil JsonPost json -> request => {
      S.respondAsync {
        val s = AppSnip.update(json)
        Full(JsonResponse(s))
      }
    }
    case "application" :: "delete" :: q :: Nil JsonDelete req => {
      S.respondAsync {
        val s = AppSnip.delete(q)
        Full(JsonResponse(s))
      }
    }

  }
}
