package code.rest

/**
 * Created by phong on 7/10/2015.
 */

import java.util.UUID

import code.common.Message
import code.model._
import com.mongodb.{BasicDBObject, QueryBuilder}
import net.liftweb.common.Full
import net.liftweb.http.{S, OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.mongodb.{Limit, Skip}
import net.liftweb.util.Helpers._

object ModelInfoAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ModelInfoAPI)
  }

  def getModelInfoJSON(): JValue = {

    val DBList = ModelInfo.findAll
    if(DBList.isEmpty)
      return code.common.Message.returnMassage("getModelInfoJSON", "1", "ModelInfo not found", null)
    else
      return code.common.Message.returnMassage("getModelInfoJSON", "0", "SUCCESS", DBList.map(_.asJValue))

  }

  def getModelInfoByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = ModelInfo.findAll(qry)

    if(DBList.isEmpty)
      return code.common.Message.returnMassage("getModelInfoByIdJSON", "1", "ModelInfo not found", null)
    else
      return code.common.Message.returnMassage("getModelInfoByIdJSON", "0", "SUCCESS", DBList.map(_.asJValue))

  }

  def getModelInfoByStatusJSON(status : String): JValue = {

    val qry = QueryBuilder.start("status").is(status)
      .get

    val DBList = ModelInfo.findAll(qry)

    if(DBList.isEmpty)
      return code.common.Message.returnMassage("getModelInfoByStatusJSON", "1", "ModelInfo not found", null)
    else
      return code.common.Message.returnMassage("getModelInfoByStatusJSON", "0", "SUCCESS", DBList.map(_.asJValue))

  }

  def deleteModelInfo(_id : String): JValue = {
    val qryM = QueryBuilder.start("_id").is(_id)
      .get
    val DBM = ModelInfo.findAll(qryM)
    if(DBM == Nil)
      return code.common.Message.returnMassage("deleteModelInfo", "1", "Model not found", null)
    if(DBM(0).status.toString().equals("draft") || DBM(0).status.toString().equals("")) {
      val qry = QueryBuilder.start("ModelId").is(_id)
        .get
      val DBListCheck = Factor.findAll(qry)
      if (DBListCheck.size == 0) {
        ModelInfo.delete(("_id" -> _id))
        return code.common.Message.returnMassage("deleteModelInfo", "0", "SUCCESS", " DELETED ")
      } else
        return code.common.Message.returnMassage("deleteModelInfo", "1", " Factor is exixts ", null)
    }else
      return code.common.Message.returnMassage("deleteModelInfo", "1", " Model can't delete (not draft) ", null)
  }

  def insertModelInfo(q : JValue): JValue = {
    val mess = code.common.Message.CheckNullReturnMess(q, List("name"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values
      return code.common.Message.returnMassage("insertModelInfo", "0", "SUCCESS",
                                ModelInfo.createRecord.id(UUID.randomUUID().toString)
                                .name(json.apply("name").toString)
                                .description(if(json.exists(j => j._1.toString.equals("description"))) json.apply("description").toString.toLowerCase else "")
                                .status(if(json.exists(j => j._1.toString.equals("status"))) json.apply("status").toString.toLowerCase else "")
                                .save.asJValue)
    }else
      return code.common.Message.returnMassage("insertModelInfo", "1", mess, null)
  }

  def updateModelInfo(q : JValue): JValue = {
    val json = q.asInstanceOf[JObject].values
    val qryM = QueryBuilder.start("_id").is(json.apply("_id").toString)
      .get
    val DBM = ModelInfo.findAll(qryM)
    if(DBM.equals("publish")){
      return code.common.Message.returnMassage("updateModelInfo", "1", "Model can't update (was published)", null)
    }else {
      val mess = code.common.Message.CheckNullReturnMess(q, List("_id", "name", "description", "status"))
      if (mess.equals("OK")) {

        val qry = QueryBuilder.start("_id").is(json.apply("_id").toString)
          .get

        val update = ModelInfo.findAll(qry)

        return code.common.Message.returnMassage("updateModelInfo", "0", "SUCCESS",
                                                  update(0).update
                                                  .name(json.apply("name").toString)
                                                    .description(if(json.exists(j => j._1.toString.equals("description"))) json.apply("description").toString.toLowerCase else update(0).description.toString())
                                                    .status(if(json.exists(j => j._1.toString.equals("status"))) json.apply("status").toString.toLowerCase else update(0).status.toString())
                                                  .save.asJValue)

      } else
          return code.common.Message.returnMassage("updateModelInfo", "1", mess, null)
    }
  }

  def viewModelInfo(id : String): JValue = {

    val qry = QueryBuilder.start("ModelId").is(id).get

    var DBList = Factor.findAll(qry)

    if(DBList.isEmpty)
      return code.common.Message.returnMassage("viewModelInfo", "1", "ModelInfo not found", null)
    else
      return code.common.Message.returnMassage("viewModelInfo", "0", "SUCCESS",DBList.map(_.asJValue))

  }

  def range(id : String) : JValue = {

    val range = code.rest.FactorAPI.ScoringRange(id)

    val json = ("min" -> range(0).toString) ~ ("max" -> range(1).toString)

    return code.common.Message.returnMassage("range", "0", "SUCCESS",json)
  }

  def rangeAndUpdate(id : String) : JValue = {

    var range = code.rest.FactorAPI.ScoringRange(id)

    val qry = QueryBuilder.start("_id").is(id).get

    var DBList = ModelInfo.findAll(qry)

    return code.common.Message.returnMassage("rangeAndUpdate", "0", "SUCCESS",
      DBList(0).update.min(range(0).toDouble).max(range(1).toDouble).save.asJValue)
  }
  def getmodelfactor(q:String) :JValue = {

    val dbmodel = ModelInfo.findAll("_id" -> q)
    if(dbmodel.size == 1){

      val dbfactor = Factor.findAll(("ModelId" -> q) ~ ("Parentid" ->""))

     return Message.returnMassage("modelinfo","0","Success",("model" -> dbmodel(0).asJValue) ~ ("factor" -> dbfactor.map(_.asJValue)))


    }else if(dbmodel.size ==0){
      return  Message.returnMassage("modelinfo","0","No record found",("model" -> "") ~ ("factor" -> "[]"))
    }else{
      return  Message.returnMassage("modelinfo","1","Many model found",null)
    }


  }

  def search(q: List[String]): JValue ={

    println(q)
    var pageIndex :Int= 1
    var pageSize :Int= 5

//    val jsonMap: Map[String, String] = q.values.asInstanceOf[Map[String, String]]
//
//    for ((key, value) <- jsonMap) {
//      if(key.toString.equals("pageIndex")){
//        pageIndex = value.toString.toInt
//      }else if(key.toString.equals("pageIndex")){
//        pageSize = value.toString.toInt
//      }
//    }

//  val req =  S.request.toList
//    for(x<- req){
//      for(y<-x.paramNames){
//        if(y.toString.equals("pageIndex")){
//          pageIndex=S.param(y).toString.toInt
//        }else if(y.toString.equals("pageIndex")){
//          pageSize=S.param(y).toString.toInt
//        }
//      }
//    }

    for(req <- S.request.toList){
     for(paramName <- req.paramNames) {
       val Full(a) = S.param(paramName)
       if (paramName.toLowerCase.equals("pageindex")) {
         pageIndex = a.toString.toInt
       }else if(paramName.toLowerCase.equals("pagesize")){
         pageSize = a.toString.toInt
       }
     }
    }

//   val  a = for{
//      req <- S.request.toList
//      paramName <- req.paramNames
//      if(paramName.toString.equals("pageIndex"))
//        value <- S.param(paramName)
//    } yield value
//
//    val  b = for{
//      req <- S.request.toList
//      paramName <- req.paramNames
//      if(paramName.toString.equals("pageSize"))
//      value <- S.param(paramName)
//    } yield value
//    if(a.size == 1){
//      pageIndex = a(0).toInt
//    }
//    if(b.size ==1){
//      pageSize = b(0).toInt
//    }

    val qry = QueryBuilder.start().get()
    val db = ModelInfo.findAll(qry,new BasicDBObject("_id",-1),Skip(pageSize*(pageIndex-1)), Limit(pageSize))
    val count = ModelInfo.count(qry)

   Message.returnMassage("modelinfo","0","Success",db.map(_.asJValue),count)
  }

  serve {

    case "modelinfo" :: "search":: q JsonGet req => search(q)

    case "modelinfo" :: "factor":: q  :: Nil JsonGet req => getmodelfactor(q) : JValue
    case "modelinfo" :: "getall"  :: Nil JsonGet req => getModelInfoJSON() : JValue

    case "modelinfo" :: "getbymodelinfoid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "getbymodelinfoid" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "_id").toOpt} yield getModelInfoByIdJSON(id) : JValue

    case "modelinfo" :: "getbymodelinfostatus" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "getbymodelinfostatus" :: Nil JsonPost json -> request =>
      for{JString(status) <- (json \\ "status").toOpt} yield getModelInfoByStatusJSON(status) : JValue

//    case "modelinfo" :: "getbymodelinfoid" ::q:: Nil JsonGet req => getModelInfoByIdJSON(q) : JValue

    case "modelinfo" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "update" :: Nil JsonPost json -> request => updateModelInfo(json)

    case "modelinfo" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "_id").toOpt} yield deleteModelInfo(id)

    //    case "modelinfo" :: "delete" :: id :: Nil JsonDelete req => deleteModelInfo(id)

    case "modelinfo" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "insert" :: Nil JsonPost json -> request => insertModelInfo(json)

    case "modelinfo" :: "view" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "view" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "_id").toOpt} yield viewModelInfo(id)

    case "modelinfo" :: "range" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "range" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "_id").toOpt} yield range(id)

    case "modelinfo" :: "rangeandupdate" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "rangeandupdate" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "_id").toOpt} yield rangeAndUpdate(id)
  }

}
