package code.snippet

import java.io.{FileInputStream, FileOutputStream, File}
import java.util.concurrent.{Callable, FutureTask, Executors, ExecutorService}

import code.rest.ScoreResultAPI
import com.github.tototoshi.csv._
import java.util.{Properties, UUID}

import code.common.{Utils, Message}
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import net.liftweb.common.{Full, Box}
import net.liftweb.http._
import net.liftweb.http.rest.RestHelper
import net.liftweb.json._
import net.liftweb.json.JsonAST.JArray
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.JsonAST.JValue


import com.mongodb.{BasicDBObject, BasicDBObjectBuilder, QueryBuilder}
import net.liftweb.http.rest.RestHelper
import bootstrap.liftweb._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import code.snippet._
import code.model._
import net.liftweb.json.Printer._
import net.liftweb.mongodb.{Limit, Skip, JObjectParser}
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import net.liftweb.util.{Helpers, Props}
import org.bson.types.ObjectId

import scala.collection.immutable.HashMap
import scala.concurrent.{Future, ExecutionContext}
import scala.util.Random
import net.liftweb.http.provider.HTTPRequest
import net.liftweb.http.S

/**
 * Created by bacnv on 7/23/15.
 */


object CsvModule {
//  val PROPSNAME = "code.snippet.csvmodel.props"
//  Props.whereToLook = () => Utils.propsWheretoLook(PROPSNAME)


  implicit object MyFormat extends DefaultCSVFormat {
    override val delimiter = '#'
  }

  def unapply(req: Req): Option[Req] = {

  req.contentType.filter(_ == "application/csv").map(_ => req)
}
def uploadCSV(q:String,req: Req) :JValue ={

//  val bfile : Array[Byte] = Array()
var str :JValue =   {"ddd" -> "ddd"} :JValue

  for(file <- req.uploadedFiles){
    str= readCSV(q,file)
  }


//  for(file <- req.body){
//    println(file.clone())
//  }

  str
}
  def fileResponse(p: String,q:String): Box[LiftResponse] = {

  val filename =  new File("/tmp/" +p+ q)
    if(filename.exists()){
      filename.delete()
    }

    val writer = CSVWriter.open(filename)

val statustype = if(p.equals("ok")) "0" else if(p.equals("fail")) "1" else "2"



    val db = CSVsave.findAll(("session" -> q.split('.')(0)) ~("statustype" -> statustype))

            var list: List[String] = List()
    if(statustype.equals("0")) {

      for (i <- 0 to db.size - 1) {


        list = List(db(i).customer.toString()) ::: List("score:" + db(i).score.toString()) ::: List("rating:" + db(i).rating.toString()) ::: List("status:" + db(i).score.toString())

        writer.writeRow(list)

      }
    }else if(statustype.equals("1")) {

      for (i <- 0 to db.size - 1) {


        list = List(db(i).customer.toString())

        writer.writeRow(list)

      }
    }

    writer.close()

    for {
      file <- Box !! filename
      input <- Helpers.tryo(new FileInputStream(file))
    } yield StreamingResponse(input, () => input.close,file.length,headers = Nil,cookies = Nil,200)
  }



  def search(q: String): JValue = {
    val dbok = CSVsave.count(("session" -> ("$oid" -> q)) ~ ("statustype" -> "0"))
    val dbfail = CSVsave.count(("session" -> ("$oid" -> q)) ~ ("statustype" -> "1"))
    val count = CSVsave.count("session" -> ("$oid" -> q))

   // val http =  CurrentReq.value.request.remoteAddress
 //   println(CurrentReq.value.request.remoteAddress +" "+ CurrentReq.value.request.remoteHost + " "+S.hostAndPath + " " +S.hostName )
   val f = new File(q + ".csv")
    val writer = CSVWriter.open(f)


    Message.returnMassage("uploadfile", "0", "No error", ("urlok" -> (S.hostAndPath+"/csv/download/ok/"+q)) ~ ("urlfail" -> (S.hostAndPath+"/csv/download/fail/"+q)), count,dbok,dbfail)
  }

  def writetoCSV(q: String): JValue = {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    val future = new FutureTask[Unit](new Callable[Unit]() {
      def call(): Unit = {
        val time = System.currentTimeMillis()
        println(time)

//        val count = ScoringResult.count(("modelid" -> q))

        implicit val xc: ExecutionContext = ExecutionContext.global

        val f = savefilecsv(q, 1)(xc)
        f.onComplete {
          case _ => println(System.currentTimeMillis() - time)
        }

      }
    })
    executor.execute(future)
    //    val mes = future.get()
    //    println(mes)
    executor.shutdown()


    {
      "OK" -> "OK"
    }: JValue
  }

