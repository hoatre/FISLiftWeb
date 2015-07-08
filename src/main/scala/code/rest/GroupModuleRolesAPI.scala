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

object GroupModuleRolesAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupModuleRolesAPI)
  }

  def getGroupModuleRoleJSON(): JValue = {

    val DBList = GroupModuleRoles.findAll
    if(DBList.isEmpty)
      "ERROR" -> "GroupModule not found" : JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def getGroupModuleRoleByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = GroupModuleRoles.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "GroupModule not found" :JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteGroupModuleRole(_id : String): JValue = {

    GroupModuleRoles.delete(("_id" -> _id))

    { "SUSCESS" -> " DELETED " } : JValue

  }

  def insertGroupModuleRole(moduleid : String, groupid : String, roleid : String): JValue = {

    val groupmodulerolein = groupmoduleroleIN.createRecord.groupid(groupid).moduleid(moduleid).roleid(roleid)

    GroupModuleRoles.createRecord.id(UUID.randomUUID().toString).groupmodulerole(groupmodulerolein).save

    { "SUSCESS" -> " INSERTED " } : JValue

  }

  def updateGroupModuleRole(id : String, moduleid : String, groupid : String, roleid : String): JValue = {

    GroupModuleRoles.update(("_id" -> id),
      ("$set" -> ("groupmodulerole.moduleid" -> moduleid)
        ~ ("groupmodulerole.groupid" -> groupid)
        ~ ("groupmodulerole.roleid" -> roleid)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "groupmodule" :: "getall"  :: Nil JsonGet req => getGroupModuleRoleJSON() : JValue

    case "groupmodule" :: "getbyroleid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getGroupModuleRoleByIdJSON(id) : JValue

    case "groupmodule" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(moduleid) <- (json \\ "moduleid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
          JString(roleid) <- (json \\ "roleid").toOpt
      } yield updateGroupModuleRole(id, moduleid, groupid, roleid)

    case "groupmodule" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteGroupModuleRole(id)

    case "groupmodule" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(moduleid) <- (json \\ "moduleid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
          JString(roleid) <- (json \\ "roleid").toOpt
      } yield insertGroupModuleRole(moduleid, groupid, roleid)
  }

}

