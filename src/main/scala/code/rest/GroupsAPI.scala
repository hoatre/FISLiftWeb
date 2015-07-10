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
 * Created by phong on 7/7/2015.
 */
object GroupsAPI extends RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupsAPI)
  }

  def getGroupJSON(): JValue = {

    val DBList = Groups.findAll
    if(DBList.isEmpty)
      "ERROR" -> "Group not found" : JValue
    else
      {"GroupsList" -> DBList.map(_.asJValue)} : JValue

  }

  def getGroupByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id).get

    val DBList = Groups.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "Group not found" :JValue
    else
      {"GroupsList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteGroup(_id : String): JValue = {

    Groups.delete(("_id" -> _id))

    { "SUSCESS" -> " DELETED " } : JValue

  }

  def insertGroup(status : String, note : String, groupname : String): JValue = {

    val groupin = groupIN.createRecord.groupname(groupname).note(note).status(status)

    Groups.createRecord.id(UUID.randomUUID().toString).group(groupin).save

    { "SUSCESS" -> " INSERTED " } : JValue

  }

  def updateGroup(id : String, status : String, note : String, groupname : String): JValue = {

    Groups.update(("_id" -> id),
      ("$set" -> ("group.groupname" -> groupname) ~ ("group.note" -> note) ~ ("group.status" -> status)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }

  def test(req : String): JValue = {
    println(req)

    { "SUSCESS" -> req } : JValue

  }

  serve {
    case "group" :: "getall"  :: Nil JsonGet req => getGroupJSON() : JValue

    case "group" :: "getbygroupid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "group" :: "getbygroupid" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getGroupByIdJSON(id)

    case "group" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "group" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(status) <- (json \\ "status").toOpt
          JString(note) <- (json \\ "note").toOpt
          JString(groupname) <- (json \\ "groupname").toOpt
      } yield updateGroup(id, status, note, groupname)

    case "group" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "group" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield  deleteGroup(id)

    case "group" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "group" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(status) <- (json \\ "status").toOpt
          JString(note) <- (json \\ "note").toOpt
          JString(groupname) <- (json \\ "groupname").toOpt
      } yield insertGroup(status, note, groupname)

  }
}
