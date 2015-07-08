package code.dao

import java.util.UUID

import code.model.{Users => UserModel}
import com.mongodb.QueryBuilder

/**
 * Created by bacnv on 7/8/15.
 */
object Users {

  def insertUser(u : UserModel) : List[UserModel] = {
    val uid = UUID.randomUUID().toString

    UserModel.createRecord.id(uid).user(u.user.get)._v(u._v.get).save

    val qry = QueryBuilder.start("_id").is(uid)
      .get

     UserModel.findAll(qry)

  }

}
