package net.paploo.orbital

/** The state variables (time, position, and velocity) grouped together */
trait State {
  def t: Double
  def pos: PhysVec
  def vel: PhysVec

  def specificEnergy: Double = 0.5 * (vel dot vel)
  def specificMomentum: Double = vel.r
  def specificMoment(origin: PhysVec = PhysVec.zero): PhysVec =
    (pos - origin) cross vel
}

/** A case-class implementation of the State trait. */
case class SimpleState(t: Double, pos: PhysVec, vel: PhysVec) extends State

/**
 * A planetoid-centric implementation of the State trait.
 *
 *  The coordinates of this state are taken to be in reference frame of the
 *  given planetoid.
 */
case class RelativeState(planetoid: Planetoid, t: Double, pos: PhysVec, vel: PhysVec) extends State {
  //TODO: Methods for converting from one planetoid frame to the other.
}
