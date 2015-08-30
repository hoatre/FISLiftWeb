package code.model.oauth2

import java.util.UUID

import code.common.Message
import com.mongodb.QueryBuilder
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.{Limit, Skip}
import net.liftweb.mongodb.BsonDSL

import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model._
import net.liftweb.json.Printer._
import net.liftweb.mongodb.{Limit, Skip, JObjectParser}
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import net.liftweb.record.field.{PasswordField, LongField, StringField}
import net.liftweb.util.{BCrypt, Helpers, Props}
import org.bson.types.ObjectId

import scala.collection.immutable.HashMap
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Random
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.S
import bootstrap.liftweb.UsersDb


/**
 * Created by bacnv on 11/08/2015.
 */
class User private () extends MongoRecord[User] with StringPk[User] {

  override def meta = User

  // An embedded document:
  object  username extends StringField(this,1024)
  object email  extends StringField(this,1024)
  object password  extends PasswordField(this)
  object imageurl  extends StringField(this,1024)
  object picture  extends StringField(this,1024)
  object facebookid  extends StringField(this,1024)
  object googleid  extends StringField(this,1024)
  object displayname  extends StringField(this,1024)
  object status  extends StringField(this,1024)
  object description  extends StringField(this,1024)
  object note  extends StringField(this,1024)
  object crated_by  extends StringField(this,1024)
  object created_date  extends LongField(this,15)
  object modified_by  extends StringField(this,1024)
  object modified_date  extends LongField(this,15)

}
object User extends User with MongoMetaRecord[User] {
  override def collectionName = "users"
  override def mongoIdentifier = UsersDb

  def get(id: String): Option[User] = {
   User.find("_id" -> id)
  }
//
  def findByUsername(username: String): Option[User] ={
  User.find("username" -> username)

  }

  def findByEmail(email: String): Option[User] ={
    User.find("email" -> email)

  }


  /**
   * @param username Username to find
   * @param encryptedPassword Encrypted version of password
   * @return Option containing User.
   */
  def findByUsernameAndPassword(username: String, encryptedPassword: String): Option[User] = {

    User.find(("username" -> username) ~ ("password" -> encryptedPassword))
  }

  /**
   * @param user User object with already encrypted password
   * @return
   */
  def insert(user: User) = {
   user.save
  }

  /**
   * @param id User id to be updated
   * @param user New User details
   * @return
   */
  def update(id: String, user: User) = {

  }

//  /**
//   * @param user User object to be deleted
//   * @return
//   */
//  def delete(user: User) = {
////    User.delete(("_id" -> user.id.toString()))
//    User.deleteAll("_id" -> user.id.toString)
//  }

  /**
   * Delete all the users. NOTE: Use with caution.
   * @return
   */
  def deleteAll() = {

  }

  def getbyid(q:String):JValue ={
    val f = User.find("_id" -> q)

    f match {
      case Full(s) => return  Message.returnMassage("user","0","success",s.asJValue)
      case _ => return  Message.returnMassage("user","1","Not found",""->"")
    }
  }

