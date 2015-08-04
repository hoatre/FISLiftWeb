package code.common

import java.io.FileInputStream

import net.liftweb.common.Full
import net.liftweb.util.Props

import scala.io.Source

/**
 * Created by bacnv on 31/07/2015.
 */
object Utils {

  def propsWheretoLook(filename:String) : List[(String, () => Full[FileInputStream])]={
        println(Props.mode)
   println(Props.hostName)
        Props.mode match {
          case Props.RunModes.Test =>  ((getClass.getResource("/props/test/"+filename).getPath, () => Full(new FileInputStream(getClass.getResource("/props/test/"+filename).getPath))) :: Nil)
          case Props.RunModes.Production =>  ((getClass.getResource("/props/production/"+filename).getPath, () => Full(new FileInputStream(getClass.getResource("/props/production/"+filename).getPath))) :: Nil)
          case Props.RunModes.Development =>  ((getClass.getResource("/props/dev/"+filename).getPath, () => Full(new FileInputStream(getClass.getResource("/props/dev/"+filename).getPath))) :: Nil)
        }
  }
  def propsWheretoLook(filename:String,any :Any) : List[(String, () => Full[FileInputStream])]={
    println(Props.mode)
    ((getClass.getResource("/props/"+filename).getPath, () => Full(new FileInputStream(getClass.getResource("/props/"+filename).getPath))) :: Nil)
  }

}
