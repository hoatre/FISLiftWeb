package code.rest

import java.util.UUID

import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json._
import net.liftweb.json.JsonAST.JArray
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonAST.JValue

import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.http.{S, LiftRules}
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model.{Factor, codeIN, Rating, Users}
import net.liftweb.json.Printer._
import net.liftweb.mongodb.JObjectParser
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue

/**
 * Created by bacnv on 7/15/15.
 */
object ScoreResultAPI extends RestHelper{

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ScoreResultAPI)

    //    LiftRules.dispatch.append(MyRest) // stateful — associated with a servlet container session
    //    LiftRules.statelessDispatchTable.append(MyRest) // stateless — no session created
  }


  serve {

    case "scoreresult" :: Nil JsonPost json-> request => scoreresult(json)

    case "scoreresult"  :: Nil Options _ => {"OK" -> "200"} :JValue


  }

  def scoreresult(q:JValue):JValue={

    val listresul = (q \ "listresult")
    val jsonmap = q.asInstanceOf[JObject]

    var scoreresult : Double =0
    var msg : JValue={("Score" -> "") ~ ("Rating"->"")} :JValue




    if(listresul.isInstanceOf[JArray]){
      val JArray(rates) = listresul
      rates collect { case rate: JObject => rate } foreach myOperation
      def myOperation(rate: JObject) = {
        val j = rate.asInstanceOf[JObject].values

        val factorid = j.apply("factorid").toString
        var score = j.apply("score").toString.toDouble


        val qry = QueryBuilder.start("_id").is(factorid).get

        val DBList = Factor.findAll(qry)

        var listDBCuoi : List[Factor] = List()

        for(factor <- DBList){
          if(factor.FactorOption.value.size != 0)
            listDBCuoi = listDBCuoi ::: List(factor)
        }

        for(factor <- listDBCuoi){

          score = score * (factor.Weight.toString().toDouble/100)
          for(path <- factor.PathFactor.value){
            score = score * (path.Weight.toString().toDouble/100)
          }

          scoreresult = scoreresult + score


        }

//        lista = lista ::: listb
      }
//
      msg ={("Score"-> scoreresult)~("Rating" -> "Not existed")~("Status" -> "Not existed")} :JValue
      val qry = QueryBuilder.start("modelid").is(jsonmap.values.apply("modelid").toString).get

      val DBquery = Rating.findAll(qry)

        if(DBquery.size >0 ){
          if((DBquery.map(_.asJValue) \ "codein").isInstanceOf[JArray]){
            val JArray(rates) = (DBquery.map(_.asJValue) \ "codein")
            rates collect { case rate: JObject => rate } foreach getlist

            def getlist(rate: JObject) = {
              var listb: List[codeIN] = List()
              val j = rate.asInstanceOf[JObject].values
              if (j.apply("scorefrom").toString.toDouble < scoreresult && scoreresult < j.apply("scoreto").toString.toDouble) {

                msg ={("Score"-> f"$scoreresult%1.2f")~("Rating" -> j.apply("code").toString)~("Status" -> j.apply("status").toString)} :JValue
              }


            }

          }


        }

    }

      msg

  }

}
