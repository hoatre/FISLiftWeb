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
      "ERROR" -> "Role not found" : JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def getGroupModuleByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = GroupModules.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "Role not found" :JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteGroupModule(_id : String): JValue = {

    GroupModules.delete(("_id" -> _id))

    { "SUSCESS" -> " DELETED " } : JValue

  }

  def insertGroupModule(moduleid : String, groupid : String): JValue = {

    val groupmodulein = groupmoduleIN.createRecord.groupid(groupid).moduleid(moduleid)

    GroupModules.createRecord.id(UUID.randomUUID().toString).groupmodule(groupmodulein).save

    { "SUSCESS" -> " INSERTED " } : JValue

  }

  def updateGroupModule(id : String, moduleid : String, groupid : String): JValue = {

    Groups.update(("_id" -> id),
      ("$set" -> ("groupmodule.moduleid" -> moduleid)
        ~ ("groupmodule.groupid" -> groupid)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "groupmodule" :: "getall"  :: Nil JsonGet req => getGroupModuleJSON() : JValue

    case "groupmodule" :: "getbyroleid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getGroupModuleByIdJSON(id) : JValue

    case "groupmodule" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(moduleid) <- (json \\ "moduleid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
      } yield updateGroupModule(id, moduleid, groupid)

    case "groupmodule" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteGroupModule(id)

    case "groupmodule" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(moduleid) <- (json \\ "moduleid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
      } yield insertGroupModule(moduleid, groupid)
  }

}

