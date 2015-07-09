package code.rest

import java.util.UUID

import code.model._
import com.mongodb.QueryBuilder
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._

/**
 * Created by phong on 7/8/2015.
 */
object RolesAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(RolesAPI)
  }

  def getRoleJSON(): JValue = {

    val DBList = Roles.findAll
    if(DBList.isEmpty)
      "ERROR" -> "Role not found" : JValue
    else
      {"RolesList" -> DBList.map(_.asJValue)} : JValue

  }

  def getRoleByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = Roles.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "Role not found" :JValue
    else
      {"RolesList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteRole(_id : String): JValue = {

    Roles.delete(("_id" -> _id))

    { "SUSCESS" -> " DELETED " } : JValue

  }

  def insertRole(status : String, note : String, rolename : String, controlid : String): JValue = {

    val rolein = roleIN.createRecord.rolename(rolename).note(note).status(status).controlid(controlid)

    Roles.createRecord.id(UUID.randomUUID().toString).role(rolein).save

    { "SUSCESS" -> " INSERTED " } : JValue

  }

  def updateRole(id : String, status : String, note : String, rolename : String, controlid : String): JValue = {

    Roles.update(("_id" -> id),
      ("$set" -> ("role.rolename" -> rolename)
                  ~ ("role.note" -> note)
                  ~ ("role.status" -> status)
                  ~ ("role.controlid" -> controlid)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "role" :: "getall"  :: Nil JsonGet req => getRoleJSON() : JValue

    case "role" :: "getbyroleid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getRoleByIdJSON(id) : JValue

    case "role" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(status) <- (json \\ "status").toOpt
          JString(note) <- (json \\ "note").toOpt
          JString(rolename) <- (json \\ "rolename").toOpt
          JString(controlid) <- (json \\ "controlid").toOpt
      } yield updateRole(id, status, note, rolename, controlid)

//    case "role" :: "delete" :: Nil JsonPost json -> request =>
//      for{JString(id) <- (json \\ "id").toOpt} yield deleteRole(id)

    case "role" :: "delete" :: id :: Nil JsonDelete req => deleteRole(id)

    case "role" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(status) <- (json \\ "status").toOpt
          JString(note) <- (json \\ "note").toOpt
          JString(rolename) <- (json \\ "rolename").toOpt
          JString(controlid) <- (json \\ "controlid").toOpt
      } yield insertRole(status, note, rolename, controlid)
  }

}
