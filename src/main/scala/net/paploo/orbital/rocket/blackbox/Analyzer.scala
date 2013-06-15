package net.paploo.orbital.rocket.blackbox

import net.paploo.orbital.rocket.{ Rocket, UnpoweredRocket, StagedRocket }
import BlackBox.{ EventLog, EventLogBuilder }

trait Analyzer[+T <: Rocket[T]] {

  def step: T

  def nextStep: T

  /**
   * Analyzes the step and next step, generating events that should be included
   *  on the next step.
   *
   *  When creating events, it is preferrable to associate them with the next
   *  step rather than the current step. This is because the events created
   *  here are typically placed on the next step.
   */
  def analyze[U >: T <: Rocket[U]]: EventLog[U]

}

class RocketAnalyzer[+T <: Rocket[T]](val step: T, val nextStep: T) extends Analyzer[T] {

  override def analyze[U >: T <: Rocket[U]]: EventLog[U] = {
    val log = new EventLogBuilder[T]

    log ++= orbitalEvents

    log.result
  }

  protected def orbitalEvents: EventLog[T] = {
    val log = new EventLogBuilder[T]

    val stepRadialVel = step.vel dot step.pos.unit
    val nextStepRadialVel = nextStep.vel dot nextStep.pos.unit

    if (stepRadialVel > 0.0 && nextStepRadialVel <= 0.0)
      log += event.orbital.ApoapsisEvent(nextStep)

    if (stepRadialVel < 0.0 && nextStepRadialVel >= 0.0)
      log += event.orbital.PeriapsisEvent(nextStep)

    if (step.t < 1000.0 && nextStep.t >= 1000.0)
      log += event.trigger.TimeEvent(nextStep, 1000.0)

    log.result
  }

}

class StagedRocketAnalyzer(override val step: StagedRocket, override val nextStep: StagedRocket)
  extends RocketAnalyzer[StagedRocket](step, nextStep) {

  override def analyze[U >: StagedRocket <: Rocket[U]]: EventLog[U] = {
    val log = new EventLogBuilder[StagedRocket]

    if (step.stages.length > nextStep.stages.length)
      log += event.rocket.StagingEvent(nextStep, step.currentStage, nextStep.stages)

    log ++= super.analyze

    log.result
  }

}