  def readCSV(q:String,file : FileParamHolder) :JValue =  {
    if(!((file.mimeType.toString.equals("application/vnd.ms-excel")||file.mimeType.toString.equals("text/csv"))&&file.fileName.toLowerCase().contains(".csv"))){
      return Message.returnMassage("uploadcsv", "3", "File not allow", null, 0)
    }

    val filename = ObjectId.get().toString + ".csv"

    val out :FileOutputStream = new FileOutputStream(filename)


    out.write(file.file)
    out.close()


    val reader = CSVReader.open(filename, "UTF-8")



    val a = reader.toStream
    //    println(a)

    //    val it2 = a.iterator
    //    loop("Iterator2: ", it2.next, it2){
    //
    //    }
    val model_id = q
//    val list = a.head
//    val model_name: String = list(1)
//    val factor_name: String = list(2)
//    val factor_option_name: String = list(3)
    val session: ObjectId = ObjectId.get()
//
//    val check = ModelInfo.findAll("_id" -> model_id)
//    if (!(check.size > 0 && check(0).name.toString.equals(model_name))) {
//      return Message.returnMassage("uploadcsv", "1", "Error", null, 0)
//    }
//    //    val b = {("FactorOption" -> ("$elemMatch" -> ("FactorOptionName" -> factor_option_name))) ~ ("ModelId" -> model_id) ~ ("FactorName" -> factor_name)} : JValue
//
//    val qry = QueryBuilder.start("ModelId").is(model_id).and("FactorName").is(factor_name).and("FactorOption").elemMatch(new BasicDBObject("FactorOptionName", factor_option_name))
//      .get
//    val checkfactor = Factor.count(qry)
//
//    if (checkfactor < 1) {
//      return Message.returnMassage("uploadcsv", "2", "Factor not found", null, 0)
//    }
    implicit val xc: ExecutionContext = ExecutionContext.global
    val f = readCSVFuture(model_id, a,session)(xc)
    f.onComplete{
      case _ => {
        val th = new Thread(readCSVFutureAfter(model_id, a, session))
        th.start()
        Thread.sleep(3000)
        reader.close()

        if(new File(filename).exists()){
          new File(filename).delete()
        }

      }
    }

    Message.returnMassage("readfile", "0", "No error", ("session" -> (S.hostAndPath+"/csv/"+session.toString)), 1)

  }

  def readCSVFuture(model_id : String, stream : Stream[List[String]],session :ObjectId)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit]   = Future{


    val db = Factor.findAll("ModelId" -> model_id)
    val dbrating = Rating.findAll("modelid" -> model_id)
    val dbmodel = ModelInfo.findAll("_id" -> model_id)

//    var count = 0
    //    val session = ObjectId.get().toString
    stream foreach {
      x => getvalue(x, db, dbrating,dbmodel, session)
//        count += 1
//        println(count)
    }


    //    val portfolio =
    //      <portfolio>
    //        <stockss>
    //          <stock>AAPL</stock>
    //          <stock>AMZN</stock>
    //          <stock>GOOG</stock>
    //        </stocks>
    //        <reits>
    //          <reit>Super REIT 1</reit>
    //        </reits>
    //      </portfolio>
    //    scala.xml.XML.save("portfolio.xml", portfolio)

//    Future(Message.returnMassage("readfile", "0", "No error", ("session" -> session.toString), 1))
  }

  def getvalue(listString: List[String], db: List[Factor], rates: List[Rating], dbmodel :List[ModelInfo], session: ObjectId)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    var lista: List[resultIN] = List()
    var factor_name = ""
    var factor_option_name = ""
    var factor_option_score: Double = 0
    var scoreresult: Double = 0
    var j = 1
    var listDBCuoi: List[Factor] = List()
    var score: Double = 0
    var coderesul: String = null
    var codestatus: String = null
    var statustype : String = "1"

    //    if(listString == null){
    //      return
    //    }

    for (factor <- db) {
      if (factor.FactorOption.value.size != 0)
        listDBCuoi = listDBCuoi ::: List(factor)
    }

