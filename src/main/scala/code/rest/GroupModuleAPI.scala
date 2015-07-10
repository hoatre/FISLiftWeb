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

object GroupModuleAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupModuleAPI)
  }

  def getGroupModuleJSON(): JValue = {

    val DBList = GroupModules.findAll
    if(DBList.isEmpty)
      "ERROR" -> "GroupModule not found" : JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def getGroupModuleByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = GroupModules.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "GroupModule not found" :JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteGroupModule(_id : String): JValue = {

    GroupModules.delete(("_id" -> _id))

    { "SUCCESS" -> " DELETED " } : JValue

  }

  def insertGroupModule(moduleid : String, groupid : String): JValue = {

    val groupmodulein = groupmoduleIN.createRecord.groupid(groupid).moduleid(moduleid)



    { "GroupModuleItem" -> GroupModules.createRecord.id(UUID.randomUUID().toString).groupmodule(groupmodulein).save.asJValue } : JValue

  }

  def updateGroupModule(id : String, moduleid : String, groupid : String): JValue = {

    GroupModules.update(("_id" -> id),
      ("$set" -> ("groupmodule.moduleid" -> moduleid)
        ~ ("groupmodule.groupid" -> groupid)))

    { "SUCCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "groupmodule" :: "getall"  :: Nil JsonGet req => getGroupModuleJSON() : JValue

    case "groupmodule" :: "getbyroleid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "groupmodule" :: "getbyroleid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getGroupModuleByIdJSON(id) : JValue

    case "groupmodule" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "groupmodule" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(moduleid) <- (json \\ "moduleid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
      } yield updateGroupModule(id, moduleid, groupid)

    case "groupmodule" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "groupmodule" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteGroupModule(id)

//    case "groupmodule" :: "delete" :: id :: Nil JsonDelete req => deleteGroupModule(id)

    case "groupmodule" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "groupmodule" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(moduleid) <- (json \\ "moduleid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
      } yield insertGroupModule(moduleid, groupid)
  }

}

