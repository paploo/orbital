package net.paploo.orbital.planetarysystem

import net.paploo.orbital.phys.PhysVec

object Planetoid {
  val kerbin = Planetoid("Kerbin", 3531600000000.0, 600000.0, 21600.0, 1.0, 5000.0)

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
case class Planetoid(name: String, mu: Double, radius: Double, rotPeriod: Double, seaLevelPressure: Double, atmAttenAlt: Double) {

  /**
   * The scalar angular velocity around the rotation axis.
   *
   *  Can be negative if rotating in the opposite direction.
   */
  lazy val angularSpeed = 2.0 * math.Pi / rotPeriod

  /** Angular velocity vector */
  lazy val angularVelocity = PhysVec(0.0, 0.0, angularSpeed)

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

  /** Returns true if the given position is at or below the planetoid surface. */
  def isCollision(pos: PhysVec): Boolean = isCollision(pos.r)

  /** Returns true if the given radius is at or below the planetoid surface. */
  def isCollision(r: Double): Boolean = alt(r) <= 0.0

  /** Returns true if the given position in in the atmosphere. */
  def isInAtmosphere(pos: PhysVec): Boolean = isInAtmosphere(pos.r)

  /** Returns true if the given radius in in the atmosphere. */
  def isInAtmosphere(r: Double): Boolean = alt(r) <= atmMaxAlt && alt(r) > 0.0

  /**
   * Returns true if the position is free and clear of both the surface
   *  and the atmosphere.
   */
  def isAboveAtmosphere(pos: PhysVec): Boolean = isAboveAtmosphere(pos.r)

  /**
   * Returns true if the radius is free and clear of both the surface
   *  and the atmosphere.
   *
   *  This method is equivalent to
   *  {{{
   *   !isCollision(r) && !isInAtmosphere(r)
   *  }}}
   *  but is more performant.
   */
  def isAboveAtmosphere(r: Double): Boolean = alt(r) > atmMaxAlt

  /** Atmospheric pressure at a position vector relative to the planetoid. */
  @inline final def atm(pos: PhysVec): Double = atm(pos.r)

  /** Atmospheric pressure at a given radius. */
  def atm(r: Double): Double = {
    if (isCollision(r)) seaLevelPressure
    else if (isAboveAtmosphere(r)) 0.0
    else seaLevelPressure * math.exp(-alt(r) / atmAttenAlt)
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

  /**
   * Returns the linear velocity of a rigidly attached body to the atmosphere
   * at the given position (e.g. the surface or the atmosphere).
   */
  def linearVelocity(pos: PhysVec): PhysVec = angularVelocity cross pos

  /**
   * The surface speed at the given latitude.  This vector is perpendicular
   * to both the rotation axis and your radius vector.
   */
  def surfaceSpeed(latitude: Double = 0.0) =
    angularSpeed * radius * math.cos(latitude)
}