package code.rest

import java.util
import java.util.UUID

import code.common.{Message, Utils}
import code.model.oauth2.GoogleModel
import net.liftweb.http.{OkResponse, LiftRules, S}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.JValue
import net.liftweb.util.{A, Props}
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import org.bson.types.ObjectId

/**
 * Created by bacnv on 05/08/2015.
 */
object GoogleAPI extends RestHelper{
  def init(): Unit = {
    LiftRules.statelessDispatch.append(GoogleAPI)
  }

  serve{
    case "google" :: "code"  :: Nil JsonPost json -> request => getbycode(json)

    case "google" ::"test" ::Nil JsonGet req => test()
  }

  def getbycode(q:JValue): JValue ={
var msg :JValue = {"" -> ""}:JValue

    var code = ""
    val jsonmap : Map[String,String] = q.values.asInstanceOf[Map[String,String]]

    for((key,value) <- jsonmap){
      if(key.toString.equals("code")){
        code = value.toString
      }

    }

    val url = "https://www.googleapis.com/oauth2/v3/token"

    val post = new HttpPost(url)
    post.addHeader("Content-Type","application/x-www-form-urlencoded")

    val client = new DefaultHttpClient
//    val params = client.getParams
//    params.setParameter("foo", "bar")

    val nameValuePairs = new util.ArrayList[NameValuePair](1)
    nameValuePairs.add(new BasicNameValuePair("code",code))
    nameValuePairs.add(new BasicNameValuePair("client_id", Props.props.apply("google.client_id")))
    nameValuePairs.add(new BasicNameValuePair("client_secret", Props.props.apply("google.client_secret")))
    nameValuePairs.add(new BasicNameValuePair("redirect_uri","http://localhost:9000"))
    nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"))
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs))

    // send the post request
    val response = client.execute(post)

//    println("--- HEADERS ---")
//    response.getAllHeaders.foreach(arg => println(arg))
val entity = EntityUtils.toString(response.getEntity)
    if(!entity.isEmpty && entity.contains("id_token") && entity.contains("Bearer")){
      var id_token =""
      var access_token =""
      var token_type =""
     val all = (net.liftweb.json.parse(entity)).values.asInstanceOf[Map[String,Any]]
      println(all)
      for((key,value) <- all){
        if(key.toString.equals("id_token")){
          id_token = value.toString
        }else if(key.toString.equals("access_token")){
          access_token = value.toString
        }else if(key.equals("token_type")){
          token_type = value.toString
        }
      }
      val urlid_token = "https://www.googleapis.com/oauth2/v1/tokeninfo"
      val header : Map[String,String] = Map(("Conttent-Type" -> "application/x-www-form-urlencoded"))
      val namevalu : Map[String,String]= Map(("id_token" -> id_token))

      val resp = Utils.http(urlid_token,"post",header,null,namevalu)

      val enttt = EntityUtils.toString(resp.getEntity)





      msg =  net.liftweb.json.parse(enttt)
//      val testid = {"_id" -> ""}:JValue

//      msg =  msg.map(testid =>msg)

      val gg = msg.values.asInstanceOf[Map[String,Any]]


      val google : GoogleModel = GoogleModel.createRecord.id(ObjectId.get.toString).audience(gg.apply("audience").toString)
      .email(gg.apply("email").toString()).email_verified(gg.apply("email_verified").toString().toBoolean).expires_in(gg.apply("expires_in").toString().toLong)
      .issued_at(gg.apply("issued_at").toString().toLong).issued_to(gg.apply("issued_to").toString()).issuer(gg.apply("issuer").toString())
      .user_id(gg.apply("user_id").toString())

      GoogleModel.updateByUserAndClient(google)


    }else{
      msg = net.liftweb.json.parse(entity)
    }


msg
  }

def test():JValue={
  val list :JArray = null
  var jv :JString = null

  var lst : List[Map[String,Any]] = List()

  for(i <- 0 to 200){


    val map :Map[String,Any] = Map("abc"->3,"def" ->"AAA")

//   val a = compact(render(decompose(map)))

//   val a=  net.liftweb.json.compact(net.liftweb.json.render(net.liftweb.json.Extraction.decompose(map)))
//
//    println(a)
    lst = lst ::: List(map)

  }
//  compact()
//  println(lst)
//  jv =  net.liftweb.json.compact(net.liftweb.json.render(net.liftweb.json.Extraction.decompose(lst.map(_.toMap))))
val b = net.liftweb.json.Extraction.decompose(lst.map(_.toMap))


  println(jv)
//OkResponse()
return Message.returnMassage("test","0","ajhjs",b)
}

}
