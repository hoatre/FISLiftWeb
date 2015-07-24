package code.rest

import java.util.UUID
import java.util.concurrent.{Callable, FutureTask, ExecutorService, Executors}

import code.common.Message
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
import code.model._
import net.liftweb.json.Printer._
import net.liftweb.mongodb.JObjectParser
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import org.bson.types.ObjectId

import scala.concurrent.{Future, ExecutionContext}

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


    val executor : ExecutorService  = Executors.newSingleThreadExecutor()

    val future = new FutureTask[JValue](new Callable[JValue]() {
      def call(): JValue = {
        var msg : JValue={("Score" -> "") ~ ("Rating"->"")} :JValue
        val listresul = (q \ "listresult")
        val jsonmap = q.asInstanceOf[JObject]

        var scoreresult : Double =0


        var lista : List[resultIN] = List()


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

            lista = lista ::: List(resultIN.createRecord.factor_id(factorid).factor_name(j.apply("factor_name").toString).factor_score(score).factor_option_score(j.apply("score").toString.toDouble).factor_option_id(Option(j.apply("factor_option_id").toString).getOrElse(null)).factor_option_name(j.apply("factor_option_name").toString))


          }
          scoreresult = (f"$scoreresult%1.2f").toDouble
          var coderesul :String = null
          var codestatus : String = null
          //
          msg = Message.returnMassage("scoreResult","1","Rating not existed"
            ,({("Score"-> f"$scoreresult%1.2f")~("Rating" -> "Not existed")~("Status" -> "Not existed")} :JValue),1)

          val qry = QueryBuilder.start("modelid").is(jsonmap.values.apply("modelid").toString).get

          val DBquery = Rating.findAll(qry)

          if(DBquery.size >0 ){


            if((DBquery.map(_.asJValue) \ "codein").isInstanceOf[JArray]){
              val listCodein: List[codeIN] =  DBquery(0).codein.value

              val listCodeinsort = listCodein.sortWith(_.scorefrom.toString().toDouble < _.scoreto.toString().toDouble)

              val x = 0
              for(x <-0 to listCodeinsort.size -1 ){
                val scoreform = listCodeinsort(x).scorefrom.toString().toDouble
                val scoreto = listCodeinsort(x).scoreto.toString().toDouble

                if((scoreform <= scoreresult && scoreresult < scoreto) ||( x == listCodeinsort.size -1 && scoreto == scoreresult)){
                  coderesul = listCodeinsort(x).code.toString()
                  codestatus = listCodeinsort(x).status.toString()

                  msg = Message.returnMassage("scoreResult","0","No error"
                    ,{("Score"-> scoreresult)~("Rating" -> coderesul)~("Status" -> codestatus)} :JValue,1)

                }
              }

              //            val JArray(rates) = (DBquery.map(_.asJValue) \ "codein")
              //            rates collect { case rate: JObject => rate } foreach getlist
              //
              //            def getlist(rate: JObject) = {
              //              var listb: List[codeIN] = List()
              //              val j = rate.asInstanceOf[JObject].values
              //              if (j.apply("scorefrom").toString.toDouble < scoreresult && scoreresult < j.apply("scoreto").toString.toDouble) {
              //
              //                coderesul = j.apply("code").toString
              //                codestatus = j.apply("status").toString
              //
              //                msg ={("Score"-> f"$scoreresult%1.2f")~("Rating" -> j.apply("code").toString)~("Status" -> j.apply("status").toString)} :JValue
              //
              //
              //              }
              //
              //
              //            }

            }


          }
          for(i <-0 to 5000) {
            saveScoreResult(null, jsonmap.values.apply("modelid").toString, Option(jsonmap.values.apply("custumer_name")).getOrElse(null).toString, scoreresult, coderesul, codestatus, lista)
          }
          }


       return msg
      }})

    executor.execute(future)
   val mes = future.get()
    executor.shutdown();
    return  mes
  }

  def saveScoreResult(session:String, modelid :String,custumer_name:String, scoring :Double,ratingCode :String,ratingStatus :String,list : List[resultIN])(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    var ses = session
    if (session == null || session.isEmpty){
       ses = ObjectId.get().toString
    }


    val db = ModelInfo.findAll("_id" -> modelid)
    if(db.size == 1) {
      ScoringResult.createRecord.id(ObjectId.get).session(ses).modelid(modelid).model_name(db(0).name.toString()).customer_name(ObjectId.get().toString).scoring(scoring).rating_code(ratingCode).rating_status(ratingStatus).resultin(list).time_stamp(System.currentTimeMillis()).factor(Factor.findAll("ModelId" -> modelid))
        .model(ModelInfo.find("_id" -> modelid)).rate(Rating.find("modelid" -> modelid)).save
    }
  }

}
