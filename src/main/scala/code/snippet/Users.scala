package code.snippet

import java.util.UUID

import com.mongodb.{BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.http.{S, LiftRules}
import net.liftweb.json
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model.{Users => UsersModel, Groups, groupIN}
import net.liftweb.json.Printer._
import net.liftweb.json.{JsonAST, JObject, Extraction}
import net.liftweb.mongodb.JObjectParser
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import code.dao.{Users => UserDAO}
/**
 * Created by bacnv on 7/8/15.
 */
object Users {

  def getall(rest : String, q : String) : JValue = {

    var dbOject : List[UsersModel] = List.empty

    if(!q.isEmpty) {
      val qry = QueryBuilder.start("user.username").is(q)
        .get

      dbOject = UsersModel.findAll(qry)

    }else if (rest.equals("getall")){

      dbOject = UsersModel.findAll

    }else{

      dbOject = UsersModel.findAll
    }



    if(dbOject.isEmpty){

      "ERROR" -> "User not found" :JValue
    }

    else {"SUSCESS" -> dbOject.map(_.asJValue)} :JValue
  }

  def getupdate(_id :String, name : String) : JValue = {

    println(name)
    val dbo = BasicDBObjectBuilder.start
      .append("$set", BasicDBObjectBuilder.start
      .append("user.name", name).get).get

    UsersModel.update(("_id" -> _id), ("$set" -> ("user.name" -> name)))


    val a = { "SUSCESS" -> " Tao update roi nhe" }

    a:JValue
  }

  def getDelete(_id:String) :JValue ={

    UsersModel.delete(("_id" -> _id))


    val a = { "SUSCESS" -> " Tao xoa roi nhe" }

    a:JValue
  }

  def getbyuser(username:String,key:String) : List[UsersModel]={

    var dblist : List[UsersModel] = List.empty

    if(!key.isEmpty) {


      val qry = QueryBuilder.start("user.username").is(username)
        .get

      dblist = UsersModel.findAll(qry)
    }

    dblist

  }

  def getuserbyusername(q:List[String]) : JValue = {

    var s = q
    var msg :JValue = {"ERROR" -> "Token is expried"}:JValue
    try {

      val a = (for {
        username <- s ::: S.params("username")
        key <- s ::: S.params("key")

        item <- getbyuser(username, key).map(_.asJValue)
      } yield item).distinct
      if(a.isEmpty) {
        msg  = {"ERROR" -> "User not found"}
      }else{
        msg  = {"SUCCESS" -> a}
      }
      msg
    } catch {

      case unknow => {"ERROR" -> unknow.toString} :JValue
    }


  }


  def greet(name: String) : JValue =
    "greeting" -> ("HELLO "+name.toUpperCase)

//  def insertUser(q:List[String]): JValue = {
//
//    var s = q
//    var msg :JValue = {"ERROR" -> "Token is expried"}:JValue
//    var user :UsersModel = new UsersModel
//
//
//    try {
//
//      for{JString(id) <- (json \\ "id").toOpt
//          JString(status) <- (json \\ "status").toOpt
//          JString(note) <- (json \\ "note").toOpt
//          JString(groupname) <- (json \\ "groupname").toOpt
//      } yield greet(id)
////      if(a.isEmpty) {
////        msg  = {"ERROR" -> "User not found"}
////      }else{
////        msg  = {"SUCCESS" -> a}
////      }
//      msg
//    } catch {
//
//      case unknow => {"ERROR" -> unknow.toString} :JValue
//    }
//
////    val groupin = groupIN.createRecord.groupname(groupname).note(note).status(status)
////
////    Groups.createRecord.id(UUID.randomUUID().toString).group(groupin).save
//
//    { "SUSCESS" -> " INSERTED " } : JValue
//
//  }

  def insertupdatedeleteUser(q : JsonAST.JValue ): JValue = {

    var json = q
    var msg :JValue = {"ERROR" -> "Token is expried"}:JValue
//    var user : UsersModel = {new UsersModel}
    try {

     val s = for{
          JString(action) <- (json \\ "action").toOpt
//          JString(address) <- (json \\ "address").toOpt
//          JString(name) <- (json \\ "name").toOpt
//          JString(password) <- (json \\ "password").toOpt
//          JString(email) <- (json \\ "email").toOpt
//          JString(username) <- (json \\ "username").toOpt
      } yield {
         if (action.equals("insert")) {
//         var user : UsersModel =  { UsersModel.user.setFromJValue(json)}
//           msg = UserDAO.insertUser(UsersModel.user.setFromJValue(json)).map(_.asJValue)
         } else {

         }
       }
      msg
    } catch {

      case unknow => {"ERROR" -> unknow.toString} :JValue
    }

    //    val groupin = groupIN.createRecord.groupname(groupname).note(note).status(status)
    //
    //    Groups.createRecord.id(UUID.randomUUID().toString).group(groupin).save



  }


}
