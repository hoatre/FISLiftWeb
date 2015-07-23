package code.rest

import code.rest.ValidateAPI._
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.JValue

import java.util.UUID

import code.common.Message
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json._
import net.liftweb.json.JsonAST.JArray
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonAST.JValue

import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.http.{S, LiftRules}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model._
import net.liftweb.json.Printer._
import net.liftweb.mongodb.JObjectParser
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue

import scala.collection.immutable.HashMap

/**
 * Created by bacnv on 7/23/15.
 */
object csvAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(csvAPI)

    //    LiftRules.dispatch.append(MyRest) // stateful — associated with a servlet container session
    //    LiftRules.statelessDispatchTable.append(MyRest) // stateless — no session created
  }


  serve {

    case "csv" :: "upload" :: Nil Options _ => {
      "OK" -> "200"
    }: JValue
    case "csv" :: "upload" :: Nil JsonPost json -> request => checkweightrate(json)

    //    case "validate" :: "checkweightrate" :: Nil JsonDelete json  => test(json)

    case "csv" :: "test"::q:: Nil JsonGet req => CsvModule.writetoCSV(q)

    case "csv" :: "read"::q:: Nil JsonGet req => CsvModule.readCSV(q)
  }
}