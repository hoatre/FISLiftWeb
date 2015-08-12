package code.model.oauth2

import java.util.UUID

import net.liftweb.util.BCrypt
import org.bson.types.ObjectId

/**
 * Created by bacnv on 12/08/2015.
 */
object Bcypted {
  def randomEctyed():String={
    BCrypt.hashpw(UUID.randomUUID().toString + ObjectId.get().toString+"Bcrypt", BCrypt.gensalt());
  }

}
