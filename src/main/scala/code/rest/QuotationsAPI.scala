package code.rest


import bootstrap.liftweb.Boot
import code.model.DayOfWeek.DayOfWeek
import code.model.{DayOfWeek, Birthday}
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Extraction
import net.liftweb.json.DefaultFormats
import net.liftweb.mongodb.record.field.StringPk

/**
 * Created by phong on 7/6/2015.
 */
object QuotationsAPI extends RestHelper{
  new Boot().boot
  val rqDB = Birthday.findAll.toList(3)
  println(rqDB.id)
  case class Quote(_id: String, dow: String, dow2: String)
  val quote = Quote(rqDB.id.toString(),
    rqDB.dow.toString(),
    rqDB.dow2.toString())

  val json : JValue = Extraction decompose quote

  println(json)

  def init() : Unit = {
    LiftRules.statelessDispatch.append(QuotationsAPI)
  }

  serve {
    case "quotation" :: Nil JsonGet req => json : JValue
  }


}
