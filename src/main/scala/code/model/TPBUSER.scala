package code
package model

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.{StringField}

class Groups private () extends MongoRecord[Groups] with StringPk[Groups] {

  override def meta = Groups

  // An embedded document:
  object group extends BsonRecordField(this, groupIN)

}

object Groups extends Groups with MongoMetaRecord[Groups] {
  override def collectionName = "groups"
}

class groupIN private () extends BsonRecord[groupIN] {
  def meta = groupIN
  object status extends StringField(this, 12)
  object note extends StringField(this, 1024)
  object groupname extends StringField(this, 1024)
}

object groupIN extends groupIN with BsonMetaRecord[groupIN]