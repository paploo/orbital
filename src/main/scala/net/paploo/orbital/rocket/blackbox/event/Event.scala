package net.paploo.orbital.rocket.blackbox.event

import net.paploo.orbital.rocket.{ Rocket, Stage }
import net.paploo.orbital.planetarysystem.Planetoid

/** Abstract superclass of all rocket events. */
abstract class Event[+R <: Rocket[R]] {
  def rocket: R
  def isControlEvent: Boolean = false
  def isTerminationEvent: Boolean = false
}

package control {

  trait ControlEvent[+R <: Rocket[R]] extends Event[R] {
    override def isControlEvent: Boolean = true
  }

  trait InitiationEvent[+R <: Rocket[R]] extends ControlEvent[R]

  trait TerminationEvent[+R <: Rocket[R]] extends ControlEvent[R] {
    override def isTerminationEvent: Boolean = true
  }

  case class StartOfSimulationEvent[+R <: Rocket[R]](rocket: R) extends InitiationEvent[R]

  case class ImpactEvent[+R <: Rocket[R], B](rocket: R, target: B) extends TerminationEvent[R]

  case class EndOfProgramEvent[+R <: Rocket[R]](rocket: R) extends TerminationEvent[R]

  case class StableOrbitEvent[+R <: Rocket[R]](rocket: R) extends TerminationEvent[R]

}

package rocket {

  trait RocketEvent[+R <: Rocket[R]] extends Event[R]

  case class FuelStarvationEvent[+R <: Rocket[R]](rocket: R) extends RocketEvent[R]

  case class StagingEvent[+R <: Rocket[R]](rocket: R, ejectedStage: Stage, remainingStages: List[Stage]) extends RocketEvent[R]

}

package orbital {

  trait OrbitalEvent[+R <: Rocket[R]] extends Event[R]

  case class ApoapsisEvent[+R <: Rocket[R]](rocket: R) extends OrbitalEvent[R]

  case class PeriapsisEvent[+R <: Rocket[R]](rocket: R) extends OrbitalEvent[R]

  case class AscendingNodeEvent[+R <: Rocket[R], B](rocket: R, referenceObject: B) extends OrbitalEvent[R]

  case class DescendingNodeEvent[+R <: Rocket[R], B](rocket: R, referenceObject: B) extends OrbitalEvent[R]

  case class SOIEvent[+R <: Rocket[R]](rocket: R, fromPlanetoid: Planetoid, toPlanetoid: Planetoid) extends OrbitalEvent[R]

}

package trigger {

  trait TriggerEvent[+R <: Rocket[R]] extends Event[R]

  case class TimeEvent[+R <: Rocket[R]](rocket: R, time: Double) extends TriggerEvent[R]

  case class TruAnomalyEvent[+R <: Rocket[R]](rocket: R, trueAnomaly: Double) extends TriggerEvent[R]

  case class RemainingDeltaVEvent[+R <: Rocket[R]](rocket: R, remainingDeltaV: Double) extends TriggerEvent[R]

}