  def searh(q:List[String]) : JValue= {
    println(S.uri)
    var pageIndex: Int = 1
    var pageSize: Int = 5
    var id =""
    var username =""
    var email =""
    var orderby ="created_date"
    val qry = QueryBuilder.start().get()
    var jmap : Map[String,String] = Map()
    var order = ("created_date" -> -1)
    for (req <- S.request.toList) {
      for (paramName <- req.paramNames) {
        val Full(a) = S.param(paramName)
        if (paramName.toLowerCase.equals("pageindex")) {
          pageIndex = a.toString.toInt
        } else if (paramName.toLowerCase.equals("pagesize")) {
          pageSize = a.toString.toInt
        }else if(paramName.toLowerCase.equals("id")){
          id = a.toString
        } else if(paramName.toLowerCase.equals("username")){
          username = a.toString
        }else if(paramName.toLowerCase.equals("email")){
          email = a.toString
        }else if(paramName.toLowerCase.equals("order_by")){
          orderby = a.toString
        }
      }
    }
    if(!id.isEmpty && id != ""){
      jmap += "_id" -> id
    }
    if(!username.isEmpty && username != ""){
      jmap += "username" -> username
    }
    if(!email.isEmpty && email != ""){
      jmap += "email" -> email
    }

    if(!orderby.isEmpty && orderby != ""){
      order = (orderby -> -1)
    }
    val db =User.findAll(jmap,order,Skip(pageSize * (pageIndex - 1)), Limit(pageSize))
   val count = User.count(qry)

    Message.returnMassage("user", "0", "Success", db.map(_.asJValue), count)
  }
  def insert(q:JValue) :  JValue= {
    val jsonmap : Map[String,Any] = q.values.asInstanceOf[Map[String,Any]]
    val id = UUID.randomUUID().toString
    var  username = ""
    var email  = ""
    var password  =""
    var imageurl  =""
    var picture  =""
    var facebookid =""
    var googleid = ""
    var displayname  =""
    var status  = ""
    var description  =""
    var note  =""
    var created_by  =""
    var created_date  = System.currentTimeMillis()/1000
    var modified_by  =""
    var modified_date  = System.currentTimeMillis()/1000

    for((key,value) <- jsonmap){
      if(key.toString.equals("username")){
        username = value.toString
      }else  if(key.toString.equals("email")){
        email = value.toString
      } else if(key.toString.equals("password")){
        password =  value.toString
      } else if(key.toString.equals("imageurl")){
        imageurl = value.toString
      } else if(key.toString.equals("picture")){
        picture = value.toString
      } else if(key.toString.equals("facebookid")){
        facebookid = value.toString
      } else if(key.toString.equals("googleid")){
        googleid = value.toString
      } else if(key.toString.equals("displayname")){
        displayname = value.toString
      } else if(key.toString.equals("status")){
        status = value.toString
      } else if(key.toString.equals("description")){
        description = value.toString
      } else if(key.toString.equals("note")){
        note = value.toString
      } else if(key.toString.equals("created_by")){
        created_by = value.toString
      }


    }
    if(email.isEmpty || email == "" ){
      return Message.returnMassage("insertuser","1","Email must be exist",("" -> ""))
    }else{
      val count = User.count("email" -> email)
      if(count>0){
        return Message.returnMassage("insertuser","11","Email existed",("" -> ""))
      }
    }
    if(password.isEmpty || password == ""){
      return Message.returnMassage("insertuser","2","Password must be exist",("" -> ""))
    }else{
      password =  BCrypt.hashpw(password, BCrypt.gensalt())
    }
    if(username.isEmpty || username == ""){
      return Message.returnMassage("insertuser","3","Username must be exist",("" -> ""))
    }else{
      val count = User.count("username" -> username)
      if(count>0){
        return Message.returnMassage("insertuser","31","Username existed",("" -> ""))
      }
    }

   val user= User.createRecord.id(id).crated_by(created_by).created_date(created_date).description(description).email(email)
    .displayname(displayname).facebookid(facebookid).googleid(googleid).modified_by(created_by).modified_date(modified_date)
    .note(note).password(password).picture(picture).status(status).username(username).save(true)

  Message.returnMassage("insertuser","0","Success",user.asJValue)

  }
  def update(q: JValue): JValue = {
    val jsonmap: Map[String, Any] = q.values.asInstanceOf[Map[String, Any]]
    var qry1: Map[String, String] = Map()
    var modified_date = System.currentTimeMillis() / 1000
    var id = ""

    for ((key, value) <- jsonmap) {
      if (key.toString.equals("id")) {
        id = value.toString

      }else if(key.toString.equals("username")){
        if(value.toString.isEmpty || value == "" ){
          return Message.returnMassage("insertuser","1","Username must be exist",("" -> ""))
        }
        qry1 += key -> value.toString
      }else  if(key.toString.equals("email")){
        if(value.toString.isEmpty || value == "" ){
          return Message.returnMassage("insertuser","1","Email must be exist",("" -> ""))
        }
        qry1 += key -> value.toString
      } else if(key.toString.equals("password")){

        if (value.toString.isEmpty || value == "") {
          return Message.returnMassage("updateUser", "3", "Password must be exist", ("" -> ""))
        }
        qry1 += key -> BCrypt.hashpw(value.toString, BCrypt.gensalt())
      } else if(key.toString.equals("imageurl")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("picture")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("facebookid")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("googleid")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("displayname")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("status")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("description")){
        qry1 += key -> value.toString
      } else if(key.toString.equals("note")){
        qry1 += key -> value.toString
      } else if (key.toString.equals("modified_by")) {
        qry1 += key -> value.toString
      }


    }
    qry1 += "modified_date" -> modified_date.toString

    if (id.isEmpty || id == "") {
      return Message.returnMassage("updateUser", "3", "Id must be exist", ("" -> ""))
    }
    val count = User.findAll("_id" -> id)
    if (count.size == 0) {
      return Message.returnMassage("updateUser", "4", "User not found", ("" -> ""))
    }

    User.update(("_id" -> id), ("$set" -> qry1))
    val application = User.findAll("_id" -> id)

    Message.returnMassage("updateUser", "0", "Success", application(0).asJValue)

  }

  def delete(q: String): JValue = {
    User.delete(("_id" -> q))
    Message.returnMassage("deleteUser", "0", "Success", ("" -> ""))
  }

}
