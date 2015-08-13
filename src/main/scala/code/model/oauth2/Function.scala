package code.model.oauth2

import bootstrap.liftweb.UsersDb
import code.common.Message
import com.mongodb.QueryBuilder
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb.{Limit, Skip}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{LongField, StringField}
import org.bson.types.ObjectId
import net.liftweb.mongodb.BsonDSL._
/**
 * Created by bacnv on 13/08/2015.
 */
class Function private () extends MongoRecord[Function] with StringPk[Function] {

  override def meta = Function

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
object Function extends Function with MongoMetaRecord[Function] {
  override def collectionName = "Functions"

  override def mongoIdentifier = UsersDb
//  def searh(q:List[String]) : JValue= {
//    var pageIndex: Int = 1
//    var pageSize: Int = 5
//    var id =""
//    var name =""
//    var status =""
//    var orderby ="created_date"
//    val qry = QueryBuilder.start().get()
//    var qry1 : JObject = ("" -> "")
//    var order = ("created_date" -> -1)
//    for (req <- S.request.toList) {
//      for (paramName <- req.paramNames) {
//        val Full(a) = S.param(paramName)
//        if (paramName.toLowerCase.equals("pageindex")) {
//          pageIndex = a.toString.toInt
//        } else if (paramName.toLowerCase.equals("pagesize")) {
//          pageSize = a.toString.toInt
//        }else if(paramName.toLowerCase.equals("id")){
//          id = a.toString
//        } else if(paramName.toLowerCase.equals("name")){
//          name = a.toString
//        }else if(paramName.toLowerCase.equals("status")){
//          status = a.toString
//        }else if(paramName.toLowerCase.equals("order_by")){
//          orderby = a.toString
//        }
//      }
//    }
//    if(!id.isEmpty && id != ""){
//      if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("_id" -> id) else qry1 = ("_id" -> id)
//    }
//    if(!name.isEmpty && name != ""){
//      if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("name" -> name) else qry1 = ("name" -> name)
//    }
//    if(!status.isEmpty && status != ""){
//      if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("status" -> status) else qry1 = ("email" -> status)
//    }
//
//    if(!orderby.isEmpty && orderby != ""){
//      order = (orderby -> -1)
//    }
//    val db =Function.findAll(qry1,order,Skip(pageSize * (pageIndex - 1)), Limit(pageSize))
//    val count = Function.count(qry)
//
//    Message.returnMassage("application", "0", "Success", db.map(_.asJValue), count)
//  }
//  def insert(q:JValue) :  JValue= {
//    val jsonmap : Map[String,String] = q.values.asInstanceOf[Map[String,String]]
//    val id = ObjectId.get().toString
//    var  name = ""
//    var desciption  = ""
//    var status  =""
//    var note  =""
//    var created_by  =""
//    var created_date  = System.currentTimeMillis()/1000
//    var modified_by  =""
//    var modified_date  = System.currentTimeMillis()/1000
//
//    for((key,value) <- jsonmap){
//      if(key.toString.equals("name")){
//        name = value
//      }else  if(key.toString.equals("desciption")){
//        desciption = value
//      } else if(key.toString.equals("status")){
//        status =  value
//      }else if(key.toString.equals("note")){
//        note =  value
//      }  else if(key.toString.equals("created_by")){
//        created_by = value
//      }
//
//
//    }
//    if(name.isEmpty || name == "" ){
//      return Message.returnMassage("insertApplication","1","Name must be exist",("" -> ""))
//    }
//    if(status.isEmpty || status == ""){
//      return Message.returnMassage("insertApplication","2","Status must be exist",("" -> ""))
//    }
//
////    val application= Function.createRecord.id(id).created_by(created_by).created_date(created_date).desciption(desciption)
////      .modified_by(created_by).modified_date(modified_date).name(name).note(note).status(status).save(true)
//
//    Message.returnMassage("insertApplication","0","Success",application.asJValue)
//
//  }
//
//  def update(q:JValue) :  JValue= {
//    val jsonmap : Map[String,String] = q.values.asInstanceOf[Map[String,String]]
//    var qry1 : JObject = ("" -> "")
//    var  name = ""
//    var desciption  = ""
//    var status  =""
//    var note  =""
//    var created_by  =""
//    var created_date  = System.currentTimeMillis()/1000
//    var modified_by  =""
//    var modified_date  = System.currentTimeMillis()/1000
//    var id =""
//
//    for((key,value) <- jsonmap){
//      if(key.toString.equals("id")){
//        id = value
//        if(id.isEmpty || id == "" ){
//          return Message.returnMassage("updateApplication","3","Id must be exist",("" -> ""))
//        }
//        if(Application.count("_id" -> id) == 0){
//          return Message.returnMassage("updateApplication","4","Application not found",("" -> ""))
//        }
//
//      }
//      else if(key.toString.equals("name")){
//        name = value
//        if(name.isEmpty || name == "" ){
//          return Message.returnMassage("updateApplication","1","Name must be exist",("" -> ""))
//        }
//        if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("name" -> name) else qry1 = ("name" -> name)
//      }else  if(key.toString.equals("desciption")){
//        desciption = value
//        if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("desciption" -> desciption) else qry1 = ("desciption" -> desciption)
//      } else if(key.toString.equals("status")){
//        status =  value
//        if(status.isEmpty || status == ""){
//          return Message.returnMassage("updateApplication","2","Status must be exist",("" -> ""))
//        }
//        if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("status" -> status) else qry1 = ("status" -> status)
//      }else if(key.toString.equals("note")){
//        note =  value
//        if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("note" -> note) else qry1 = ("note" -> note)
//      }  else if(key.toString.equals("modified_by")){
//        modified_by = value
//        if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("modified_by" -> note) else qry1 = ("modified_by" -> note)
//      }
//
//
//    }
//    if (qry1.equals(("" -> ""))) qry1 = qry1 ~ ("modified_date" -> modified_date) else qry1 = ("modified_date" -> modified_date)
//
//
//
//    Application.update(("_id" -> id),("$set" -> qry1))
//    val application =  Application.findAll("_id" -> id)
//
//    Message.returnMassage("updateApplication","0","Success",application(0).asJValue)
//
//  }
//  def delete(q:String):JValue={
//    Application.delete(("_id" -> q))
//    Message.returnMassage("deleteApplication","0","Success",("" -> ""))
//  }
}