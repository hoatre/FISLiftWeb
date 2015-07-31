package code.common

import java.io.FileInputStream

import net.liftweb.common.Full
import net.liftweb.util.Props

/**
 * Created by bacnv on 31/07/2015.
 */
object Utils {

  def propsWheretoLook(filename:String) : List[(String, () => Full[FileInputStream])]={
        println(Props.mode)
        Props.mode match {
          case Props.RunModes.Test =>  (("src/main/resources/props/test/"+filename, () => Full(new FileInputStream("src/main/resources/props/test/"+filename))) :: Nil)
          case Props.RunModes.Production =>  (("src/main/resources/props/production/"+filename, () => Full(new FileInputStream("src/main/resources/production/test/"+filename))) :: Nil)
          case Props.RunModes.Development =>  (("src/main/resources/props/dev/"+filename, () => Full(new FileInputStream("src/main/resources/props/dev/"+filename))) :: Nil)
        }
  }
  def propsWheretoLook(filename:String,any :Any) : List[(String, () => Full[FileInputStream])]={
    println(Props.mode)
    (("src/main/resources/props/"+filename, () => Full(new FileInputStream("src/main/resources/props/"+filename))) :: Nil)
  }

}
