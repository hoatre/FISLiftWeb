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

  def insertModelInfo(name : String, description : String, status : String): JValue = {

    val ModelInfoin = modelinfoIN.createRecord.name(name).description(description).status(status)



    { "SUCCESS" -> ModelInfo.createRecord.id(UUID.randomUUID().toString).modelinfo(ModelInfoin).save.asJValue } : JValue

  }

  def updateModelInfo(id : String, name : String, description : String, status : String): JValue = {

    ModelInfo.update(("_id" -> id),
      ("$set" -> ("modelinfo.name" -> name)
        ~ ("modelinfo.description" -> description)
        ~ ("modelinfo.status" -> status)))

    { "SUCCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "modelinfo" :: "getall"  :: Nil JsonGet req => getModelInfoJSON() : JValue

    case "modelinfo" :: "getbymodelinfoid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "getbymodelinfoid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getModelInfoByIdJSON(id) : JValue

    case "modelinfo" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(description) <- (json \\ "description").toOpt
          JString(status) <- (json \\ "status").toOpt
          JString(name) <- (json \\ "name").toOpt
      } yield updateModelInfo(id, description, name, status)

    case "modelinfo" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteModelInfo(id)

    //    case "modelinfo" :: "delete" :: id :: Nil JsonDelete req => deleteModelInfo(id)

    case "modelinfo" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "modelinfo" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(description) <- (json \\ "description").toOpt
          JString(status) <- (json \\ "status").toOpt
          JString(name) <- (json \\ "name").toOpt
      } yield insertModelInfo(description, name, status)
  }

}
