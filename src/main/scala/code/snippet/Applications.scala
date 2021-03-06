package code.snippet

import java.util.UUID

import code.common.Message
import com.mongodb.QueryBuilder
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.json.JsonAST.{JObject, JValue}
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
import net.liftweb.util.{Helpers, Props}
import org.bson.types.ObjectId

import scala.collection.immutable.HashMap
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Random
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.S
import code.model, oauth2.{Applications => AppModel}

/**
 * Created by bacnv on 13/08/2015.
 */
object Applications {



  def getbyid(q:String):JValue ={
    val f = AppModel.find("_id" -> q)

    f match {
      case Full(s) => return  Message.returnMassage("application","0","success",s.asJValue)
      case _ => return  Message.returnMassage("application","1","Not found",""->"")
    }
  }

//  def getid(q:String):JValue={
//    var a = ("" ->"")
//    Future.successful(a = getbyid(q))
//
//  }

  def searh(q: List[String]): JValue = {
    println(S.uri)
    var pageIndex: Int = 1
    var pageSize: Int = 5
    var id = ""
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

    if (!orderby.isEmpty && orderby != "") {
      order = (orderby -> -1)
    }
    val db = AppModel.findAll(jmap, order, Skip(pageSize * (pageIndex - 1)), Limit(pageSize))
    val count = AppModel.count(jmap)

    Message.returnMassage("application", "0", "Success", db.map(_.asJValue), count)
  }

  def insert(q: JValue): JValue = {
    val jsonmap: Map[String, Any] = q.values.asInstanceOf[Map[String, Any]]
    val id = UUID.randomUUID().toString
    var name = ""
    var description = ""
    var status = ""
    var note = ""
    var created_by = ""
    var created_date = System.currentTimeMillis()/1000
    var modified_by = ""
    var modified_date = created_date
    var ordinal :Long = 100

    for ((key, value) <- jsonmap) {
      if (key.toString.equals("name")) {
        name = value.toString
      } else if (key.toString.equals("description")) {
        description = value.toString
      } else if (key.toString.equals("status")) {
        status = value.toString
      } else if (key.toString.equals("note")) {
        note = value.toString
      } else if (key.toString.equals("created_by")) {
        created_by = value.toString
      } else if(key.toString.equals("ordinal")){
        ordinal = name.toString.toLong
      }


    }
    if (name.isEmpty || name == "") {
      return Message.returnMassage("application", "1", "Name must be exist", ("" -> ""))
    }
    if (status.isEmpty || status == "") {
      return Message.returnMassage("application", "2", "Status must be exist", ("" -> ""))
    }

    val application = AppModel.createRecord.id(id).created_by(created_by).created_date(created_date).description(description)
      .modified_by(created_by).modified_date(modified_date).name(name).note(note).status(status).ordinal(ordinal).save(true)

    Message.returnMassage("application", "0", "Success", application.asJValue)

  }

  def update(q: JValue): JValue = {
    val jsonmap: Map[String, Any] = q.values.asInstanceOf[Map[String, Any]]
    var qry1: Map[String, String] = Map()
    var modified_date = System.currentTimeMillis()/1000
    var id = ""

    //    val bu = QueryBuilder.start("_id").is("55cc53aae4b0fb6acad9a144").get
//    val avc = AppModel.findAll("_id" -> "a468451b-5faf-4c14-b389-bc1898dcaa87")

//    println(avc.size)
    for ((key, value) <- jsonmap) {
      if (key.toString.equals("_id")) {
        id = value.toString

      }
      else if (key.toString.equals("name")) {
        if (value.toString.isEmpty || value == "") {
          return Message.returnMassage("application", "1", "Name must be exist", ("" -> ""))
        }
        qry1 += key -> value.toString
      } else if (key.toString.equals("description")) {

        qry1 += key -> value.toString
      } else if (key.toString.equals("status")) {

        if (value.toString.isEmpty || value == "") {
          return Message.returnMassage("application", "2", "Status must be exist", ("" -> ""))
        }
        qry1 += key -> value.toString
      } else if (key.toString.equals("note")) {
        qry1 += key -> value.toString
      } else if (key.toString.equals("modified_by")) {
        qry1 += key -> value.toString
      } else if (key.toString.equals("ordinal")) {
        qry1 += key -> value.toString
      }

    }
    qry1 += "modified_date" -> modified_date.toString

    if (id.isEmpty || id == "") {
      return Message.returnMassage("application", "3", "Id must be exist", ("" -> ""))
    }
    val count = AppModel.findAll("_id" -> id)
    if (count.size == 0) {
      return Message.returnMassage("application", "4", "Application not found", ("" -> ""))
    }

    AppModel.update(("_id" -> id), ("$set" -> qry1))
    val application = AppModel.findAll("_id" -> id)

    Message.returnMassage("application", "0", "Success", application(0).asJValue)

  }

  def delete(q: String): JValue = {
    AppModel.delete(("_id" -> q))
    Message.returnMassage("application", "0", "Success", ("" -> ""))
  }

  def getActive():JValue={
    val f = AppModel.findAll(("status" -> "active"),("ordinal,name" -> 1))
    return Message.returnMassage("application","0","Success",f.map(_.asJValue.remove{
    case JField("created_by", _) =>  true
    case JField("modified_by", _) =>  true
    case JField("created_date", _) =>  true
    case JField("modified_date", _) =>  true
    case _ => false
    }))

  }
}
