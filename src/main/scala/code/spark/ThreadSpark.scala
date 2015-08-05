package code.spark

import com.redis._
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by phong on 8/5/2015.
 */
object ThreadSpark {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").setAppName("CamusApp")
    val sc = new SparkContext(conf)
    val hiveContext = new org.apache.spark.sql.hive.HiveContext(sc)
    val df = hiveContext.read.json("hdfs://10.15.171.36:54310/home/phonghh/project/demo/camusDisk/topics/Scoring/hourly/*/*/*/*")
    df.registerTempTable("HDFS")

    RangeScoring("cd602b77-b570-4a56-8590-eb65e55b8210", hiveContext)
    TopBotOption("d848e3f9-9ae6-4c46-ba46-62adb892e94d", hiveContext)
    TopBotOption("878578e5-c9f4-430e-a129-446eaa69b374", hiveContext)
  }

  def RangeScoring(ModelId : String, hiveContext:HiveContext) = {
    val query = hiveContext.sql("SELECT rating_code, rating_status, COUNT(scoring) application_count, SUM(scoring)/COUNT(scoring) TB FROM HDFS WHERE rate.modelid = '" + ModelId + "' GROUP BY rating_code, rating_status ORDER BY TB")
    val a = query.toJSON.collect()
    val r = new RedisClient("10.15.171.41", 6379)
    r.del("Spark-RangeScoring-" + ModelId)
    for (x <- a ){
      r.rpush("Spark-RangeScoring-" + ModelId, x)
    }
  }

  def TopBotOption(factorOptionId : String, hiveContext:HiveContext) = {
    val queryTop = hiveContext.sql("SELECT scoring, rating_code, customer_name, part.factor_option_name FROM HDFS LATERAL VIEW explode(resultin) resultinable AS part WHERE part.factor_option_id = '" + factorOptionId + "' ORDER BY scoring LIMIT 5").toJSON.collect()
    val queryBot = hiveContext.sql("SELECT scoring, rating_code, customer_name, part.factor_option_name FROM HDFS LATERAL VIEW explode(resultin) resultinable AS part WHERE part.factor_option_id = '" + factorOptionId + "' ORDER BY scoring DESC LIMIT 5").toJSON.collect()

    val r = new RedisClient("10.15.171.41", 6379)
    r.del("Spark-TopBotOption-Top-" + factorOptionId)
    for (x <- queryTop) {
      r.rpush("Spark-TopBotOption-Top-" + factorOptionId, x)
    }
    r.del("Spark-TopBotOption-Bot-" + factorOptionId)
    for (x <- queryBot) {
      r.lpush("Spark-TopBotOption-Bot-" + factorOptionId, x)
    }
  }
}
