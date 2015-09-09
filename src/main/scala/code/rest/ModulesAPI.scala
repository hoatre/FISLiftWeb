package code.rest

/**
 * Created by phong on 7/8/2015.
 */

import java.util.UUID
import code.model._
import code.model.oauth2.{APIs, Functions}
import com.mongodb.QueryBuilder
import net.liftweb.http.{OkResponse, LiftRules}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST._
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Helpers._

object ModulesAPI extends RestHelper {


  def init(): Unit = {
    LiftRules.statelessDispatch.append(ModulesAPI)
    APIs.insertBoot("/module/getall")
    APIs.insertBoot("/module/getbyroleid")
    APIs.insertBoot("/module/update")
    APIs.insertBoot("/module/delete")
    APIs.insertBoot("/module/insert")
  }
  serve {
    case "module" :: "getall" :: Nil Options _ => OkResponse()
    case "module" :: "getall"  :: Nil JsonGet req => getModuleJSON() : JValue

    case "module" :: "getbyroleid" :: Nil Options _ => OkResponse()
    case "module" :: "getbyroleid" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield getModuleByIdJSON(id) : JValue

    case "module" :: "update" :: Nil Options _ => OkResponse()
    case "module" :: "update" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt
          JString(status) <- (json \\ "status").toOpt
          JString(displayforguess) <- (json \\ "displayforguess").toOpt
          JString(note) <- (json \\ "note").toOpt
          JString(parentname) <- (json \\ "parentname").toOpt
          JString(parent) <- (json \\ "parent").toOpt
          JString(icon) <- (json \\ "icon").toOpt
          JString(link) <- (json \\ "link").toOpt
          JString(modulename) <- (json \\ "modulename").toOpt
      } yield updateModule(id, status, displayforguess, note, parentname
        , parent, icon, link, modulename)

    case "module" :: "delete" :: Nil Options _ => OkResponse()
    case "module" :: "delete" :: Nil JsonPost json -> request =>
      for{JString(id) <- (json \\ "id").toOpt} yield deleteModule(id)

    //    case "module" :: "delete" :: id :: Nil JsonDelete req => deleteModule(id)

    case "module" :: "insert" :: Nil Options _ => OkResponse()
    case "module" :: "insert" :: Nil JsonPost json -> request =>
      for{JString(status) <- (json \\ "status").toOpt
          JString(displayforguess) <- (json \\ "displayforguess").toOpt
          JString(note) <- (json \\ "note").toOpt
          JString(parentname) <- (json \\ "parentname").toOpt
          JString(parent) <- (json \\ "parent").toOpt
          JString(icon) <- (json \\ "icon").toOpt
          JString(link) <- (json \\ "link").toOpt
          JString(modulename) <- (json \\ "modulename").toOpt
      } yield insertModule(status, displayforguess, note, parentname
        , parent, icon, link, modulename)
  }

  def getModuleJSON(): JValue = {

    val DBList = Modules.findAll
    if(DBList.isEmpty)
      "ERROR" -> "Module not found" : JValue
    else
      {"ModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def getModuleByIdJSON(id : String): JValue = {

    val qry = QueryBuilder.start("_id").is(id)
      .get

    val DBList = Modules.findAll(qry)

    if(DBList.isEmpty)
      "ERROR" -> "Module not found" :JValue
    else
      {"GroupModulesList" -> DBList.map(_.asJValue)} : JValue

  }

  def deleteModule(_id : String): JValue = {

    Modules.delete(("_id" -> _id))

    { "SUCCESS" -> " DELETED " } : JValue

  }

  def insertModule(status : String, displayforguess : String,
                   note : String,parentname : String,parent : String,
                   icon : String,link : String,modulename : String): JValue = {

    val modulein = moduleIN.createRecord.status(status).displayforguess(displayforguess)
                                        .note(note).parentname(parentname).parent(parent)
                                        .icon(icon).link(link).modulename(modulename)



    { "SUCCESS" -> Modules.createRecord.id(UUID.randomUUID().toString).module(modulein).save.asJValue } : JValue

  }

  def updateModule(id : String, status : String, displayforguess : String,
                        note : String,parentname : String,parent : String,
                        icon : String,link : String,modulename : String): JValue = {

    Modules.update(("_id" -> id),
      ("$set" -> ("module.status" -> status)
        ~ ("module.displayforguess" -> displayforguess)
        ~ ("module.note" -> displayforguess)
        ~ ("module.parentname" -> displayforguess)
        ~ ("module.parent" -> displayforguess)
        ~ ("module.icon" -> displayforguess)
        ~ ("module.link" -> displayforguess)
        ~ ("module.modulename" -> displayforguess)))

    { "SUCCESS" -> " UPDATED " } : JValue

  }




}

