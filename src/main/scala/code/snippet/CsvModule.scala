package code.snippet

import java.io.File
import java.util.concurrent.{Callable, FutureTask, Executors, ExecutorService}

import com.github.tototoshi.csv._
import java.util.UUID

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
import net.liftweb.mongodb.{Limit, Skip, JObjectParser}
import net.liftweb.http.js.JsExp
import net.liftweb.json.JsonDSL.seq2jvalue
import org.bson.types.ObjectId

import scala.collection.immutable.HashMap
import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by bacnv on 7/23/15.
 */
object CsvModule {

  implicit object MyFormat extends DefaultCSVFormat {
    override val delimiter = '#'
  }

  def writetoCSV(q: String): JValue = {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    val future = new FutureTask[Unit](new Callable[Unit]() {
      def call(): Unit = {
        val time = System.currentTimeMillis()
        println(time)

        val count = ScoringResult.count(("modelid" -> q))

        implicit val xc: ExecutionContext = ExecutionContext.global

        val f = savefilecsv(q, count.toInt)(xc)
        f.onComplete {
          case _ => println(System.currentTimeMillis() - time)
        }

      }
    })
    executor.execute(future)
    //    val mes = future.get()
    //    println(mes)
    executor.shutdown();


    {
      "OK" -> "OK"
    }: JValue
  }

  def readCSV(q: String): JValue = {
    val reader = CSVReader.open(new File(q + ".csv"), "UTF-8")
    val a = reader.toStream
//    println(a)

//    val it2 = a.iterator
//    loop("Iterator2: ", it2.next, it2){
//
//    }
    val model_id = "c69f764e-d651-42ab-8046-b09e9e2c412e"
    val list = a.head
    val model_name :String = list(1)
    val factor_name : String = list(2)
    val factor_option_name : String = list(3)
    val session :ObjectId = ObjectId.get()

    val check = ModelInfo.findAll("_id" -> model_id)
    if(!(check.size > 0 && check(0).name.toString.equals(model_name))){
        return  Message.returnMassage("uploadcsv","1","Error",null,0)
    }
//    val b = {("FactorOption" -> ("$elemMatch" -> ("FactorOptionName" -> factor_option_name))) ~ ("ModelId" -> model_id) ~ ("FactorName" -> factor_name)} : JValue

    val qry = QueryBuilder.start("ModelId").is(model_id).and("FactorName").is(factor_name).and("FactorOption").elemMatch(new BasicDBObject("FactorOptionName", factor_option_name))
      .get
    val checkfactor = Factor.count(qry)

    if(checkfactor < 1){
      return  Message.returnMassage("uploadcsv","1","Factor not found",null,0)
    }

    val db = Factor.findAll("ModelId"-> model_id)
    val dbrating = Rating.findAll("modelid"-> model_id)

    var count = 0
    a foreach {
      x => getvalue(x,db,dbrating,session)
        count +=1
        println(count)
    }

    //    val portfolio =
    //      <portfolio>
    //        <stocks>
    //          <stock>AAPL</stock>
    //          <stock>AMZN</stock>
    //          <stock>GOOG</stock>
    //        </stocks>
    //        <reits>
    //          <reit>Super REIT 1</reit>
    //        </reits>
    //      </portfolio>
    //    scala.xml.XML.save("portfolio.xml", portfolio)

    {
      "OK" -> "OK"
    }: JValue
  }
  def getvalue(listString :List[String],db : List[Factor],rates : List[Rating],objid : ObjectId): Unit ={
    var lista : List[resultIN] = List()
    var factor_name =""
    var factor_option_name =""
    var factor_option_score :Double = 0
    var scoreresult  : Double =0
    var j = 1
    var listDBCuoi: List[Factor] = List()
    var score :Double = 0

    for (factor <- db) {
      if (factor.FactorOption.value.size != 0)
        listDBCuoi = listDBCuoi ::: List(factor)
    }


    for(i<- 2 to listString.size -1){
        if(j==1){
          factor_name = listString(i).toString
        }else if(j == 2){
          factor_option_name = listString(i).toString
        }else if(j == 3){
          factor_option_score = listString(i).toString.toDouble
          for(x <- 0 to listDBCuoi.size -1){
            if(listDBCuoi(x).FactorName.toString().equals(factor_name)){

              for(z <-  listDBCuoi(x).FactorOption.value){
//                val j = z.asInstanceOf[JObject].values
                if(z.FactorOptionName.toString().equals(factor_option_name)){
                  score = z.Score.toString.toDouble

                  score = score * (listDBCuoi(x).Weight.toString().toDouble/100)
                  for(path <- listDBCuoi(x).PathFactor.value){
                    score = score * (path.Weight.toString().toDouble/100)
                  }
                  scoreresult = scoreresult + score

                }
              }

            }


          }

          j = 0
        }
      j += 1

    }

  }

  def savefilecsv(q: String, count: Int)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
    var index = 1
    var page = 5
    val f = new File(q + ".csv")
    val writer = CSVWriter.open(f)
    for (z <- 0 to count - 1) {

      val skip = page * (index - 1)
      if (z == count.toInt - 1 || skip == z) {
        val db = ScoringResult.findAll(("modelid" -> q), Skip(skip), Limit(page))

        var list: List[String] = List()
        var listModel: List[List[String]] = List()
        var x = 0

        for (i <- 0 to db.size - 1) {
          println(i)
          val JArray(listop) = db(i).resultin.asJValue

          //      println(listop)
          list = list ::: List(db(i).customer_name.toString()) ::: List(db(i).model_name.toString())
          for (j <- 0 to listop.size - 1) {
            val a = listop(j).asInstanceOf[JObject].values
            list = list ::: List(a.apply("factor_name").toString) ::: List(a.apply("factor_option_name").toString) ::: List(a.apply("factor_option_score").toString)


//          listModel = listModel ::: List(list)





//          }
            //            x = 0
          }
          writer.writeRow(list)

          list = List()
          x += 1
        println(i)
        }
        index += 1
      }

      println(z)
    }

    writer.close()


  }

  //  def savefilecsv(list: List[List[String]],writer:CSVWriter)(implicit xc: ExecutionContext = ExecutionContext.global): Future[Unit] = Future {
  //    val w = writer
  //    w.writeAll(list)
  //  }

}
