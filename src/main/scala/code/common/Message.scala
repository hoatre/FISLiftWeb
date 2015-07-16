package code.common

import net.liftweb.json.JsonAST.{JObject, JValue}

import scala.util.control.Breaks

/**
 * Created by phong on 7/16/2015.
 */
object Message {
  def ErrorFieldExixts(field : String) : String = {
    if(field.equals("OK"))
      "OK"
    else
      "Field " + field + " is not exists !"
  }
  def ErrorFieldNull(field : String) : String = {
    "Field " + field + " must be not null !"
  }
  def CheckNullReturnMess(q: JValue, listField : List[String]) : String = {
    val json = q.asInstanceOf[JObject].values
    var mess = ""
    val loop = new Breaks;
    loop.breakable {
      for (field <- listField) {
        if (json.exists(p => p._1 == field))
          mess = "OK"
        else {
          mess = ErrorFieldExixts(field)
          loop.break()
        }
      }
    }
    return ErrorFieldExixts(mess)
  }
}
