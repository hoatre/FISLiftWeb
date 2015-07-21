package code
package model

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field.StringField

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

class Modules private () extends MongoRecord[Modules] with StringPk[Modules] {

  override def meta = Modules

  // An embedded document:
  object module extends BsonRecordField(this, moduleIN)

}

object Modules extends Modules with MongoMetaRecord[Modules] {
  override def collectionName = "modules"
}

class moduleIN private () extends BsonRecord[moduleIN] {
  def meta = moduleIN
  object status extends StringField(this, 12)
  object displayforguess extends StringField(this, 1024)
  object note extends StringField(this, 1024)
  object parentname extends StringField(this, 1024)
  object parent extends StringField(this, 1024)
  object icon extends StringField(this, 1024)
  object link extends StringField(this, 1024)
  object modulename extends StringField(this, 1024)
}

object moduleIN extends moduleIN with BsonMetaRecord[moduleIN]

//---------------------------------------------------------------------------------

class GroupModuleRoles private () extends MongoRecord[GroupModuleRoles] with StringPk[GroupModuleRoles] {

  override def meta = GroupModuleRoles

  // An embedded document:
  object groupmodulerole extends BsonRecordField(this, groupmoduleroleIN)

}

object GroupModuleRoles extends GroupModuleRoles with MongoMetaRecord[GroupModuleRoles] {
  override def collectionName = "groupmoduleroles"
}

class groupmoduleroleIN private () extends BsonRecord[groupmoduleroleIN] {
  def meta = groupmoduleroleIN
  object moduleid extends StringField(this, 1024)
  object groupid extends StringField(this, 1024)
  object roleid extends StringField(this, 1024)
}

object groupmoduleroleIN extends groupmoduleroleIN with BsonMetaRecord[groupmoduleroleIN]

//---------------------------------------------------------------------------------

class GroupUsers private () extends MongoRecord[GroupUsers] with StringPk[GroupUsers] {

  override def meta = GroupUsers

  // An embedded document:
  object groupuser extends BsonRecordField(this, groupuserIN)

}

object GroupUsers extends GroupUsers with MongoMetaRecord[GroupUsers] {
  override def collectionName = "usergroups"
}

class groupuserIN private () extends BsonRecord[groupuserIN] {
  def meta = groupuserIN
  object userid extends StringField(this, 1024)
  object groupid extends StringField(this, 1024)
}

object groupuserIN extends groupuserIN with BsonMetaRecord[groupuserIN]

//---------------------------------------------------------------------------------



//---------------------------------------------------------------------------------

//class FactorOption private () extends MongoRecord[FactorOption] with StringPk[FactorOption] {
//
//  override def meta = FactorOption
//
//  // An embedded document:
//  object factoroption extends BsonRecordField(this, FactorOption)
//
//}
//
//object FactorOption extends FactorOption with MongoMetaRecord[FactorOption] {
//  override def collectionName = "factoroption"
//}

//class factoroptionIN private () extends BsonRecord[factoroptionIN] {
//  def meta = factoroptionIN
//  object factorid extends StringField(this, 1024)
//  object name extends StringField(this, 1024)
//  object description extends StringField(this, 1024)
//  object score extends StringField(this, 1024)
//  object fatal extends StringField(this, 1024)
//  object status extends StringField(this, 1024)
//}
//
//object factoroptionIN extends factoroptionIN with BsonMetaRecord[factoroptionIN]

//---------------------------------------------------------------------------------




//---------------------------------------------------------------------------------