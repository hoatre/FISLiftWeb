package code.dao

import java.util.UUID

import code.model.{Users => UserModel}
import com.mongodb.QueryBuilder
import org.bson.types.ObjectId

/**
 * Created by bacnv on 7/8/15.
 */
object Users {

  def insertUser(u : UserModel) : List[UserModel] = {
    val uid = ObjectId.get()

    UserModel.createRecord.id(uid).user(u.user.get).save

    val qry = QueryBuilder.start("_id").is(uid)
      .get

     UserModel.findAll(qry)

  }

}
