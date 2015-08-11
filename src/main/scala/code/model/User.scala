package code.model


import bootstrap.liftweb.UsersDb
import net.liftweb.mongodb.record.field.StringPk
import net.liftweb.mongodb.record.{MongoRecord, MongoMetaRecord}
import net.liftweb.record.field.{LongField, PasswordField, StringField}
import org.datanucleus.enhancer.spi.StringFieldPK

/**
 * Created by bacnv on 11/08/2015.
 */
class User private () extends MongoRecord[User] with StringPk[User] {

  override def meta = User

  // An embedded document:
  object  username extends StringField(this,1024)
  object email  extends StringField(this,1024)
  object password  extends PasswordField(this)
  object imageurl  extends StringField(this,1024)
  object picture  extends StringField(this,1024)
  object facebookid  extends StringField(this,1024)
  object googleid  extends StringField(this,1024)
  object displayname  extends StringField(this,1024)
  object status  extends StringField(this,1024)
  object description  extends StringField(this,1024)
  object note  extends StringField(this,1024)
  object cratedby  extends StringField(this,1024)
  object createddate  extends LongField(this,15)
  object modifiedby  extends StringField(this,1024)
  object modifieddate  extends LongField(this,15)

}
object User extends User with MongoMetaRecord[User] {
  override def collectionName = "user"
  override def mongoIdentifier = UsersDb

//  def get(id: String): Option[User] = {
//   User.find("_id" -> id)
//  }
//
//  def findByUsername(username: String): Option[User] =
//    users.where(_.username === username).firstOption
//
//  /**
//   * @param username Username to find
//   * @param encryptedPassword Encrypted version of password
//   * @return Option containing User.
//   */
//  def findByUsernameAndPassword(username: String, encryptedPassword: String): Option[User] = {
//
//    User.find(("username" -> username) ~ ("password" -> encryptedPassword))
//  }
//
//  /**
//   * @param user User object with already encrypted password
//   * @return
//   */
//  def insert(user: User) = {
//   user.save
//  }
//
//  /**
//   * @param id User id to be updated
//   * @param user New User details
//   * @return
//   */
//  def update(id: String, user: User) = {
//
//  }
//
//  /**
//   * @param user User object to be deleted
//   * @return
//   */
//  def delete(user: User) = {
////    User.delete(("_id" -> user.id.toString()))
//    User.deleteAll(user)
//  }
//
//  /**
//   * Delete all the users. NOTE: Use with caution.
//   * @return
//   */
//  def deleteAll() = {
//
//  }
}