if(listString(1).toString.equals(dbmodel(0).name.toString())) {

  for (i <- 2 to listString.size - 1) {
    if (j == 1) {
      factor_name = listString(i).toString
    } else if (j == 2) {
      factor_option_name = listString(i).toString
    } else if (j == 3) {
      factor_option_score = listString(i).toString.toDouble
      for (x <- 0 to listDBCuoi.size - 1) {
        if (listDBCuoi(x).FactorName.toString().equals(factor_name)) {

          for (z <- listDBCuoi(x).FactorOption.value) {
            //                val j = z.asInstanceOf[JObject].values
            if (z.FactorOptionName.toString().equals(factor_option_name)) {
              score = z.Score.toString.toDouble

              score = score * (listDBCuoi(x).Weight.toString().toDouble / 100)
              for (path <- listDBCuoi(x).PathFactor.value) {
                score = score * (path.Weight.toString().toDouble / 100)
              }
              scoreresult = scoreresult + score
              statustype = "0"
              lista = lista ::: List(resultIN.createRecord.factor_id(listDBCuoi(x).id.toString()).factor_name(listDBCuoi(x).FactorName.toString()).
                factor_score(score).factor_option_id(z.FactorOptionId.toString()).factor_option_name(z.FactorOptionName.toString()).
                factor_option_score(z.Score.toString().toDouble))
            }
          }

        }


      }

      j = 0
    }
    j += 1

  }
}

    if (rates.size > 0) {
      scoreresult = (f"$scoreresult%1.2f").toDouble

      if ((rates.map(_.asJValue) \ "codein").isInstanceOf[JArray]) {
        val listCodein: List[codeIN] = rates(0).codein.value

        val listCodeinsort = listCodein.sortWith(_.scorefrom.toString().toDouble < _.scoreto.toString().toDouble)

        val x = 0
        for (x <- 0 to listCodeinsort.size - 1) {
          val scoreform = listCodeinsort(x).scorefrom.toString().toDouble
          val scoreto = listCodeinsort(x).scoreto.toString().toDouble

          if ((scoreform <= scoreresult && scoreresult < scoreto) || (x == listCodeinsort.size - 1 && scoreto == scoreresult)) {
            coderesul = listCodeinsort(x).code.toString()
            codestatus = listCodeinsort(x).status.toString()
          }


        }
      }
    }

    //    val msg = (("Customer"-> listString(0).toString) ~ ("Score" -> scoreresult) ~ ("Rating" -> coderesul) ~ ("Status" -> codestatus))


    saveResult(CSVsave.createRecord.session(session).customerid(ObjectId.get).customer(listString(0).toString).score(scoreresult).rating(coderesul).status(codestatus).statustype(statustype))
    //
//    ScoreResultAPI.saveScoreResult(session, db(0).ModelId.toString(), listString(0).toString, scoreresult, coderesul, codestatus, lista)


