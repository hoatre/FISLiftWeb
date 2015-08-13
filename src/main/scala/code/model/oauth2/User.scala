package code.model.oauth2

import java.util.UUID

import bootstrap.liftweb.UsersDb
import code.common.Message
import com.mongodb.QueryBuilder
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.mongodb.{Limit, Skip}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.{LongField, PasswordField, StringField}
import net.liftweb.util.BCrypt
import org.bson.types.ObjectId

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


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
  override def collectionName = "user"
  override def mongoIdentifier = UsersDb

  def get(id: String): Option[User] = {
   User.find("_id" -> id)
  }
//
  def findByUsername(username: String): Option[User] ={
  User.find("username" -> username)

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


  def searh(q:List[String]) : JValue= {
    var pageIndex: Int = 1
    var pageSize: Int = 5
    var id =""
    var username =""
    var email =""
    var orderby ="created_date"
    val qry = QueryBuilder.start().get()
    var qry1 : JObject = ("" -> "")
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
       if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("_id" -> id) else qry1 = ("_id" -> id)
    }
    if(!username.isEmpty && username != ""){
      if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("username" -> username) else qry1 = ("username" -> username)
    }
    if(!email.isEmpty && email != ""){
      if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("email" -> email) else qry1 = ("email" -> email)
    }

    if(!orderby.isEmpty && orderby != ""){
      order = (orderby -> -1)
    }
    val db =User.findAll(qry1,order,Skip(pageSize * (pageIndex - 1)), Limit(pageSize))
   val count = User.count(qry)

    Message.returnMassage("user", "0", "Success", db.map(_.asJValue), count)
  }
  def insert(q:JValue) :  JValue= {
    val jsonmap : Map[String,String] = q.values.asInstanceOf[Map[String,String]]
    val id = ObjectId.get().toString
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
        username = value
      }else  if(key.toString.equals("email")){
        email = value
      } else if(key.toString.equals("password")){
        password =  value
      } else if(key.toString.equals("imageurl")){
        imageurl = value
      } else if(key.toString.equals("picture")){
        picture = value
      } else if(key.toString.equals("facebookid")){
        facebookid = value
      } else if(key.toString.equals("googleid")){
        googleid = value
      } else if(key.toString.equals("displayname")){
        displayname = value
      } else if(key.toString.equals("status")){
        status = value
      } else if(key.toString.equals("description")){
        description = value
      } else if(key.toString.equals("note")){
        note = value
      } else if(key.toString.equals("created_by")){
        created_by = value
      }


    }
    if(email.isEmpty || email == "" ){
      return Message.returnMassage("insertuser","1","Email must be exist",("" -> ""))
    }
    if(password.isEmpty || password == ""){
      return Message.returnMassage("insertuser","1","Password must be exist",("" -> ""))
    }else{
      password =  BCrypt.hashpw(password, BCrypt.gensalt())
    }
    if(username.isEmpty || username == ""){
      return Message.returnMassage("insertuser","1","Username must be exist",("" -> ""))
    }

   val user= User.createRecord.id(id).crated_by(created_by).created_date(created_date).description(description)
    .displayname(displayname).facebookid(facebookid).googleid(googleid).modified_by(created_by).modified_date(modified_date)
    .note(note).password(password).picture(picture).status(status).save(true)

  Message.returnMassage("insertuser","0","Success",user.asJValue)

  }

}
