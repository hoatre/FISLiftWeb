package code.common

import net.liftweb.json.JsonAST.{JObject, JValue}

/**
 * Created by phong on 7/16/2015.
 */
object Message {
  def ErrorFieldExixts(field : String) : String = {
    "Field " + field + " is not exists !"
  }
  def ErrorFieldNull(field : String) : String = {
    "Field " + field + " must be not null !"
  }
  def CheckNullReturnMess(q: JValue, listField : List[String]) : String = {
    val json = q.asInstanceOf[JObject].values
    for(field <- listField){
      if(json.exists(p => p._1 != field))
        return ErrorFieldExixts(field)
    }
    return "OK"
  }
}