//    val result = ScoringResult.createRecord.id(ObjectId.get).session(session).modelid(dbmodel(0).id.toString()).model_name(dbmodel(0).name.toString()).customer_name(ObjectId.get().toString).scoring(scoreresult).rating_code(coderesul).rating_status(codestatus).resultin(lista).time_stamp(System.currentTimeMillis()).factor(Factor.findAll("ModelId" -> dbmodel(0).id.toString()))
//      .model(ModelInfo.find("_id" -> dbmodel(0).id.toString())).rate(Rating.find("modelid" -> dbmodel(0).id.toString()))
//
// println(result)
  }


  def readCSVFutureAfter (model_id : String, stream : Stream[List[String]],session :ObjectId)   = new Thread(){
    override def run {



      var time = System.currentTimeMillis()
      println(time)

      val db = Factor.findAll("ModelId" -> model_id)
      val dbrating = Rating.findAll("modelid" -> model_id)
      val dbmodel = ModelInfo.findAll("_id" -> model_id)
      val props = new Properties()

//      for(x <- Props.props){
//
//                println(x._1 + "  "+x._2)
//              }
//
//      println(Props.props.apply("serializer.class"))
      props.put("metadata.broker.list",Props.props.apply("metadata.broker.list"))
      props.put("serializer.class",Props.props.apply("serializer.class"))
      props.put("producer.type",Props.props.apply("producer.type"))
      props.put("queue.enqueue.timeout.ms",Props.props.apply("queue.enqueue.timeout.ms"))
      props.put("batch.num.messages",Props.props.apply("batch.num.messages"))
      props.put("compression.codec",Props.props.apply("compression.codec"))

//      for(x <- Props.props){
//
//        props.put(x._1,x._2)
//      }

//      props.put("metadata.broker.list", Props.props.apply("metadata.broker.list"))
//      //    props.put("zk.connect", "10.15.171.36:2181")
//      props.put("serializer.class", "kafka.serializer.StringEncoder")
//      props.put("producer.type", "async")
//      //    props.put("batch.size", "10000")
//      props.put("queue.enqueue.timeout.ms", "-1")
//      props.put("batch.num.messages", "200")
//      props.put("compression.codec", "1")
      //    props.put("queue.size", "10000")
      //    props.put("queue.time", "5000")
      val config = new ProducerConfig(props)
      val producer = new Producer[String, String](config)

//      var count = 0
      //    val session = ObjectId.get().toString
      var messages: List[KeyedMessage[String, String]] = List()
      stream foreach {
        x => getvalueafter(x, db, dbrating, dbmodel, session, producer)

//          count += 1
//          println(count)
      }
      println(System.currentTimeMillis() - time)

//      producer.send(messages : _*)
      producer.close()
      println(System.currentTimeMillis() - time)
    }
  }
  def getvalueafter(listString: List[String], db: List[Factor], rates: List[Rating], dbmodel :List[ModelInfo], session: ObjectId,producer:Producer[String, String]) : Unit = {
    var lista: List[resultIN] = List()
    var factor_name = ""
    var factor_option_name = ""
    var factor_option_score: Double = 0
    var scoreresult: Double = 0
    var j = 1
    var listDBCuoi: List[Factor] = List()
    var score: Double = 0
    var coderesul: String = null
    var codestatus: String = null
    var statustype:String = "1"

    //    if(listString == null){
    //      return
    //    }

    for (factor <- db) {
      if (factor.FactorOption.value.size != 0)
        listDBCuoi = listDBCuoi ::: List(factor)
    }

    if(listString(1).toString.equals(dbmodel(0).name.toString())) {
      for (i <- 2 to listString.size - 1) {
        if (j == 1) {
          factor_name = listString(i).toString
        } else if (j == 2) {
          factor_option_name = listString(i).toString
        } else if (j == 3) {
          factor_option_score = listString(i).toString.toDouble
          for (x <- 0 to listDBCuoi.size - 1) {
            if (listDBCuoi(x).FactorName.toString().equals(factor_name)) {

              for (z <- listDBCuoi(x).FactorOption.value) {
                //                val j = z.asInstanceOf[JObject].values
                if (z.FactorOptionName.toString().equals(factor_option_name)) {
                  score = z.Score.toString.toDouble

                  score = score * (listDBCuoi(x).Weight.toString().toDouble / 100)
                  for (path <- listDBCuoi(x).PathFactor.value) {
                    score = score * (path.Weight.toString().toDouble / 100)
                  }
                  scoreresult = scoreresult + score
                  statustype = "0"
                  lista = lista ::: List(resultIN.createRecord.factor_id(listDBCuoi(x).id.toString()).factor_name(listDBCuoi(x).FactorName.toString()).
                    factor_score(score).factor_option_id(z.FactorOptionId.toString()).factor_option_name(z.FactorOptionName.toString()).
                    factor_option_score(z.Score.toString().toDouble))
                }
              }

            }


          }

          j = 0
        }
        j += 1

      }
    }
    if (rates.size > 0) {
      scoreresult = (f"$scoreresult%1.2f").toDouble

      if ((rates.map(_.asJValue) \ "codein").isInstanceOf[JArray]) {
        val listCodein: List[codeIN] = rates(0).codein.value

        val listCodeinsort = listCodein.sortWith(_.scorefrom.toString().toDouble < _.scoreto.toString().toDouble)

        val x = 0
        for (x <- 0 to listCodeinsort.size - 1) {
          val scoreform = listCodeinsort(x).scorefrom.toString().toDouble
          val scoreto = listCodeinsort(x).scoreto.toString().toDouble

          if ((scoreform <= scoreresult && scoreresult < scoreto) || (x == listCodeinsort.size - 1 && scoreto == scoreresult)) {
            coderesul = listCodeinsort(x).code.toString()
            codestatus = listCodeinsort(x).status.toString()
          }


        }
      }
    }

    //    val msg = (("Customer"-> listString(0).toString) ~ ("Score" -> scoreresult) ~ ("Rating" -> coderesul) ~ ("Status" -> codestatus))


//    saveResult(CSVsave.createRecord.session(session).customer(listString(0).toString).score(scoreresult).rating(coderesul).status(codestatus))
    //
    //    ScoreResultAPI.saveScoreResult(session, db(0).ModelId.toString(), listString(0).toString, scoreresult, coderesul, codestatus, lista)

if(statustype.equals("0")) {
  val result = ScoringResult.id(ObjectId.get).session(session).modelid(dbmodel(0).id.toString()).model_name(dbmodel(0).name.toString()).customer_id(ObjectId.get).customer_name(ObjectId.get().toString).scoring(scoreresult).rating_code(coderesul).rating_status(codestatus)
    .resultin(lista).timestamp((System.currentTimeMillis() / 1000)).factor(db)
    .model(dbmodel(0)).rate(rates(0))

  //    val liststr : List[String] = List(result.asJSON.toString())


  //  val hhj = net.liftweb.json.compact(net.liftweb.json.render(result.asJValue))
  val data = new KeyedMessage[String, String](Props.props.apply("scoring.topic"), net.liftweb.json.compact(net.liftweb.json.render(result.asJValue)))
  //    data.copy()
      producer.send(data)
  //    println(hhj)

}
  }

  def saveResult(csv: CSVsave)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    csv.save
  }


  def savefilecsv(q: String, count: Int)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    var index = 1
    var page = 5
    val f = new File(q + ".csv")
    val writer = CSVWriter.open(f)
