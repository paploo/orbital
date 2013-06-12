package net.paploo.orbital

/** Abstract superclass of all rocket events. */
abstract class RocketEvent[T <: Rocket[T]] {
  def rocket: T
}

/** Event for when the rocket reaches an apoapsis */
case class ApoapsisEvent[T <: Rocket[T]](rocket: T) extends RocketEvent[T]

/** Event for when the rocket reaches a periapsis */
case class PeriapsisEvent[T <: Rocket[T]](rocket: T) extends RocketEvent[T]

/**
 * Event for when the rocket reaches an ascending node.
 *
 *  Note that the second parameter, while usually a Planetoid, may be another
 *  Rocket.
 */
case class AscendingNodeEvent[A <: Rocket[A], B](rocket: A, referenceObject: B) extends RocketEvent[A]

/**
 * Event for when the rocket reaches a descending node.
 *
 *  Note that the second parameter, while usually a Planetoid, may be another
 *  Rocket.
 */
case class DescendingNodeEvent[A <: Rocket[A], B](rocket: A, referenceObject: B) extends RocketEvent[A]

/** Event for when changing SOIs */
case class SOIEvent[T <: Rocket[T]](rocket: T, fromPlanetoid: Planetoid, toPlanetoid: Planetoid) extends RocketEvent[T]

/**
 * Event for when a given time is reached.
 *
 *  This is typically used as a response to a triggered event.
 */
case class TimeEvent[T <: Rocket[T]](rocket: T, time: Double)

/**
 * Event for when a given true anomaly is reached.
 *
 *  This is typically used as a response to a triggered event.
 */
case class TrueAnomalyEvent[T <: Rocket[T]](rocket: T, trueAnomaly: Double)

/**
 * Event for when a given remaining total delta-v is reached
 *
 *  This is typically used as a response to a triggered event.
 */
case class RemainingDeltaVEvent[T <: Rocket[T]](rocket: T, remainingDeltaV: Double)