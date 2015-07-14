package code.rest

/**
 * Created by phong on 7/8/2015.
 */

import java.util.UUID

import code.model._
import com.mongodb.QueryBuilder
import net.liftweb.http.LiftRules
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._

object FactorAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(FactorAPI)
  }

  def getFactorJSON(): JValue = {
    val DBList = Factor.findAll
    if(DBList.isEmpty)
      "ERROR" -> "Factor not found" : JValue
    else
      {"FactorsList" -> DBList.map(_.asJValue)} : JValue

  }

  def getFactorByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id).get

    val DBList = Factor.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "Factor not found" :JValue
    else
      {"FactorsList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteFactor(_id : String): JValue = {

    Factor.delete(("_id" -> _id))

    { "SUCCESS" -> " DELETED " } : JValue

  }

//  def insertFactor(parentid : String, parentname : String, name : String, description : String, weigth : String, status : String): JValue = {
//
//    val Factorin = factorIN.createRecord.parentid(parentid).parentname(parentname).name(name).description(description).weigth(weigth).status(status)
//
//    {"SUCCESS" -> Factor.createRecord.id(UUID.randomUUID().toString).factor(Factorin).save.asJValue} : JValue
//
//  }

  def test(q : JValue): JValue = {

    ScoringRange(q)

    "test" -> "test" : JValue

  }

  def ScoringRange (q : JValue) : List[Double] = {
    val json = q.asInstanceOf[JObject].values
    val qry = QueryBuilder.start("ModelId").is(json.apply("ModelId").toString)
                          .get

    val DBList = Factor.findAll(qry)

    var listDBCuoi : List[Factor] = List()

    for(factor <- DBList){
      if(factor.FactorOption.value.size != 0)
        listDBCuoi = listDBCuoi ::: List(factor)
    }

    var min : Double = 0
    var max : Double = 0

    for(factor <- listDBCuoi){
      val list = factor.FactorOption.value.sortWith(_.Score.toString().toDouble < _.Score.toString().toDouble)

      var minIn = list(0).Score.toString().toDouble * (factor.Weigth.toString().toDouble/100)
      for(path <- factor.PathFactor.value){
        minIn = minIn * (path.Weigth.toString().toDouble/100)
      }

      var maxIn = list(list.size-1).Score.toString().toDouble * (factor.Weigth.toString().toDouble/100)
      for(path <- factor.PathFactor.value){
        maxIn = maxIn * (path.Weigth.toString().toDouble/100)
      }

      min = min + minIn
      max = max + maxIn
    }

    println("min : " + min + " - max : " + max)

    List(min, max)
  }

  def UpdateRangeModel (range : List[Double], ModelId : String) = {
    val qry = QueryBuilder.start("ModelId").is(ModelId).get
    val DBList = ModelInfo.findAll(qry)

    val update = DBList(0).update.min(range(0)).max(range(1)).save
  }

  def insertFactorOption(q : JValue): JValue = {
    val json = q.asInstanceOf[JObject].values
    var listFactorOption : List[FactorOptionIN] = List()
    if(json != null) {
      val qry = QueryBuilder.start("_id").is(json.apply("FactorId").toString).get
      val DBList = Factor.findAll(qry)

      val factorOption = FactorOptionIN
                          .Description(json.apply("Description").toString)
                          .FactorOptionName(json.apply("FactorOptionName").toString)
                          .Fatal(json.apply("Fatal").toString)
                          .Score(json.apply("Score").toString.toDouble)
                          .Status(json.apply("Status").toString)



      listFactorOption = listFactorOption ::: DBList(0).FactorOption.value

      listFactorOption = listFactorOption ::: List(factorOption)

      val updateFactor = DBList(0).update.FactorOption(listFactorOption).save

      {
        "SUCCESS" -> updateFactor.asJValue
      }: JValue
    }else
      {"ERROR" -> "INSERT FAILED"} : JValue

  }

  def insertFactor(q : JValue): JValue = {
    val json = q.asInstanceOf[JObject].values
    if(json != null) {
      var listPathFactor: List[FactorPath] = List()
      if (json.apply("Parentid").toString != "") {
        val qry = QueryBuilder.start("_id").is(json.apply("Parentid").toString).get
        val DBList = Factor.findAll(qry)
        if (DBList != null) {
          listPathFactor = listPathFactor ::: DBList(0).PathFactor.value
          val factorPath = FactorPath.createRecord
            .FactorPathId(DBList(0).id.toString())
            .Weigth(DBList(0).Weigth.toString().toDouble)
          val x: List[FactorPath] = List(factorPath)
          listPathFactor = listPathFactor ::: x
        }

      }

      val listFactorOption: List[FactorOptionIN] = List()
      val model : ModelInfo = null

      val saveItem: Factor = Factor.createRecord
        .id(UUID.randomUUID().toString)
        .ModelId(json.apply("ModelId").toString)
        .Parentid(json.apply("Parentid").toString)
        .ParentName(json.apply("ParentName").toString)
        .FactorName(json.apply("Name").toString)
        .Weigth(json.apply("Weigth").toString.toDouble)
        .Ordinal(json.apply("Ordinal").toString.toInt)
        .Status(json.apply("Status").toString)
        .Note(json.apply("Note").toString)
        .PathFactor(listPathFactor)
        .FactorOption(listFactorOption)
        .save

      {
        "SUCCESS" -> saveItem.asJValue
      }: JValue
    }else
      {"ERROR" -> "INSERT FAILED"} : JValue

  }

  def updateFactor(id : String, parentid : String, parentname : String, name : String, description : String, weigth : String, status : String): JValue = {

    Factor.update(("_id" -> id),
      ("$set" -> ("parentid" -> parentid)
        ~ ("parentname" -> parentname)
        ~ ("name" -> name)
        ~ ("weigth" -> weigth)
        ~ ("description" -> description)
        ~ ("status" -> status)))

    { "SUCCESS" -> " UPDATED " } : JValue

  }


  serve {
    case "factor" :: "getall"  :: Nil JsonGet req => getFactorJSON() : JValue

    case "factor" :: "getbyfactorid" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factor" :: "getbyfactorid" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getFactorByIdJSON(id) : JValue

    case "factor" :: "update" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factor" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(parentid) <- (json \\ "parentid").toOpt
          JString(parentname) <- (json \\ "parentname").toOpt
          JString(weigth) <- (json \\ "weigth").toOpt
          JString(description) <- (json \\ "description").toOpt
          JString(name) <- (json \\ "name").toOpt
          JString(status) <- (json \\ "status").toOpt
      } yield updateFactor(id, parentid, parentname, weigth, description, name, status)

    case "factor" :: "delete" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factor" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield  deleteFactor(id)

    case "factor" :: "insert" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factor" :: "insert" :: Nil JsonPost json -> request =>insertFactor(json)

    case "factor" :: "insertOption" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "factor" :: "insertOption" :: Nil JsonPost json -> request =>insertFactorOption(json)

    case "test" :: Nil Options _ => {"OK" -> "200"} :JValue
    case "test" :: Nil JsonPost json -> request =>test(json)

  }

}
