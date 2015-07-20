package code.rest

/**
 * Created by bacnv on 7/17/15.
 */
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
 * Created by bacnv on 7/14/15.
 */
object ValidateAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ValidateAPI)

    //    LiftRules.dispatch.append(MyRest) // stateful — associated with a servlet container session
    //    LiftRules.statelessDispatchTable.append(MyRest) // stateless — no session created
  }


  serve {

    case "validate" :: "checkweightrate" :: Nil Options _ => {
      "OK" -> "200"
    }: JValue
    case "validate" :: "checkweightrate" :: Nil JsonPost json -> request => checkweightrate(json)

//    case "validate" :: "checkweightrate" :: Nil JsonDelete json  => test(json)


  }

  def  test(q:JValue): JValue ={
    {"" -> ""} :JValue

  }

  def checkweightrate(q: JValue): JValue = {
    val json = q.asInstanceOf[JObject]
    var msg : List[JValue] = List()

    var j = 0
    var sum: Double = 0
    var factorid = ""
    var factorname = ""
    var mapAllFactor: List[JValue] = List()
    var mapAllRating: JValue = null


    val modelid = json.values.apply("modelid").toString

    val dbFind = Factor.findAll(QueryBuilder.start("ModelId").is(modelid).get)
    val dbRates = Rating.findAll(QueryBuilder.start("modelid").is(modelid).get)
    val dbModel = ModelInfo.findAll(QueryBuilder.start("_id").is(modelid).get)

    if(dbFind.size == 0 && dbRates.size == 0) {
      return  msgcheckweightrate(mapAllFactor,null,0)
    }

    var lista: List[String] = List()

    var listname: Map[String, String] = Map()

    var i = 0
    for (i <- 0 to dbFind.size - 1) {

      for {
        JString(parent) <- (dbFind(i).asJValue \ "Parentid").toOpt
        // JString(weight) <- (dbFind(i).asJValue \ "Weight").toOpt
        item = parent.toString
      } yield {
        if (!lista.contains(item)) {
          val listb: List[String] = List(item)

          lista = lista ::: listb
        }
      }

    }

    for (j <- 0 to lista.size - 1) {
      for (i <- 0 to dbFind.size - 1) {
        val db = dbFind(i)

        if (lista(j).equals(db.Parentid.toString())) {
          sum = sum + db.Weight.toString().toDouble
          //          println(db.Parentid.toString() +"    "+ lista(j) + "   " + sum)
        }
        if (lista(j).equals(db.id.toString())) {
          factorid = lista(j)
          factorname = db.FactorName.toString()

        }
        if (i == dbFind.size - 1) {
          if (sum != 100) {
            mapAllFactor = mapAllFactor ::: List({
              ("_id" -> factorid) ~ ("FactorName" -> factorname) ~ ("Weight" -> sum)
            }: JValue)
            //          mapAllFactor = mapAllFactor ::: List(a.values.)
          }
          sum = 0
        }

      }

    }

    val min = dbModel(0).min.toString().toDouble
    val max = dbModel(0).max.toString().toDouble

    //     val codein = List(dbRates(0).codein).so

    var listrate: List[codeIN] = List()

    if (dbRates.size > 0) {
      if ((dbRates.map(_.asJValue) \ "codein").isInstanceOf[JArray]) {
        val JArray(rates) = (dbRates.map(_.asJValue) \ "codein").asInstanceOf[JArray]
        rates collect { case rate: JObject => rate } foreach myOperation
        def myOperation(rate: JObject) = {


          val j = rate.asInstanceOf[JObject].values

          val item = codeIN.createRecord.code(j.apply("code").toString).status(j.apply("status").toString)
            .statusname(j.apply("statusname").toString).scorefrom(j.apply("scorefrom").toString.toDouble).scoreto(j.apply("scoreto").toString.toDouble)

          //          var listb: List[codeIN] = List(item)

          listrate = listrate ::: List(item)
        }



        //          println(listrate)

//        println(listrate.sortWith(_.scorefrom.toString().toDouble < _.scoreto.toString().toDouble))
        val listratesort = listrate.sortWith(_.scorefrom.toString().toDouble < _.scoreto.toString().toDouble)

        var x = 0
        for (x <- 0 to listratesort.size - 1) {

          val scoreform = listratesort(x).scorefrom.toString().toDouble
          val scoreto = listratesort(x).scoreto.toString().toDouble

          if (scoreform > scoreto
            || (x < listratesort.size - 1 && scoreto != listratesort(x + 1).scorefrom.toString().toDouble)
            || listratesort(0).scorefrom.toString().toDouble < min
            || (x == listratesort.size - 1 && listratesort(x).scoreto.toString().toDouble != max)) {

            //            return   {
            //              ("ERROR" -> "fuck")
            //            }: JValue

            //            println(min +"    "+ max)

            //            mapAllRating = {"RATEERROR" -> listratesort(x).code.toString() }: JValue


            return  msgcheckweightrate(mapAllFactor,listratesort(x).code.toString(),1)

          }
        }


      }

    }else{
      return  msgcheckweightrate(mapAllFactor,"Not found",1)

    }




    msgcheckweightrate(mapAllFactor,null,1)
  }
  def msgcheckweightrate(s:List[JValue],h:String,v:Int) :JValue={
    var msg :JValue = {"SUCCESS" -> "OK"} :JValue
    var check =  0
    var checkmsg : String = "No error"
    if(s.size>0 && h != null && !h.isEmpty ){
      msg =  {("weight" -> s) ~ ("rate" -> "Rating having a problem") ~ ("code" -> h)} :JValue
      check = 1
      checkmsg = "Weight and Rate have problems"
    }
    else  if(s.size>0){
      msg =  {("weight" -> s) ~ ("rate" -> "") ~ ("code" -> "")} :JValue
      check = 2
      checkmsg = "Weight has a problem"
    }
    else if(h != null && !h.isEmpty){
      msg =  {("weight" -> "") ~("rate" -> "Rating having a problem") ~ ("code" -> h)} :JValue
      check = 3
      checkmsg = "Rate has a problem"
    }else if(v == 0){
      msg =  {("weight" -> "") ~ ("rate" -> "") ~ ("code" -> "")} :JValue
      check = 4
      checkmsg = "Model can not active"
    }

//    {"checkweightrate" -> (("header" ->(("code"->check.toString)~("message" -> checkmsg)))~("body" -> msg))} :JValue


    Message.returnMassage("checkweightrate",check.toString,checkmsg,msg)
  }

}
