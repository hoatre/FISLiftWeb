package code.rest.oauth2

import code.model.oauth2.Applications
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{OkResponse, LiftRules}
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
import net.liftweb.http.S

/**
 * Created by bacnv on 13/08/2015.
 */
object ApplicationAPI extends  RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ApplicationAPI)
  }

  serve{
    case "application" ::"search":: Nil  Options _ => OkResponse()
    case "application" ::"insert":: Nil Options _ => OkResponse()
    case "application" ::"update":: Nil Options _ => OkResponse()
    case "application" ::"delete":: Nil Options _ => OkResponse()
    case "application" :: "search" :: q Post req => AppSnip.searh(q)
    case "application" :: "insert" :: Nil JsonPost json -> request => AppSnip.insert(json)
    case "application" :: "update" :: Nil JsonPost json -> request => AppSnip.update(json)
    case "application" :: "delete" :: q :: Nil JsonDelete req => AppSnip.delete(q)
    case "application" :: "getbyid" :: q :: Nil JsonGet req => test(q)

  }
  def test(q:String) : JValue ={
    val db =Applications.findAll("_id" -> q.toString)
println(db.size)
    db.map(_.asJValue)
  }

}
