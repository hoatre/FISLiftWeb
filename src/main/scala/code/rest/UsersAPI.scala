package code.rest

import code.common.{Utils, Oauth2users}
import com.mongodb.{BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.http.{OkResponse, S, LiftRules}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model.Users
import net.liftweb.json.Printer._
import net.liftweb.json.JObject
import net.liftweb.json.Extraction
import net.liftweb.mongodb.JObjectParser
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import code.snippet.{Users => UsersSpet}
import org.apache.http.util.EntityUtils

import scala.concurrent.{Future, ExecutionContext}


/**
 * Created by bacnv on 7/7/15.
 */
object UsersAPI extends RestHelper {

  def init(): Unit = {
    LiftRules.dispatch.append(UsersAPI)

//    LiftRules.dispatch.append(MyRest) // stateful — associated with a servlet container session
//    LiftRules.statelessDispatchTable.append(MyRest) // stateless — no session created
  }




  serve {
    case "users" :: "getall"  :: Nil JsonGet req => UsersSpet.getall("getall","") : JValue



//
////    case "user" :: q   JsonGet req =>
////      UsersSpet.getuserbyusername(q) : JValue
//
//    case "user" :: "update_user" :: q:: Nil Post req -> request =>
//      for{
//        JString(id) <- (q \\ "id").toOpt
//        JString(username) <- (q \\ "username").toOpt} yield UsersSpet.getupdate(id,username)
//
//    case "user" :: "delete" :: Nil JsonPost json -> request =>
//      for{JString(id) <- (json \\ "_id").toOpt} yield UsersSpet.getDelete(id)
//
//    case "user" ::"add"  :: Nil JsonPost json->request => UsersSpet.insert(json)
//
//    case "user" ::"test"  :: Nil JsonPost json->request => UsersSpet.insertUser(json)
//
//    case "user" ::"update"  :: Nil JsonPost json->request => UsersSpet.update(json)
//
//    case "shout" :: q:: Nil Post req => {"abc" -> "abc"} :JValue
//
//    case "user" :: "accesstoken" :: Nil Options _ => OkResponse()
//    case "user" :: "accesstoken" :: Nil JsonGet req => Oauth2users.getAccessToken()
//
//    case "user" :: "checkapi" :: Nil Options _ => OkResponse()
//    case "user" :: "checkapi" :: Nil JsonGet req => Oauth2users.validate()

  }
  def test():Unit ={
    val url = "http://localhost:8080/application/id/khjhh"


    for(i <- 0 to 100000){
      futuretest(url,i)
    }


  }
  def futuretest(q: String, count:Int)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    val resp = Utils.httpget(q,null,null,null)
    val enttt = EntityUtils.toString(resp.getEntity)


//    val msg =  net.liftweb.json.parse(enttt)
    println(count)
  }


}

