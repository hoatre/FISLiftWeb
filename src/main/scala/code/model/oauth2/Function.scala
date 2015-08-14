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
import net.liftweb.record.field.{LongField, StringField}
import net.liftweb.util.{Helpers, Props}
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
class Functions private () extends MongoRecord[Functions] with StringPk[Functions] {

  override def meta = Functions

  // An embedded document:
  object parent_id  extends StringField(this,1024)
  object  app_id extends StringField(this,1024)
  object name extends StringField(this,1024)
  object description extends StringField(this,1024)
  object status extends StringField(this,1024)
  object note extends StringField(this,1024)
  object created_by extends StringField(this,1024)
  object created_date extends LongField(this)
  object modified_by extends StringField(this,1024)
  object modified_date extends LongField(this)


}
object Functions extends Functions with MongoMetaRecord[Functions] {
  override def collectionName = "Functions"

  override def mongoIdentifier = UsersDb

  def searh(q: List[String]): JValue = {
    var pageIndex: Int = 1
    var pageSize: Int = 5
    var id = ""
    var parent_id = ""
    var app_id = ""
    var name = ""
    var status = ""
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
        } else if (paramName.toLowerCase.equals("name")) {
          name = a.toString
        } else if (paramName.toLowerCase.equals("status")) {
          status = a.toString
        } else if (paramName.toLowerCase.equals("parent_id")) {
          parent_id = a.toString
        } else if (paramName.toLowerCase.equals("app_id")) {
          app_id = a.toString
        } else if (paramName.toLowerCase.equals("order_by")) {
          orderby = a.toString
        }
      }
    }
    if (!id.isEmpty && id != "") {
      jmap += "_id" -> id
    }
    if (!name.isEmpty && name != "") {
      jmap += "name" -> name
    }
    if (!status.isEmpty && status != "") {
      jmap += "status" -> status
    }
    if (!parent_id.isEmpty && parent_id != "") {
      jmap += "parent_id" -> parent_id
    }
    if (!app_id.isEmpty && app_id != "") {
      jmap += "app_id" -> app_id
    }

    if (!orderby.isEmpty && orderby != "") {
      order = (orderby -> -1)
    }
    val db = Functions.findAll(jmap, order, Skip(pageSize * (pageIndex - 1)), Limit(pageSize))
    val count = Functions.count(qry)

    Message.returnMassage("function", "0", "Success", db.map(_.asJValue), count)
  }

  def insert(q: JValue): JValue = {
    val jsonmap: Map[String, String] = q.values.asInstanceOf[Map[String, String]]
    val id = UUID.randomUUID().toString
    var parent_id = ""
    var app_id = ""
    var name = ""
    var description = ""
    var status = ""
    var note = ""
    var created_by = ""
    var created_date = System.currentTimeMillis() / 1000
    var modified_by = ""
    var modified_date = System.currentTimeMillis() / 1000

    for ((key, value) <- jsonmap) {
      if (key.toString.equals("name")) {
        name = value
      } else if (key.toString.equals("description")) {
        description = value
      } else if (key.toString.equals("status")) {
        status = value
      } else if (key.toString.equals("note")) {
        note = value
      } else if (key.toString.equals("created_by")) {
        created_by = value
      }else if (key.toString.equals("parent_id")) {
        parent_id = value
      } else if (key.toString.equals("app_id")) {
        app_id = value
      }


    }
    if (name.isEmpty || name == "") {
      return Message.returnMassage("insertFunction", "1", "Name must be exist", ("" -> ""))
    }
    if (status.isEmpty || status == "") {
      return Message.returnMassage("insertFunction", "2", "Status must be exist", ("" -> ""))
    }

    val application = Functions.createRecord.id(id).created_by(created_by).created_date(created_date).description(description)
      .modified_by(created_by).modified_date(modified_date).name(name).note(note).status(status).app_id(app_id).parent_id(parent_id).save(true)

    Message.returnMassage("insertFunction", "0", "Success", application.asJValue)

  }

  def update(q: JValue): JValue = {
    val jsonmap: Map[String, String] = q.values.asInstanceOf[Map[String, String]]
    var qry1: Map[String, String] = Map()
    var modified_date = System.currentTimeMillis() / 1000
    var id = ""

    //    val bu = QueryBuilder.start("_id").is("55cc53aae4b0fb6acad9a144").get
//    val avc = Functions.findAll("_id" -> "a468451b-5faf-4c14-b389-bc1898dcaa87")
//
//    println(avc.size)
    for ((key, value) <- jsonmap) {
      if (key.toString.equals("id")) {
        id = value.toString

      }
      else if (key.toString.equals("name")) {
        if (value.isEmpty || value == "") {
          return Message.returnMassage("updateFunction", "1", "Name must be exist", ("" -> ""))
        }
        qry1 += key -> value
      } else if (key.toString.equals("description")) {

        qry1 += key -> value
      } else if (key.toString.equals("status")) {

        if (value.isEmpty || value == "") {
          return Message.returnMassage("updateFunction", "2", "Status must be exist", ("" -> ""))
        }
        qry1 += key -> value
      } else if (key.toString.equals("note")) {
        qry1 += key -> value
      } else if (key.toString.equals("modified_by")) {
        qry1 += key -> value
      }else if (key.toString.equals("app_id")) {
        qry1 += key -> value
      } else if (key.toString.equals("parent_id")) {
        qry1 += key -> value
      }


    }
    qry1 += "modified_date" -> modified_date.toString

    if (id.isEmpty || id == "") {
      return Message.returnMassage("updateFunction", "3", "Id must be exist", ("" -> ""))
    }
    val count = Functions.findAll("_id" -> id)
    if (count.size == 0) {
      return Message.returnMassage("updateFunction", "4", "Function not found", ("" -> ""))
    }


    Functions.update(("_id" -> id), ("$set" -> qry1))
    val application = Functions.findAll("_id" -> id)

    Message.returnMassage("updateFunction", "0", "Success", application(0).asJValue)

  }

  def delete(q: String): JValue = {
    Functions.delete(("_id" -> q))
    Message.returnMassage("deleteFunction", "0", "Success", ("" -> ""))
  }
}