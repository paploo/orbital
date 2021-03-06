package net.paploo.orbital.app

import scala.concurrent.duration._
import scala.sys.process.Process
import java.io.{ File, PrintWriter, FileOutputStream }
import net.paploo.orbital.rocket._

import net.paploo.orbital.phys.{ State, PhysVec }
import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.rocket.blackbox.BlackBox

object App {

  def main(args: Array[String]): Unit = {
    val funcs = rocketRunFunctions.toIndexedSeq

    for {
      index <- 0 until funcs.length
      (label, func) = funcs(index)
    } {
      println(s"Running $label (${index + 1} of ${funcs.length})...")
      val (rocket, delta) = time(func)
      rocket match {
        case rocket: UnpoweredRocket => logRocket(label, rocket, delta)(rocketLibFile)
        case rocket: StagedRocket => logRocket(label, rocket, delta)(rocketLibFile)
        case rocket => new java.lang.RuntimeException(s"Expected a known Rocket type, but got ${rocket.getClass.getName}")
      }
      println(s"Completed $label in $delta.")
    }

    System.exit(0)
  }

  def time[T](f: () => T): (T, Duration) = {
    val start = System.currentTimeMillis
    val result = f()
    val stop = System.currentTimeMillis
    val deltaT = Duration(stop - start, MILLISECONDS)
    (result, deltaT)
  }

  def getHostName: String = {
    var rawName: String = null
    try {
      rawName = java.net.InetAddress.getLocalHost.getHostName
    } catch {
      case ex: java.net.UnknownHostException =>
        rawName = Process("hostname").!!
    }
    rawName.split('.').head
  }

  def rocketRunFunctions = Map[String, () => Any](
    "Basic Orbit" -> SampleLibrary.basicOrbit _,
    "Staged Vertical Climb" -> SampleLibrary.stagedVerticalClimb _
  )

  lazy val rocketLibFilePath: String = s"libout/${getHostName}.txt"

  lazy val rocketLibFile: File = {
    val file = new File(rocketLibFilePath)
    if (file.exists) file.delete
    file.createNewFile
    file
  }

  def logRocket[R <: Rocket[R]](label: String, rocket: Rocket[R], duration: Duration)(file: File) = {
    val writer = new PrintWriter(new FileOutputStream(file, true))

    writer.println(hr)
    writer.println(s"$label:")
    writer.println(s"Run Time: $duration")
    writer.println
    writer.println(rocket)
    writer.println
    writer.println(s"apeses: ${rocket.apses}")
    writer.println(s"period: ${rocket.period}")
    writer.println(s"trueAnomaly: ${rocket.trueAnomaly}")
    writer.println(s"radialDistance: ${rocket.pos.r}")
    writer.println
    writer.println(rocket.blackBox)
    writer.println

    writer.close
  }

  val hr = "* " * 35 + "*"

}