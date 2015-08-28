package code.model.oauth2

import java.util.UUID

import bootstrap.liftweb.UsersDb
import net.liftweb.common.Full
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.record.field.{IntField, StringField}
import net.liftweb.mongodb.BsonDSL._

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
      FirstBoot.createRecord.id(UUID.randomUUID().toString).status(0).save
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
