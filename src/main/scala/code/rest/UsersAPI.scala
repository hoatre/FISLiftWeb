package code.rest

import code.model.Users
import com.mongodb.{BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL.{seq2jvalue, _}


/**
 * Created by bacnv on 7/7/15.
 */
object UsersAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.statelessDispatch.append(UsersAPI)
  }



  def getall(rest : String, q : String) : JValue = {

    var dbOject : List[Users] = List.empty

    if(!q.isEmpty) {
      val qry = QueryBuilder.start("user.username").is(q)
        .get

       dbOject = Users.findAll(qry)

    }else if (rest.equals("getall")){

       dbOject = Users.findAll

    }else{

      dbOject = Users.findAll
    }



    if(dbOject.isEmpty){

        "ERROR" -> "User not found" :JValue
      }

    else {"SUSCESS" -> dbOject.map(_.asJValue)} :JValue
  }

  def getupdate(_id :String, name : String) : JValue = {

    println(name)
    val dbo = BasicDBObjectBuilder.start
      .append("$inc", BasicDBObjectBuilder.start
      .append("user.name", name).get).get

    Users.update(("_id" -> "55910ffd3eade03c169e5550"), ("$set" -> ("user.name" -> name)))

    val a = { "SUSCESS" -> " Tao update roi nhe" }

    a:JValue
  }

 def getDelete(_id:String) :JValue ={

   Users.delete(("_id" -> _id))


   val a = { "SUSCESS" -> " Tao xoa roi nhe" }

   a:JValue
 }


  def greet(name: String) : JValue =
    "greeting" -> ("HELLO "+name.toUpperCase)

  serve {
    case "users" :: "getall"  :: Nil JsonGet req => getall("getall","") : JValue

    case "users" :: "getbyusername" :: q :: Nil JsonGet req => getall("getbyusername",q) : JValue

    case "users" :: "update" :: Nil JsonPost json -> request =>
      for{JString(username) <- (json \\ "username").toOpt} yield getupdate("",username)

//    case "users" :: "delete" :: Nil JsonPost json -> request =>
//      for{JString(id) <- (json \\ "id").toOpt} yield getDelete(id)

    case "users" :: "delete" :: id :: Nil JsonDelete req => getDelete(id)

    case "shout" :: Nil JsonPost json->request =>
      for { JString(name) <- (json \\ "name").toOpt }
        yield greet(name)
  }
}

