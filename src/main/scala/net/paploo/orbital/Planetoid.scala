package net.paploo.orbital

object Planetoid {
  val kerbin = Planetoid("Kerbin", 3531600000000.0, 600000.0, 21600.0, 5000.0)
}

/**
 * A Planetoid.
 *
 * Represents a Planetoid as a standalone object.  Constrast this against
 * knowing information about its orbit in relation to its parent body, and
 * contextual information like who its parent and children are, which is a
 * different object.
 *
 * TODO: Determine if convenience methods to the planetary system tree should
 * be in planetoid.
 */
case class Planetoid(name: String, mu: Double, radius: Double, rotPeriod: Double, atmAttenAlt: Double) {
  lazy val angular_velocity = 2.0 * math.Pi / rotPeriod

  lazy val atmMaxAlt = atmAttenAlt * math.log(1e6) // For some reason the devs chose to do it this way.  

  @inline def alt(pos: PhysVec): Double = alt(pos.r)
  @inline def alt(r: Double): Double = r - radius

  def atm(pos: PhysVec): Double = atm(pos.r)
  def atm(r: Double): Double = {
    val a = alt(r)
    if (a > atmMaxAlt) 0.0
    else if (a < 0.0) 1.0
    else math.exp(-a / atmAttenAlt)
  }
}