package code
package model

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.{StringField}

//---------------------------------------------------------------------------------
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

//---------------------------------------------------------------------------------

class Roles private () extends MongoRecord[Roles] with StringPk[Roles] {

  override def meta = Roles

  // An embedded document:
  object role extends BsonRecordField(this, roleIN)

}

object Roles extends Roles with MongoMetaRecord[Roles] {
  override def collectionName = "roles"
}

class roleIN private () extends BsonRecord[roleIN] {
  def meta = roleIN
  object status extends StringField(this, 12)
  object note extends StringField(this, 1024)
  object controlid extends StringField(this, 1024)
  object rolename extends StringField(this, 1024)
}

object roleIN extends roleIN with BsonMetaRecord[roleIN]

//---------------------------------------------------------------------------------

class GroupModules private () extends MongoRecord[GroupModules] with StringPk[GroupModules] {

  override def meta = GroupModules

  // An embedded document:
  object groupmodule extends BsonRecordField(this, groupmoduleIN)

}

object GroupModules extends GroupModules with MongoMetaRecord[GroupModules] {
  override def collectionName = "groupmodules"
}

class groupmoduleIN private () extends BsonRecord[groupmoduleIN] {
  def meta = groupmoduleIN
  object moduleid extends StringField(this, 1024)
  object groupid extends StringField(this, 1024)
}

object groupmoduleIN extends groupmoduleIN with BsonMetaRecord[groupmoduleIN]

//---------------------------------------------------------------------------------