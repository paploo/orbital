package net.paploo.orbital.rocket.blackbox.event

import net.paploo.orbital.rocket.Rocket
import net.paploo.orbital.planetarysystem.Planetoid

/** Abstract superclass of all rocket events. */
abstract class Event[R <: Rocket[R]] {
  def rocket: R
}

/** Event for when the rocket reaches an apoapsis */
case class ApoapsisEvent[R <: Rocket[R]](rocket: R) extends Event[R]

/** Event for when the rocket reaches a periapsis */
case class PeriapsisEvent[R <: Rocket[R]](rocket: R) extends Event[R]

/**
 * Event for when the rocket reaches an ascending node.
 *
 *  Note that the second parameter, while usually a Planetoid, may be another
 *  Rocket.
 */
case class AscendingNodeEvent[R <: Rocket[R], B](rocket: R, referenceObject: B) extends Event[R]

/**
 * Event for when the rocket reaches a descending node.
 *
 *  Note that the second parameter, while usually a Planetoid, may be another
 *  Rocket.
 */
case class DescendingNodeEvent[R <: Rocket[R], B](rocket: R, referenceObject: B) extends Event[R]

/** Event for when changing SOIs */
case class SOIEvent[R <: Rocket[R]](rocket: R, fromPlanetoid: Planetoid, toPlanetoid: Planetoid) extends Event[R]

/**
 * Event for when a given time is reached.
 *
 *  This is typically used as a response to a triggered event.
 */
case class RimeEvent[R <: Rocket[R]](rocket: R, time: Double) extends Event[R]

/**
 * Event for when a given true anomaly is reached.
 *
 *  This is typically used as a response to a triggered event.
 */
case class RrueAnomalyEvent[R <: Rocket[R]](rocket: R, trueAnomaly: Double) extends Event[R]

/**
 * Event for when a given remaining total delta-v is reached
 *
 *  This is typically used as a response to a triggered event.
 */
case class RemainingDeltaVEvent[R <: Rocket[R]](rocket: R, remainingDeltaV: Double) extends Event[R]
