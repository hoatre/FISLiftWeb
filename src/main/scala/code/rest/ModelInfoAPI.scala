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

  def deleteModelInfo(_id : String): JValue = {

    ModelInfo.delete(("_id" -> _id))

    { "SUCCESS" -> " DELETED " } : JValue

  }

  def insertModelInfo(q : JValue): JValue = {
    val json = q.asInstanceOf[JObject].values

    { "SUCCESS" -> ModelInfo.createRecord.id(UUID.randomUUID().toString)
                            .name(json.apply("name").toString)
                            .description(json.apply("description").toString)
                            .status(json.apply("status").toString)
                            .save.asJValue
    } : JValue

  }

  def updateModelInfo(q : JValue): JValue = {
    val json = q.asInstanceOf[JObject].values

    val qry = QueryBuilder.start("_id").is(json.apply("id").toString)
                          .get

    var update = ModelInfo.findAll(qry)

    { "SUCCESS" -> update(0).update
                            .name(json.apply("name").toString)
                            .description(json.apply("description").toString)
                            .status(json.apply("status").toString)
                            .save.asJValue
    } : JValue

  }

  def viewModelInfo(id : String): JValue = {

    val qry = QueryBuilder.start("ModelId").is(id).get

    var DBList = Factor.findAll(qry)

    { "SUCCESS" -> DBList.map(_.asJValue)
    } : JValue

  }

  def range(id : String) : JValue = {

    var range = code.rest.FactorAPI.ScoringRange(id)

    { "SUCCESS" -> range } : JValue
  }

  def rangeAndUpdate(id : String) : JValue = {

    var range = code.rest.FactorAPI.ScoringRange(id)

    val qry = QueryBuilder.start("_id").is(id).get

    var DBList = ModelInfo.findAll(qry)

    { "SUCCESS" -> DBList(0).update.min(range(0)).max(range(1)).save.asJValue } : JValue
  }

  serve {
    case "modelinfo" :: "getall"  :: Nil JsonGet req => getModelInfoJSON() : JValue

    case "modelinfo" :: "getbymodelinfoid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "getbymodelinfoid" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getModelInfoByIdJSON(id) : JValue

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
