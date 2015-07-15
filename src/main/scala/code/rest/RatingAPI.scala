package code.rest

import java.util.UUID

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
import code.model.{codeIN, Rating, Users}
import net.liftweb.json.Printer._
import net.liftweb.mongodb.JObjectParser
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue

import scala.collection.immutable.HashMap

/**
 * Created by bacnv on 7/14/15.
 */
object RatingAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(RatingAPI)

    //    LiftRules.dispatch.append(MyRest) // stateful — associated with a servlet container session
    //    LiftRules.statelessDispatchTable.append(MyRest) // stateless — no session created
  }


    serve {



      case "rating" :: "insert" :: Nil JsonPost json-> request => insert(json)
      case "rating" :: "update" :: Nil JsonPost json-> request => updates(json)
      case "rating" :: "delete" :: Nil JsonPost json-> request => delete(json)
      case "rating" :: "getmodelid" :: q :: Nil JsonGet req => getbymodelid(q)

      case "rating" :: "getcode" :: q :: p:: Nil JsonGet req => getbycodelid(q,p)
      case "rating" :: "getall" :: Nil JsonGet req => getall

      case "rating" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
      case "rating" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
      case "rating" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue




    }

  def getbymodelid(q:String): JValue={
    val qry = QueryBuilder.start("modelid").is(q).get

    val db = Rating.findAll(qry)

    var msg : JValue= {"ERROR" -> "Not existed"} :JValue

    if(db.size>0) {

    msg = { "SUCCESS" -> db.map(_.asJValue): JValue}:JValue

    }

    msg
  }

  def getbycodelid(q:String,p:String): JValue={
    val qry = QueryBuilder.start("modelid").is(q).put("codein").elemMatch(new BasicDBObject("code", p)).get

    val db = Rating.findAll(qry)

    var msg : JValue= {"ERROR" -> "Not existed"} :JValue

    if(db.size>0) {

      val db1 = db(0).asJValue
      val json = (db1\"codein")

      val JArray(rates) = json
      rates collect { case rate: JObject => rate } foreach myOperation
      def myOperation(rate: JObject) = {
        val j = rate.asInstanceOf[JObject].values

        val item = codeIN.code(j.apply("code").toString).status(j.apply("status").toString)
          .statusname(j.apply("statusname").toString).scorefrom(j.apply("scorefrom").toString.toDouble).scoreto(j.apply("scoreto").toString.toDouble)

//        var listb: List[codeIN] = List(item)
//
//        lista = lista ::: listb
        val db2 = Rating.modelid(db1.values.apply("modelid").toString).modelname(Option(db1.values.apply("modelname").toString).getOrElse(""))
        .codein(List(item))

        msg = { "SUCCESS" -> db2.asJValue }:JValue
      }




    }

    msg
  }
  def getall :JValue={
    val db = Rating.findAll

    var msg : JValue= {"ERROR" -> "Not existed"} :JValue

    if(db.size>0) {

      msg = { "SUCCESS" -> db.map(_.asJValue): JValue}:JValue

    }

    msg

  }

  def insert(q: JValue): JValue = {



    var json = q.asInstanceOf[JObject]



    val modelid = json.values.apply("modelid").toString()
//    val rate = json.values.apply("rate").toString.toList
//    val a1 = codeIN.createRecord.code(json.values.apply("rate").apply("code").toString)
//
//    val a2 = codeIN.createRecord.code(json.values.apply("code").toString)

//    val b :List(Code)
//    println(rate)
val qry = QueryBuilder.start("modelid").is(modelid).get

    val DBquery = Rating.findAll(qry)
    var c : JValue = {"ERROR" -> "Check again"} :JValue

    if(DBquery.size ==0){
    var i = 0
    var lista : List[codeIN] =List()
    val list = (json \ "codein")


if (list.isInstanceOf[JArray]) {
  val JArray(rates) = list
  rates collect { case rate: JObject => rate } foreach myOperation
  def myOperation(rate: JObject) = {
    val j = rate.asInstanceOf[JObject].values

    val item = codeIN.createRecord.code(j.apply("code").toString).status(j.apply("status").toString)
      .statusname(j.apply("statusname").toString).scorefrom(j.apply("scorefrom").toString.toDouble).scoreto(j.apply("scoreto").toString.toDouble)

    var listb: List[codeIN] = List(item)

    lista = lista ::: listb
  }

}else{
  val j = list.children
  for{
    JString(code) <- (j \\ "code").toOpt
    JString(status) <- (j \\ "status").toOpt
    JString(statusname) <- (j \\ "statusname").toOpt
    JString(scorefrom) <- (j \\ "scorefrom").toOpt
    JString(scoreto) <- (j \\ "scoreto").toOpt

    item = codeIN.createRecord.code(code.toString).status(status.toString).statusname(statusname.toString).scorefrom(scorefrom.toString.toDouble).scoreto(scoreto.toString.toDouble)


  }yield {

    var listb: List[codeIN] = List(item)

    lista = lista ::: listb
  }
//  val item = codeIN.createRecord.code(j.apply("code").toString).status(j.apply("status").toString)
}



      val d = Rating.createRecord.id(UUID.randomUUID().toString()).modelid(modelid).codein(lista).save

      c = d.asJValue
    }else{
     c= updates(q)

    }


    c
  }

  def updates(q:JValue): JValue = {
    var json = (q\"codein").asInstanceOf[JObject]
    var msg : JValue ={"ERROR"->"Not found"} :JValue

    //    var listFactorOption : List[codeIN] = List()
    var listi  : List[codeIN] = List()
    var listu  : List[codeIN] = List()

    val qry = QueryBuilder.start("modelid").is(q.asInstanceOf[JObject].values.apply("modelid")).get

    var dbFind = Rating.findAll(qry)

//    val code = for { JField("code", JString(code)) <- q } yield code

    //    val qry1 = QueryBuilder.start("_id").is(json.values.apply("_id")).put("rate.code").is(code).get

    //    dbFind = Rating.findAll("$where" -> "function() { return this.rate.status=='AAA'}")



    val factorOption = codeIN.code(json.values.apply("code").toString).status(json.values.apply("status").toString)
      .statusname(json.values.apply("statusname").toString).scorefrom(json.values.apply("scorefrom").toString.toDouble)
      .scoreto(json.values.apply("scoreto").toString.toDouble)



    //    listFactorOption =  dbFind(0).codein.value
    if(dbFind.size > 0) {

      val JArray(rates) = (dbFind.map(_.asJValue) \ "codein")
      rates collect { case rate: JObject => rate } foreach getlist

      def getlist(rate: JObject) = {
        var listb: List[codeIN] = List()
        val j = rate.asInstanceOf[JObject].values
        if (j.apply("code").toString.equals(json.values.apply("code").toString)) {

        } else {
          val item = codeIN.createRecord.code(j.apply("code").toString).status(j.apply("status").toString)
            .statusname(j.apply("statusname").toString).scorefrom(j.apply("scorefrom").toString.toDouble)
            .scoreto(j.apply("scoreto").toString.toDouble)
          listb = List(item)
          listi = listi ::: listb
        }


      }


      val updateFactor = dbFind(0).update.codein(listi ::: listu ::: List(factorOption)).save

      msg = {"SUCCESS" -> dbFind.map(_.asJValue)} :JValue

    }else{
      msg = {"ERROR" -> "Cannot find model"} :JValue
    }

    msg
  }

  def update(q:JValue): JValue = {
    var json = q.asInstanceOf[JObject]
    var msg : JValue ={"ERROR"->"Not found"} :JValue

//    var listFactorOption : List[codeIN] = List()
    var listi  : List[codeIN] = List()
    var listu  : List[codeIN] = List()

    val qry = QueryBuilder.start("modelid").is(json.values.apply("modelid")).get

    var dbFind = Rating.findAll(qry)

   val code = for { JField("code", JString(code)) <- q } yield code

//    val qry1 = QueryBuilder.start("_id").is(json.values.apply("_id")).put("rate.code").is(code).get

//    dbFind = Rating.findAll("$where" -> "function() { return this.rate.status=='AAA'}")



    val factorOption = codeIN.code(json.values.apply("code").toString).status(json.values.apply("status").toString)
      .statusname(json.values.apply("statusname").toString).scorefrom(json.values.apply("scorefrom").toString.toDouble)
      .scoreto(json.values.apply("scoreto").toString.toDouble)



//    listFactorOption =  dbFind(0).codein.value
    if(dbFind.size > 0) {

      val JArray(rates) = (dbFind.map(_.asJValue) \ "codein")
      rates collect { case rate: JObject => rate } foreach getlist

      def getlist(rate: JObject) = {
        var listb: List[codeIN] = List()
        val j = rate.asInstanceOf[JObject].values
        if (j.apply("code").toString.equals(json.values.apply("code").toString)) {

        } else {
          val item = codeIN.createRecord.code(j.apply("code").toString).status(j.apply("status").toString)
            .statusname(j.apply("statusname").toString).scorefrom(j.apply("scorefrom").toString.toDouble)
            .scoreto(j.apply("scoreto").toString.toDouble)
          listb = List(item)
          listi = listi ::: listb
        }


      }


      val updateFactor = dbFind(0).update.codein(listi ::: listu ::: List(factorOption)).save

     msg = {"SUCCESS" -> dbFind.map(_.asJValue)} :JValue

    }else{
      msg = {"ERROR" -> "Cannot find model"} :JValue
    }

    msg
  }



  def delete(q:JValue): JValue = {
    var json = q.asInstanceOf[JObject]

//    var listFactorOption : List[codeIN] = List()
    var listi  : List[codeIN] = List()
    var listu  : List[codeIN] = List()

    val qry = QueryBuilder.start("modelid").is(json.values.apply("modelid")).get

    var dbFind = Rating.findAll(qry)

//    val code = for { JField("code", JString(code)) <- q } yield code

    //    val qry1 = QueryBuilder.start("_id").is(json.values.apply("_id")).put("rate.code").is(code).get

    //    dbFind = Rating.findAll("$where" -> "function() { return this.rate.status=='AAA'}")







//    listFactorOption =  dbFind(0).codein.value

    val JArray(rates) =   (dbFind.map(_.asJValue) \ "codein")
    rates collect { case rate: JObject => rate } foreach getlist

    def getlist(rate: JObject) = {
      var listb: List[codeIN] = List()
      val j = rate.asInstanceOf[JObject].values
      if(!j.apply("code").toString.equals(json.values.apply("code").toString)){
        val item = codeIN.createRecord.code(j.apply("code").toString).status(j.apply("status").toString)
          .statusname(j.apply("statusname").toString).scorefrom(j.apply("scorefrom").toString.toDouble)
          .scoreto(j.apply("scoreto").toString.toDouble)
        listb = List(item)
        listi = listi ::: listb
      }




    }


    val updateFactor = dbFind(0).update.codein(listi ::: listu).save



    {"SUCCESS" -> dbFind.map(_.asJValue)} :JValue
  }
}