//    for (z <- 0 to count - 1) {
//
//      val skip = page * (index - 1)
//      if (z == count.toInt - 1 || skip == z) {
//        val db = ScoringResult.findAll(("modelid" -> q), Skip(skip), Limit(page))
//
//        var list: List[String] = List()
//        var listModel: List[List[String]] = List()
//        var x = 0
//
//        for (i <- 0 to db.size - 1) {
//          println(i)
//          val JArray(listop) = db(i).resultin.asJValue
//
//          //      println(listop)
//          list = list ::: List(db(i).customer_name.toString()) ::: List(db(i).model_name.toString())
//          for (j <- 0 to listop.size - 1) {
//            val a = listop(j).asInstanceOf[JObject].values
//            list = list ::: List(a.apply("factor_name").toString) ::: List(a.apply("factor_option_name").toString) ::: List(a.apply("factor_option_score").toString)
//
//
//            //          listModel = listModel ::: List(list)
//
//
//            //          }
//            //            x = 0
//          }
//          writer.writeRow(list)
//
//          list = List()
//          x += 1
//          println(i)
//        }
//        index += 1
//      }
//
//      println(z)
//    }

    val dbFind = Factor.findAll("ModelId" -> q)
    val dbmodel = ModelInfo.findAll("_id" -> q)
    var lista: List[String] = List()

    var i = 0
    for (i <- 0 to dbFind.size - 1) {

      //     for {
      //       JString(parent) <- (dbFind(i).asJValue \ "Parentid").toOpt
      //       item = parent.toString
      //     } yield {
      if (!lista.contains(dbFind(i).Parentid.toString())) {
        val listb: List[String] = List(dbFind(i).Parentid.toString())

        lista = lista ::: listb
        //       }

      }

    }
    val dbin = QueryBuilder.start("ModelId").is(q).and("_id").notIn(lista.toArray).get

    val db = Factor.findAll(dbin)
    for (x <-1 to 5000){
      var listwriter : List[String] = List()
      listwriter = listwriter ::: List(ObjectId.get().toString) ::: List(dbmodel(0).name.toString())
      for(i<-0 to db.size -1){

        listwriter = listwriter ::: List(db(i).FactorName.toString())
        val random :Int = new Random().nextInt(db(i).FactorOption.value.size)
        println(db(i).FactorOption.value.size + "/"+random)
        for(y <- 0 to db(i).FactorOption.value.size -1){
          if(y == random){
            listwriter = listwriter ::: List(db(i).FactorOption.value(y).FactorOptionName.toString())::: List(db(i).FactorOption.value(y).Score.toString())
          }

        }




      }
      writer.writeRow(listwriter)

    }

    writer.close()


  }

  //  def savefilecsv(list: List[List[String]],writer:CSVWriter)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
  //    val w = writer
  //    w.writeAll(list)
  //  }

}


