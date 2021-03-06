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
 * Created by bacnv on 13/08/2015.
 */
class RoleGroup private () extends MongoRecord[RoleGroup] with StringPk[RoleGroup] {

  override def meta = RoleGroup

  // An embedded document:
  object role_id extends StringField(this,1024)
  object group_id extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)


}
object RoleGroup extends RoleGroup with MongoMetaRecord[RoleGroup] {
  override def collectionName = "RoleGroups"

  override def mongoIdentifier = UsersDb
  def searh(q: List[String]): JValue = {
    var pageIndex: Int = 1
    var pageSize: Int = 5
    var id = ""
    var role_id = ""
    var group_id = ""
    var orderby = "created_date"
    val qry = QueryBuilder.start().get()
    //    var qry1: JObject = ("" -> "")
    var jmap : Map[String,String] = Map()
    var order = ("created_date" -> -1)
    for (req <- S.request.toList) {
      for (paramName <- req.paramNames) {
        val Full(a) = S.param(paramName)
        if (paramName.toLowerCase.equals("pageindex")) {
          pageIndex = a.toString.toInt
        } else if (paramName.toLowerCase.equals("pagesize")) {
          pageSize = a.toString.toInt
        } else if (paramName.toLowerCase.equals("id")) {
          id = a.toString
        } else if (paramName.toLowerCase.equals("role_id")) {
          role_id = a.toString
        } else if (paramName.toLowerCase.equals("group_id")) {
          group_id = a.toString
        }  else if (paramName.toLowerCase.equals("order_by")) {
          orderby = a.toString
        }
      }
    }
    if (!id.isEmpty && id != "") {
      jmap += "_id" -> id
    }
    if (!role_id.isEmpty && role_id != "") {
      jmap += "role_id" -> role_id
    }
    if (!group_id.isEmpty && group_id != "") {
      jmap += "group_id" -> group_id
    }


    if (!orderby.isEmpty && orderby != "") {
      order = (orderby -> -1)
    }
    val db = RoleGroup.findAll(jmap, order, Skip(pageSize * (pageIndex - 1)), Limit(pageSize))
    val count = RoleGroup.count(qry)

    Message.returnMassage("RoleGroup", "0", "Success", db.map(_.asJValue), count)
  }
  def insert(q:JValue) :  JValue= {
    val jsonmap : Map[String,String] = q.values.asInstanceOf[Map[String,String]]
    val id = UUID.randomUUID().toString
    var  role_id = ""
    var group_id  = ""
    var created_by  =""
    var created_date  = System.currentTimeMillis()/1000

    for((key,value) <- jsonmap){
      if(key.toString.equals("role_id")){
        role_id = value
      }else  if(key.toString.equals("group_id")){
        group_id = value
      } else if(key.toString.equals("created_by")){
        created_by = value
      }


    }
    if(role_id.isEmpty || role_id == "" ){
      return Message.returnMassage("RoleGroup","1","Role ID must be exist",("" -> ""))
    }
    if(group_id.isEmpty || group_id == ""){
      return Message.returnMassage("RoleGroup","2","Group ID must be exist",("" -> ""))
    }
    try {
      val count = RoleGroup.count(("role_id" -> role_id) ~ ("group_id" -> group_id))

      if (count > 0) {
        return Message.returnMassage("RoleGroup", "3", "RoleGroup existed", ("" -> ""))
      }
    }
    val user= RoleGroup.createRecord.id(id).role_id(role_id).created_date(created_date).group_id(group_id)
      .created_by(created_by).save(true)

    Message.returnMassage("RoleGroup","0","Success",user.asJValue)

  }
  def delete(q: String): JValue = {
    RoleGroup.delete(("_id" -> q))
    Message.returnMassage("deleteRoleGroup", "0", "Success", ("" -> ""))
  }

}