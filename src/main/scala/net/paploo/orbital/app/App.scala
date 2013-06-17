package net.paploo.orbital.app

object App {

  def main(args: Array[String]): Unit = {
    println(java.net.InetAddress.getLocalHost.getHostName)
    System.exit(0)
  }

}