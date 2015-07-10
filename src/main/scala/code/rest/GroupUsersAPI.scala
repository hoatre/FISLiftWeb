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

object GroupUsersAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupUsersAPI)
  }

  def getGroupUserJSON(): JValue = {

    val DBList = GroupUsers.findAll
    if(DBList.isEmpty)
      "ERROR" -> "GroupUser not found" : JValue
    else
      {"GroupUsersList" -> DBList.map(_.asJValue)} : JValue

  }

  def getGroupUserByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = GroupUsers.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "GroupUser not found" :JValue
    else
      {"GroupUsersList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteGroupUser(_id : String): JValue = {

    GroupUsers.delete(("_id" -> _id))

    { "SUCCESS" -> " DELETED " } : JValue

  }

  def insertGroupUser(userid : String, groupid : String): JValue = {

    val groupuserin = groupuserIN.createRecord.groupid(groupid).userid(userid)



    { "SUCCESS" -> GroupUsers.createRecord.id(UUID.randomUUID().toString).groupuser(groupuserin).save.asJValue} : JValue

  }

  def updateGroupUser(id : String, userid : String, groupid : String): JValue = {

    GroupUsers.update(("_id" -> id),
      ("$set" -> ("usergroup.userid" -> userid)
        ~ ("usergroup.groupid" -> groupid)))

    { "SUCCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "usergroup" :: "getall"  :: Nil JsonGet req => getGroupUserJSON() : JValue

    case "usergroup" :: "getbyroleid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "usergroup" :: "getbyroleid" :: id :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getGroupUserByIdJSON(id) : JValue

    case "usergroup" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "usergroup" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(userid) <- (json \\ "userid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
      } yield updateGroupUser(id, userid, groupid)

    case "usergroup" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "usergroup" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteGroupUser(id)

//    case "usergroup" :: "delete" :: id :: Nil JsonDelete req => deleteGroupUser(id)

    case "usergroup" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "usergroup" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(userid) <- (json \\ "userid").toOpt
          JString(groupid) <- (json \\ "groupid").toOpt
      } yield insertGroupUser(userid, groupid)
  }

}

