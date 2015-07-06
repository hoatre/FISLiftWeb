/**
 * Created by phong on 7/6/2015.
 */

import bootstrap.liftweb._
import code.model.{Country, DayOfWeek, Birthday}

object ConnectMongo {
  def main(args: Array[String]) {
    new Boot().boot
    println(Birthday.findAll.toList(0))
  }
}
