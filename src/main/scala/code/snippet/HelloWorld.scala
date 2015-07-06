package code
package snippet

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import Helpers._
import code.model._

class HelloWorld {

  val uk = Country.find("uk") openOr {
    val earth = Planet.createRecord.id("earth").review("Harmless").save
    Country.createRecord.id("uk").planet(earth.id.is).save
  }

  def facts =
    ".country *" #> uk.id &
      ".planet" #> uk.planet.obj.map { p =>
        ".name *" #> p.id &
          ".review *" #> p.review
      }

}

