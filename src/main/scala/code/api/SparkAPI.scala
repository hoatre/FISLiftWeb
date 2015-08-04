package code.api

import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.json._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._
import org.apache.spark._

/**
 * Created by phong on 7/29/2015.
 */


object SparkAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(SparkAPI)
  }

  val conf = new SparkConf().setMaster("local[*]").setAppName("CamusApp")
  val sc = new SparkContext(conf)
  val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
  val df = hiveContext.read.json("hdfs://10.15.171.36:54310/home/phonghh/project/demo/camusDisk/topics/Scoring/hourly/*/*/*/*")
  df.registerTempTable("HDFS")

  def ScoringRange(q: JValue) : JValue={
    val mess = code.common.Message.CheckNullReturnMess(q, List("modelId"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values
      var year : String = "*"
      if(json.exists(j => j._1.toString.equals("year"))){
        year = json.apply("year").toString
      }
      var month : String = "*"
      if(json.exists(j => j._1.toString.equals("month"))){
        month = json.apply("month").toString
      }
      var day : String = "*"
      if(json.exists(j => j._1.toString.equals("day"))){
        day = json.apply("day").toString
      }
      var hour : String = "*"
      if(json.exists(j => j._1.toString.equals("hour"))){
        hour = json.apply("hour").toString
      }

//      val df = hiveContext.read.json("hdfs://10.15.171.36:54310/home/phonghh/project/demo/camusDisk/topics/Scoring/hourly/" + year + "/" + month + "/" + day + "/" + hour)
//      df.registerTempTable("HDFS")
      var timeNow = System.currentTimeMillis()

      val query = hiveContext.sql("SELECT rating_code, rating_status, COUNT(scoring) application_count, SUM(scoring)/COUNT(scoring) TB FROM HDFS WHERE rate.modelid = '" + json.apply("modelId").toString + "' GROUP BY rating_code, rating_status ORDER BY TB")

      println("sau select : " + (System.currentTimeMillis() - timeNow))
      timeNow = System.currentTimeMillis()

      return code.common.Message.returnMassage("ScoringRange", "0", "SUCCESS", query.toJSON.collect().toList.map(m =>parse(m)))
    }else
      return code.common.Message.returnMassage("ScoringRange", "3", mess, null)
  }

  def TopBot(q: JValue) : JValue={
    val mess = code.common.Message.CheckNullReturnMess(q, List("factorOptionId"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values
      var year : String = "*"
      if(json.exists(j => j._1.toString.equals("year"))){
        year = json.apply("year").toString
      }
      var month : String = "*"
      if(json.exists(j => j._1.toString.equals("month"))){
        month = json.apply("month").toString
      }
      var day : String = "*"
      if(json.exists(j => j._1.toString.equals("day"))){
        day = json.apply("day").toString
      }
      var hour : String = "*"
      if(json.exists(j => j._1.toString.equals("hour"))){
        hour = json.apply("hour").toString
      }

//      val df = hiveContext.read.json("hdfs://10.15.171.36:54310/home/phonghh/project/demo/camusDisk/topics/Scoring/hourly/" + year + "/" + month + "/" + day + "/" + hour)
//      df.registerTempTable("HDFS")
//      hiveContext.sql("CREATE TABLE IF NOT EXISTS HDFS (key INT, value STRING)")
//      hiveContext.sql("LOAD DATA LOCAL INPATH 'hdfs://10.15.171.36:54310/home/phonghh/project/demo/camusDisk/topics/Scoring/hourly/" + year + "/" + month + "/" + day + "/" + hour + "' INTO TABLE HDFS")
//      val query = hiveContext.sql("SELECT scoring FROM HDFS WHERE model._id = '" + json.apply("modelId").toString + "' AND resultin.factor_option_id LIKE '" + json.apply("factorOptionId").toString + "'")
      val queryTop = hiveContext.sql("SELECT scoring, rating_code, customer_name, part.factor_option_name FROM HDFS LATERAL VIEW explode(resultin) resultinable AS part WHERE part.factor_option_id = '" + json.apply("factorOptionId").toString + "' ORDER BY scoring LIMIT 5")
      val queryBot = hiveContext.sql("SELECT scoring, rating_code, customer_name, part.factor_option_name FROM HDFS LATERAL VIEW explode(resultin) resultinable AS part WHERE part.factor_option_id = '" + json.apply("factorOptionId").toString + "' ORDER BY scoring DESC LIMIT 5")
      var bacTop : List[String] = List()

      var bac = List({"Top" -> queryTop.toJSON.collect().toList.map(m=>parse(m))}, {"Bot" -> queryBot.toJSON.collect().toList.map(m=>parse(m))})

      return code.common.Message.returnMassage("ScoringRange", "0", "SUCCESS", bac)
    }else
      return code.common.Message.returnMassage("ScoringRange", "3", mess, null)
  }

  serve {

    case "spark" :: "scoringrange" :: Nil Options _ => {
      "OK" -> "200"
    }: JValue
    case "spark" :: "scoringrange" :: Nil JsonPost json -> request => ScoringRange(json)

    case "spark" :: "topbot" :: Nil Options _ => {
      "OK" -> "200"
    }: JValue
    case "spark" :: "topbot" :: Nil JsonPost json -> request => TopBot(json)
  }
}
