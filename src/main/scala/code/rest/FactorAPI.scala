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

object FactorAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(FactorAPI)
  }

  def getFactorJSON(): JValue = {

    val DBList = Factor.findAll
    if(DBList.isEmpty)
      "ERROR" -> "Factor not found" : JValue
    else
      {"FactorsList" -> DBList.map(_.asJValue)} : JValue

  }

  def getFactorByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = Factor.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "Factor not found" :JValue
    else
      {"FactorsList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteFactor(_id : String): JValue = {

    Factor.delete(("_id" -> _id))

    { "SUSCESS" -> " DELETED " } : JValue

  }

  def insertFactor(parentid : String, parentname : String, name : String, description : String, weigth : String): JValue = {

    val Factorin = factorIN.createRecord.parentid(parentid).parentname(parentname).name(name).description(description).weigth(weigth)

    Factor.createRecord.id(UUID.randomUUID().toString).factor(Factorin).save

    { "SUSCESS" -> " INSERTED " } : JValue

  }

  def updateFactor(id : String, parentid : String, parentname : String, name : String, description : String, weigth : String): JValue = {

    Factor.update(("_id" -> id),
      ("$set" -> ("factor.parentid" -> parentid)
        ~ ("factor.parentname" -> parentname)
        ~ ("factor.name" -> name)
        ~ ("factor.weigth" -> weigth)
        ~ ("factor.description" -> description)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "factor" :: "getall"  :: Nil JsonGet req => getFactorJSON() : JValue

    case "factor" :: "getbyfactorid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getFactorByIdJSON(id) : JValue

    case "factor" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(parentid) <- (json \\ "parentid").toOpt
          JString(parentname) <- (json \\ "parentname").toOpt
          JString(weigth) <- (json \\ "weigth").toOpt
          JString(description) <- (json \\ "description").toOpt
          JString(name) <- (json \\ "name").toOpt
      } yield updateFactor(id, parentid, parentname, weigth, description, name)

    case "factor" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteFactor(id)

    case "factor" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(parentid) <- (json \\ "parentid").toOpt
          JString(parentname) <- (json \\ "parentname").toOpt
          JString(weigth) <- (json \\ "weigth").toOpt
          JString(description) <- (json \\ "description").toOpt
          JString(name) <- (json \\ "name").toOpt
      } yield insertFactor(parentid, parentname, weigth, description, name)
  }

}
