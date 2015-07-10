package code.rest

/**
 * Created by phong on 7/8/2015.
 */
import java.util.UUID

import code.model._
import com.mongodb.QueryBuilder
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._

object FactorOptionAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(FactorOptionAPI)
  }

  def getFactorOptionJSON(): JValue = {

    val DBList = FactorOption.findAll
    if(DBList.isEmpty)
      "ERROR" -> "FactorOption not found" : JValue
    else
      {"FactorOptionsList" -> DBList.map(_.asJValue)} : JValue

  }

  def getFactorOptionByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = FactorOption.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "FactorOption not found" :JValue
    else
      {"FactorOptionsList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteFactorOption(_id : String): JValue = {

    FactorOption.delete(("_id" -> _id))

    { "SUSCESS" -> " DELETED " } : JValue

  }

  def insertFactorOption(factorid : String, name : String, description : String, score : String, fatal : String, ordinal : String): JValue = {

    val FactorOptionin = factoroptionIN.createRecord.factorid(factorid).name(name).description(description).score(score).fatal(fatal).ordinal(ordinal)

    FactorOption.createRecord.id(UUID.randomUUID().toString).factoroption(FactorOptionin).save

    { "SUSCESS" -> " INSERTED " } : JValue

  }

  def updateFactorOption(id : String, factorid : String, name : String, description : String, score : String, fatal : String, ordinal : String): JValue = {

    FactorOption.update(("_id" -> id),
      ("$set" -> ("factoroption.factorid" -> factorid)
        ~ ("factoroption.name" -> name)
        ~ ("factoroption.description" -> description)
        ~ ("factoroption.score" -> score)
        ~ ("factoroption.fatal" -> fatal)
        ~ ("factoroption.ordinal" -> ordinal)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "factoroption" :: "getall"  :: Nil JsonGet req => getFactorOptionJSON() : JValue

    case "factoroption" :: "getbyfactoroptionid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factoroption" :: "getbyfactoroptionid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getFactorOptionByIdJSON(id) : JValue

    case "factoroption" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factoroption" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(factorid) <- (json \\ "factorid").toOpt
          JString(description) <- (json \\ "description").toOpt
          JString(score) <- (json \\ "score").toOpt
          JString(fatal) <- (json \\ "fatal").toOpt
          JString(ordinal) <- (json \\ "ordinal").toOpt
          JString(name) <- (json \\ "name").toOpt
      } yield updateFactorOption(id, factorid, description, score, fatal, name, ordinal)

    case "factoroption" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factoroption" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteFactorOption(id)

//    case "factoroption" :: "delete" :: id :: Nil JsonDelete req => deleteFactorOption(id)

    case "factoroption" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factoroption" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(factorid) <- (json \\ "factorid").toOpt
          JString(description) <- (json \\ "description").toOpt
          JString(score) <- (json \\ "score").toOpt
          JString(fatal) <- (json \\ "fatal").toOpt
          JString(ordinal) <- (json \\ "ordinal").toOpt
          JString(name) <- (json \\ "name").toOpt
      } yield insertFactorOption(factorid, description, score, fatal, name, ordinal)
  }

}
