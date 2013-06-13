package net.paploo.orbital.planetarysystem

import net.paploo.orbital.phys.PhysVec

object Planetoid {
  val kerbin = Planetoid("Kerbin", 3531600000000.0, 600000.0, 21600.0, 5000.0)

  val kerbinSurfaceDensity = 1.2230948554874
  val dragMultiplier = 0.008
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

  /**
   * As of 0.21, the max altitude of the atmosphere is the atmospheric attenuation
   * times the natural log of 1e6 for reasons that aren't well understood.
   *
   * Note that time-warp includes an extra 2.25 km buffer for parts that are
   * on the edge to go out of the local game scope, so the apparent altitude
   * in game will be slightly higher.
   */
  lazy val atmMaxAlt = atmAttenAlt * math.log(1e6) // For some reason the devs chose to do it this way.  

  @inline final def alt(pos: PhysVec): Double = alt(pos.r)
  @inline final def alt(r: Double): Double = r - radius

  /** Atmospheric pressure at a position vector relative to the planetoid. */
  @inline final def atm(pos: PhysVec): Double = atm(pos.r)

  /** Atmospheric pressure at a given radius. */
  def atm(r: Double): Double = {
    val a = alt(r)
    if (a > atmMaxAlt) 0.0
    else if (a <= 0.0) 1.0
    else math.exp(-a / atmAttenAlt)
  }

  /** Atmospheric density at a position vector relative to the planetoid. */
  @inline final def density(pos: PhysVec): Double = density(pos.r)

  /**
   * Atmospheric density at a given radius.
   *
   * At the time of 0.21, it is still believed by the community that the
   * density for all planetoids is calculated from the pressure using
   * kerbins surface density and the drag multiplier.
   */
  def density(r: Double): Double =
    Planetoid.kerbinSurfaceDensity * Planetoid.dragMultiplier * atm(r)
}