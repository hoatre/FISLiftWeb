package code.rest

/**
 * Created by phong on 7/10/2015.
 */

import java.util.UUID

import code.model._
import com.mongodb.QueryBuilder
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._

object ModelInfoAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(ModelInfoAPI)
  }

  def getModelInfoJSON(): JValue = {

    val DBList = ModelInfo.findAll
    if(DBList.isEmpty)
      "ERROR" -> "ModelInfo not found" : JValue
    else
      {"ModelInfosList" -> DBList.map(_.asJValue)} : JValue

  }

  def getModelInfoByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = ModelInfo.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "ModelInfo not found" :JValue
    else
      {"ModelInfosList" -> DBList.map(_.asJValue)} : JValue

  }

  def getModelInfoByStatusJSON(status : String): JValue = {

    val qry = QueryBuilder.start("status").is(status)
      .get

    val DBList = ModelInfo.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "ModelInfo not found" :JValue
    else
      {"SUCCESS" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteModelInfo(_id : String): JValue = {
    val qryM = QueryBuilder.start("_id").is(_id)
      .get
    val DBM = ModelInfo.findAll(qryM)
    if(DBM(0).status.equals("draft") || DBM(0).status.equals("")) {
      val qry = QueryBuilder.start("ModelId").is(_id)
        .get
      val DBListCheck = Factor.findAll(qry)
      if (DBListCheck.size == 0) {
        ModelInfo.delete(("_id" -> _id))

        {
          "SUCCESS" -> " DELETED "
        }: JValue
      } else {
        "ERROR" -> " Factor is exixts "
      }: JValue
    }
    {
      "ERROR" -> " Model can't delete (not draft) "
    }: JValue
  }

  def insertModelInfo(q : JValue): JValue = {
    val mess = code.common.Message.CheckNullReturnMess(q, List("name", "description", "status"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values

      {
        "SUCCESS" -> ModelInfo.createRecord.id(UUID.randomUUID().toString)
          .name(json.apply("name").toString)
          .description(json.apply("description").toString)
          .status(json.apply("status").toString)
          .save.asJValue
      }: JValue
    }else
      return {"ERROR" -> mess} :JValue
  }

  def updateModelInfo(q : JValue): JValue = {
    val json = q.asInstanceOf[JObject].values
    val qryM = QueryBuilder.start("_id").is(json.apply("id").toString)
      .get
    val DBM = ModelInfo.findAll(qryM)
    if(DBM.equals("publish")){
      {
        "ERROR" -> "Model can't update (was published)"
      }: JValue
    }else {
      val mess = code.common.Message.CheckNullReturnMess(q, List("id", "name", "description", "status"))
      if (mess.equals("OK")) {


        val qry = QueryBuilder.start("_id").is(json.apply("id").toString)
          .get

        var update = ModelInfo.findAll(qry)

        {
          "SUCCESS" -> update(0).update
            .name(json.apply("name").toString)
            .description(json.apply("description").toString)
            .status(json.apply("status").toString)
            .save.asJValue
        }: JValue
      } else
        return {
          "ERROR" -> mess
        }: JValue
    }
  }

  def viewModelInfo(id : String): JValue = {

    val qry = QueryBuilder.start("ModelId").is(id).get

    var DBList = Factor.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "ModelInfo not found" : JValue
    else
      {
        "SUCCESS" -> DBList.map(_.asJValue)
      } : JValue

  }

  def range(id : String) : JValue = {

    val range = code.rest.FactorAPI.ScoringRange(id)

    val json = ("min" -> range(0).toString) ~ ("max" -> range(1).toString)

    { "SUCCESS" -> json } : JValue
  }

  def rangeAndUpdate(id : String) : JValue = {

    var range = code.rest.FactorAPI.ScoringRange(id)

    val qry = QueryBuilder.start("_id").is(id).get

    var DBList = ModelInfo.findAll(qry)

    { "SUCCESS" -> DBList(0).update.min(range(0).toDouble).max(range(1).toDouble).save.asJValue } : JValue
  }

  serve {
    case "modelinfo" :: "getall"  :: Nil JsonGet req => getModelInfoJSON() : JValue

    case "modelinfo" :: "getbymodelinfoid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "getbymodelinfoid" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getModelInfoByIdJSON(id) : JValue

    case "modelinfo" :: "getbymodelinfostatus" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "getbymodelinfostatus" :: Nil JsonPost json -> request =>
      for{JString(status) <- (json \\ "status").toOpt} yield getModelInfoByStatusJSON(status) : JValue

    case "modelinfo" :: "getbymodelinfoid" ::q:: Nil JsonGet req => getModelInfoByIdJSON(q) : JValue

    case "modelinfo" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "update" :: Nil JsonPost json -> request => updateModelInfo(json)

    case "modelinfo" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteModelInfo(id)

    //    case "modelinfo" :: "delete" :: id :: Nil JsonDelete req => deleteModelInfo(id)

    case "modelinfo" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "insert" :: Nil JsonPost json -> request => insertModelInfo(json)

    case "modelinfo" :: "view" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "view" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield viewModelInfo(id)

    case "modelinfo" :: "range" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "range" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield range(id)

    case "modelinfo" :: "rangeandupdate" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "rangeandupdate" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield rangeAndUpdate(id)
  }

}
