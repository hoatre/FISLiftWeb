package code.rest

import java.util.UUID

import code.common.{Message}
import net.liftweb.common.Full
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.http.{LiftRules, OkResponse}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.model.{ActiveTable}
import org.bson.types.ObjectId

/**
 * Created by bacnv on 06/08/2015.
 */
object ActiveTableAPI extends RestHelper{
  def init(): Unit = {
    LiftRules.statelessDispatch.append(ActiveTableAPI)
  }

  def update(q:JValue):JValue={

    val mapjson = q.values.asInstanceOf[Map[String,String]]
    var modelid = ""
    var objectId : String = ""
//    var check :Int = 1

    val db = ActiveTable.findAll
    if(db.size == 0){
//      check = 1
      val objid = ObjectId.get()
      objectId = objid.toString
      ActiveTable.createRecord.id(objid).save
    }else{
      objectId = db(0).id.toString()
    }
    var lista: Map[String, String] = Map()
    for((key,value) <- mapjson){
      if(key.toString.equals("modelid")){
        ActiveTable.update(("_id" -> ("$oid" ->  objectId)),("$set" -> (key.toString -> value.toString)))
      }
    }

    val Full(dbnew) = ActiveTable.find("_id" -> ("$oid" -> objectId))
  return Message.returnMassage("activetable","0","Success",dbnew.asJValue)
  }
  def getall(): JValue ={
    val dbnew = ActiveTable.findAll
    if(dbnew.size == 0){
      return Message.returnMassage("activetable","1","Not found",null)
    }

   return Message.returnMassage("activetable","0","Success",dbnew(0).asJValue)
  }
  serve{
    case "activetable" :: Nil JsonGet req => getall()

    case "activetable" :: Nil Options _ => OkResponse()

    case "activetable" :: Nil JsonPost json -> request => update(json)

  }


}
