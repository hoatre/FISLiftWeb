package code.rest

import java.util.Properties
import java.util.concurrent.{Callable, ExecutorService, Executors, FutureTask}

import code.common.{Utils, Message}
import code.model._
import com.mongodb.{BasicDBObject, DBObject, QueryBuilder}
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import net.liftweb.common.Full
import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.{JArray, JValue, _}
import net.liftweb.json.JsonDSL.{seq2jvalue, _}
import net.liftweb.mongodb.{Skip, Limit}
import net.liftweb.util.Props
import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer}
import org.bson.types.ObjectId

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by bacnv on 7/15/15.
 */
object ScoreResultAPI extends RestHelper{

//  val PROPSNAME = "code.scoringresultapi.props"
//  Props.whereToLook = () => Utils.propsWheretoLook(PROPSNAME)

  def init(): Unit = {
    LiftRules.statelessDispatch.append(ScoreResultAPI)

    //    LiftRules.dispatch.append(MyRest) // stateful — associated with a servlet container session
    //    LiftRules.statelessDispatchTable.append(MyRest) // stateless — no session created
  }


  serve {
    case "scoreresult"::"customer" :: Nil JsonGet req => getCustomer()

    case "scoreresult"::"customer" :: q:: Nil JsonGet req => getCustomerbyid(q)
    case "scoreresult"::"result" :: Nil JsonGet req => getResult()

    case "scoreresult"::"result" :: q:: Nil JsonGet req => getResultid(q)

    case "scoreresult" :: Nil JsonPost json-> request => scoreresult(json)

    case "scoreresult"  :: Nil Options _ => OkResponse()


  }
  def getResult(): JValue ={

    val qry = QueryBuilder.start().get()
    val db = ScoringResult.findAll(qry,new BasicDBObject("timestamp",-1),Skip(0),Limit(50))
    var list : List[JValue] = List()

    for(x <- db){
      list = list ::: List({("_id" -> x.id.toString())} :JValue)
    }

    Message.returnMassage("getresult","0","Success",list.distinct,list.distinct.size)

  }
  def getResultid(q:String) : JValue={
    val db = ScoringResult.findAll(("_id" -> ("$oid" ->q)))
    if(db.size == 1){
     val Full(dbone) = ScoringResult.find(("_id" -> ("$oid" ->q)))
      return Message.returnMassage("getresult","0","Success",dbone.asJValue,db.size)
    }

    Message.returnMassage("getresult","1","Found many item",db.map(_.asJValue),db.size)

  }

  def getCustomer(): JValue ={

    val db = ScoringResult.findAll(("_id" -> ("$ne" -> "fdsd")),Skip(0),Limit(50))
    var list : List[JValue] = List()

    for(x <- db){
      list = list ::: List({("customer_id" -> x.customer_id.toString()) ~ ("customer_name" -> x.customer_name.toString())} :JValue)
    }

    Message.returnMassage("getcustomer","0","Success",list.distinct,list.distinct.size)

  }
  def getCustomerbyid(q:String) : JValue={
    val db = ScoringResult.findAll(("customer_id" -> ("$oid" ->q)))

    Message.returnMassage("getcustomer","0","Success",db.map(_.asJValue),db.size)

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

          if(DBquery.size >0 ) {


            if ((DBquery.map(_.asJValue) \ "codein").isInstanceOf[JArray]) {
              val listCodein: List[codeIN] = DBquery(0).codein.value

              val listCodeinsort = listCodein.sortWith(_.scorefrom.toString().toDouble < _.scoreto.toString().toDouble)

              val x = 0
              for (x <- 0 to listCodeinsort.size - 1) {
                val scoreform = listCodeinsort(x).scorefrom.toString().toDouble
                val scoreto = listCodeinsort(x).scoreto.toString().toDouble

                if ((scoreform <= scoreresult && scoreresult < scoreto) || (x == listCodeinsort.size - 1 && scoreto == scoreresult)) {
                  coderesul = listCodeinsort(x).code.toString()
                  codestatus = listCodeinsort(x).status.toString()

                  msg = Message.returnMassage("scoreResult", "0", "No error"
                    , {
                      ("Score" -> scoreresult) ~ ("Rating" -> coderesul) ~ ("Status" -> codestatus)
                    }: JValue, 1)

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
            saveScoreResult(null, jsonmap.values.apply("modelid").toString, Option(jsonmap.values.apply("custumer_name")).getOrElse(null).toString, scoreresult, coderesul, codestatus, lista)

          }


       return msg
      }})

    executor.execute(future)
   val mes = future.get()
    executor.shutdown();
    return  mes
  }

  def saveScoreResult(session:ObjectId, modelid :String,custumer_name:String, scoring :Double,ratingCode :String,ratingStatus :String,list : List[resultIN])(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    var ses = session
    if (session == null){
       ses = ObjectId.get()
    }
    val customer_id  = ObjectId.get


    val db = ModelInfo.findAll("_id" -> modelid)
    if(db.size == 1) {
      val result = ScoringResult.createRecord.id(ObjectId.get).session(ses).modelid(modelid).model_name(db(0).name.toString()).customer_id(customer_id).customer_name(ObjectId.get().toString).scoring(scoring).rating_code(ratingCode).rating_status(ratingStatus).resultin(list).timestamp((System.currentTimeMillis()/1000)).factor(Factor.findAll("ModelId" -> modelid))
        .model(ModelInfo.find("_id" -> modelid)).rate(Rating.find("modelid" -> modelid)).save

      val props = new Properties()
      props.put("metadata.broker.list",Props.props.apply("metadata.broker.list"))
      props.put("serializer.class",Props.props.apply("serializer.class"))
      props.put("producer.type",Props.props.apply("producer.type"))
      props.put("queue.enqueue.timeout.ms",Props.props.apply("queue.enqueue.timeout.ms"))
      props.put("batch.num.messages",Props.props.apply("batch.num.messages"))
      props.put("compression.codec",Props.props.apply("compression.codec"))
//      val config = new ProducerConfig(props)
//      val producer = new Producer[String, String](config)
      val producer =  new KafkaProducer[String,String](props)
      val data = new ProducerRecord[String, String](Props.props.apply("scoring.topic"), result.asJSON.toString())
      producer.send(data)
      producer.close()
    }
  }

}
