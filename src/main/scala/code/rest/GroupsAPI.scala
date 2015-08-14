package code.rest

import code.model._
import com.mongodb.QueryBuilder
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JValue
import net.liftweb.json.JsonAST.{JValue, JObject}
import net.liftweb.json._

//import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._
//import com.mongodb.casbah.Imports._

/**
 * Created by phong on 7/7/2015.
 */
object GroupsAPI extends RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(GroupsAPI)
  }

  def getGroupJSON(): JValue = {

//    val b : groupIN
//    b.groupname.set("asdas")
//    b.setFieldsFromJSON("{'status':'asd'}")
//    var a : Groups() {b}

//    println(a)

    val DBList = Groups.findAll
    if(DBList.isEmpty)
      "ERROR" -> "Group not found" : JValue
    else
      {"GroupsList" -> DBList.map(_.asJValue)} : JValue

  }

  def getGroupByIdJSON(id : String): JValue = {
//    val someObjectId : ObjectId = new ObjectId(id)
//
//    val qry = QueryBuilder.start("_id").is(someObjectId).get
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

  def insertGroup[A](request : A): JValue = {



    println(request)


    {"SUCCESS" -> "sdfds"} : JValue

  }

  def insertFreeFormat(json : String) : JValue = {

//    Groups.insert
//    val b : Field[StringField, Groups] = null
//    val g = Groups.createWithMutableField[StringField](Groups, b, a).save

    { "SUCCESS" -> " INSERT " } : JValue
  }

  def updateGroup(id : String, status : String, note : String, groupname : String): JValue = {

    Groups.update(("_id" -> id),
      ("$set" -> ("group.groupname" -> groupname) ~ ("group.note" -> note) ~ ("group.status" -> status)))

    { "SUSCESS" -> " UPDATED " } : JValue

  }
  def updateGroups(q:JValue):JValue={
    var json = q.asInstanceOf[JObject]

    val qry = QueryBuilder.start("_id").is(json.values.apply("_id")).get

    var dbFind = Groups.findAll(qry)

    val list = (json \ "group")

    var msg :JValue={"ERROR" -> "I Dont know what happen"} :JValue

    val j = list.children
    for{
      JString(note) <- (j \\ "note").toOpt
      JString(groupname) <- (j \\ "groupname").toOpt
      JString(status) <- (j \\ "status").toOpt

      item = groupIN.note(Option(status.toString).getOrElse(null))
        .groupname(Option(groupname.toString).getOrElse(null))
        .status(Option(note.toString).getOrElse(null))

    }yield {

      val update1 = dbFind(0).update.group(item).save
      msg = {"SUCCESS" -> update1.asJValue}:JValue
    }

    msg

  }

//  serve {
//    case "group" :: "insertFreeFormat" :: Nil JsonPost json -> request =>
//      for{JString(model) <- (json \\ "model").toOpt} yield insertFreeFormat(model)
//
//    case "group" :: "getall"  :: Nil JsonGet req => getGroupJSON() : JValue
//
//    case "group" :: "getbygroupid" :: Nil Options _ => {"OK" -> "200"} :JValue
//    case "group" :: "getbygroupid" :: Nil JsonPost json -> request =>
//      for{JString(id) <- (json \\ "id").toOpt} yield getGroupByIdJSON(id)
//
//    case "group" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
//    case "group" :: "update" :: Nil JsonPost json -> request =>
//      for{JString(id) <- (json \\ "id").toOpt
//          JString(status) <- (json \\ "status").toOpt
//          JString(note) <- (json \\ "note").toOpt
//          JString(groupname) <- (json \\ "groupname").toOpt
//      } yield updateGroup(id, status, note, groupname)
//
//    case "group" :: "updates" :: Nil Options _ => {"OK" -> "200"} :JValue
//    case "group" :: "updates" :: Nil JsonPost json -> request =>updateGroups(json)
//
//    case "group" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
//    case "group" :: "delete" :: Nil JsonPost json -> request =>
//      for{JString(id) <- (json \\ "id").toOpt} yield  deleteGroup(id)
//
//    case "group" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
//    case "group" :: "insert" :: Nil JsonPost json -> request  => insertGroup(json.values)
//
//
//  }
}
