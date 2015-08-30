package code.model.oauth2

import java.util.UUID

import bootstrap.liftweb.UsersDb
import net.liftweb.common.Full
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{IntField, StringField}
import net.liftweb.mongodb.BsonDSL._
import net.liftweb.util.Props

/**
 * Created by bacnv on 28/08/2015.
 */
class FirstBoot private () extends MongoRecord[FirstBoot] with StringPk[FirstBoot] {

  override def meta = FirstBoot

  // An embedded document:
  object status  extends IntField(this)
}
object FirstBoot extends FirstBoot with MongoMetaRecord[FirstBoot] {
  override def collectionName = "firstboot"

  override def mongoIdentifier = UsersDb

  def firstboot():Unit={
    val count = FirstBoot.count
    if(count == 0){
    val appid =UUID.randomUUID().toString
    val groupid =UUID.randomUUID().toString
      val rolid = UUID.randomUUID().toString
      FirstBoot.createRecord.id(UUID.randomUUID().toString).status(0).save(true)
      Applications.createRecord.id(appid).name("fislifweb").note("").status("active").description("").created_by("system").created_date(System.currentTimeMillis()/1000).modified_by("system").modified_date(System.currentTimeMillis()/1000).save(true)
      Group.createRecord.id(groupid).created_by("system").modified_by("system").app_id(Props.props.apply("app_id")).created_date(System.currentTimeMillis()).description("").modified_date(System.currentTimeMillis()).name("group all")
        .note("").status("active").save(true)
      Role.createRecord.id(rolid).created_by("system").modified_by("system").created_date(System.currentTimeMillis()).description("").modified_date(System.currentTimeMillis())
        .note("").status("active").save(true)
//      GroupFunction.createRecord.
    }
  }
  def updateBoot():Unit={
    val db = FirstBoot.find("status" -> 0)

  db match {
    case Full(a) => FirstBoot.update("_id" -> a.id.toString(),"$set" -> ("status" -> 1))
    case _ =>
  }
  }

}
