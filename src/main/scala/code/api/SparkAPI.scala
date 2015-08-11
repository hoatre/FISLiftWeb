package code.api

import com.redis.RedisClient
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._
import net.liftweb.util.Props

/**
 * Created by phong on 7/29/2015.
 */


object SparkAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(SparkAPI)
  }

  def PercentOptionOfFactor(q: JValue) : JValue={
    val mess = code.common.Message.CheckNullReturnMess(q, List("modelId"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values
      val r = new RedisClient(Props.props.apply("redis.host"), Props.props.apply("redis.port").toInt)

      var listCount = r.lrange("Spark-PercentOptionOfFactor-" + json.apply("modelId").toString, 0, r.llen("Spark-PercentOptionOfFactor-" + json.apply("modelId").toString).get.toInt - 1).get

      var data:List[Map[String,Any]] = List()
      if(listCount.size>0) {
        val listColor:List[String]=List("green","red","blue","yellow", "violet", "orange", "Amazon", "Apricot","pink")
        var listFactorName:List[String] = List()
        for(i <- 1 to listCount.size - 1){
          listFactorName=listFactorName:::List(parse(listCount(i).toList(0)).asInstanceOf[JObject].values.apply("factor_name").toString)
        }
        listFactorName=listFactorName.distinct
        var k = 0
        for (i <- 1 to listCount.size - 1) {

          var dataPoints: List[Map[String, Any]] = List()
          val factorOptionName = parse(listCount(i).toList(0)).asInstanceOf[JObject].values.apply("factor_option_name").toString
          val factorNameIN = parse(listCount(i).toList(0)).asInstanceOf[JObject].values.apply("factor_name").toString
          val count = parse(listCount(i).toList(0)).asInstanceOf[JObject].values.apply("application_count").toString

          for (factorName <- listFactorName) {

            if (factorName.equals(factorNameIN.toString)) {
              dataPoints = dataPoints ::: List(Map("y" -> count.toInt, "label" -> factorName))
            }
            else
              dataPoints = dataPoints ::: List(Map("y" -> 0, "label" -> factorName))


          }
          data = data ::: List(Map("type" -> "stackedBar100", "showInLegend" -> false, "name" -> factorOptionName, "dataPoints" -> dataPoints))
        }

        val bac: Map[String, Any] = Map(
          "modelName" -> parse(listCount(0).toList(0)).asInstanceOf[JObject].values.apply("modelName").toString
          ,
          "data" -> data
        )
        val v = net.liftweb.json.Extraction.decompose(bac)
        return code.common.Message.returnMassage("PercentOptionOfFactor", "0", "SUCCESS", v)
      }else
        return code.common.Message.returnMassage("PercentOptionOfFactor", "3", "null", null)
    }else
      return code.common.Message.returnMassage("PercentOptionOfFactor", "3", mess, null)
  }


  def ScoringRange(q: JValue) : JValue={
    val mess = code.common.Message.CheckNullReturnMess(q, List("modelId"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values
      val r = new RedisClient(Props.props.apply("redis.host"), Props.props.apply("redis.port").toInt)

      var a = r.lrange("Spark-RangeScoring-" + json.apply("modelId").toString, 0, r.llen("Spark-RangeScoring-" + json.apply("modelId").toString).get.toInt - 1).get
      return code.common.Message.returnMassage("ScoringRange", "0", "SUCCESS",a.map(m=>parse(m.get)))
    }else
      return code.common.Message.returnMassage("ScoringRange", "3", mess, null)
  }

  def TopBot(q: JValue) : JValue={
    val mess = code.common.Message.CheckNullReturnMess(q, List("factorOptionId"))
    if(mess.equals("OK")) {
      val json = q.asInstanceOf[JObject].values
      val r = new RedisClient(Props.props.apply("redis.host"), Props.props.apply("redis.port").toInt)
      if(r.llen("Spark-TopBotOption-Top-" + json.apply("factorOptionId").toString).get.toInt == 0 && r.llen("Spark-TopBotOption-Bot-" + json.apply("factorOptionId").toString).get.toInt == 0) {
        return code.common.Message.returnMassage("ScoringRange", "1", "null", null)
      }else {
        var top = r.lrange("Spark-TopBotOption-Top-" + json.apply("factorOptionId").toString, 0, r.llen("Spark-TopBotOption-Top-" + json.apply("factorOptionId").toString).get.toInt - 1).get
        var bot = r.lrange("Spark-TopBotOption-Bot-" + json.apply("factorOptionId").toString, 0, r.llen("Spark-TopBotOption-Bot-" + json.apply("factorOptionId").toString).get.toInt - 1).get

        val bac = List({
          "Top" -> top.map(m => parse(m.get))
        }, {
          "Bot" -> bot.map(m => parse(m.get))
        })
        return code.common.Message.returnMassage("ScoringRange", "0", "SUCCESS", bac)
      }
    }else
      return code.common.Message.returnMassage("ScoringRange", "3", mess, null)
  }

  serve {

    case "spark" :: "percentoptionoffactor" :: Nil Options _ => {
      "OK" -> "200"
    }: JValue
    case "spark" :: "percentoptionoffactor" :: Nil JsonPost json -> request => PercentOptionOfFactor(json)

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
