package net.paploo.orbital.rocket

import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.phys.{ PhysVec, State, OrbitalParameters }
import net.paploo.orbital.phys.PhysVec.{ SphericalVec, VecDouble }
import blackbox.BlackBox
import blackbox.{ RocketAnalyzer, StagedRocketAnalyzer }
import net.paploo.orbital.phys.Steppable

object Rocket {
  /** Rocket ISP is calculated using this value for the surface gravity. */
  val ispSurfaceGravity = 9.8072

  /**
   * While true drag is a weighted average, ships without parachutes deployed
   *  are typically very nearly 0.2 as of 0.21.
   */
  val standardDragCoefficient = 0.2

  /** As of 0.21, the cross-sectional area isn't calculated and instead set to 1. */
  val crossSectionalArea = 1.0
}

/**
 * The Rocket trait.
 *
 *  All rockets are considered immutable, so most values are declared as val
 *  or lazy val.
 *
 *  Rockets are Steppables with Orbital Parameters. Be sure to check these out.
 *
 *  When subclassing, it is important to override the step method from Steppable,
 *  as well as provide for state, mass, attitude, thrust, and mass flow.
 *
 */
trait Rocket[+T <: Rocket[T]] extends Steppable[T] with OrbitalParameters {
  this: T =>

  val state: State

  val mass: Double

  val massFlow: Double

  val attitude: PhysVec

  val thrust: Double

  def isFuelStarved: Boolean

  lazy val isOnCrashCourse: Boolean = periapsis <= planetoid.radius

  lazy val isInStableOrbit: Boolean =
    isFuelStarved && planetoid.isAboveAtmosphere(pos) && !isOnCrashCourse

  def deltaV: Double

  lazy val force: PhysVec = gravForce + dragForce + thrustForce

  lazy val acceleration: PhysVec = force / mass

  lazy val gravForce: PhysVec =
    -((planetoid.mu * mass) / (pos.sq)) * pos.unit

  lazy val dragForce: PhysVec = {
    val relVel = vel - planetoid.linearVelocity(pos)
    val magnitude = 0.5 * planetoid.density(pos) * relVel.sq * mass * Rocket.standardDragCoefficient * Rocket.crossSectionalArea
    -magnitude * relVel.unit
  }

  lazy val thrustForce: PhysVec = SphericalVec(thrust, attitude.phi, attitude.th)

  def blackBox: BlackBox[T]

  override def toString = s"${getClass.getSimpleName}($state)"
}

/** An concrete unpowered Rocket superclass. */
class UnpoweredRocket(override val state: State,
                      override val mass: Double,
                      override val blackBox: BlackBox[UnpoweredRocket])
  extends Rocket[UnpoweredRocket]
  with Steppable[UnpoweredRocket] {

  /* Overriding this to remove the thrust component reduces computation time by 25% */
  override lazy val force = gravForce + dragForce

  override val thrust = 0.0

  override val massFlow = 0.0

  override val attitude = PhysVec.zero

  override val isFuelStarved = true

  override val deltaV = 0.0

  override def physStep(deltaT: Double): Option[UnpoweredRocket] =
    Option(new UnpoweredRocket(state.step(deltaT, acceleration), mass, blackBox))

  override def analyzeSteps[U >: UnpoweredRocket <: Steppable[U]](steps: (U, U)): Option[U] = steps match {
    case (step: UnpoweredRocket, nextStep: UnpoweredRocket) => {
      if (step.blackBox.isNewlyTerminated) None
      else {
        val eventLog = new RocketAnalyzer(step, nextStep).analyze
        if (eventLog.isEmpty) Some(nextStep)
        else Option(nextStep ++ eventLog)
      }
    }
    case _ => None
  }

  def ++(eventLog: BlackBox.EventLog[UnpoweredRocket]): UnpoweredRocket =
    new UnpoweredRocket(state, mass, blackBox ++ eventLog)

}

/**
 * StagedRockets are PoweredRockets with a list of Stages to burn through.
 *
 * The simplest powered rocket is merely a staged rocket with one stage.
 */
class StagedRocket(override val state: State,
                   override val attitude: PhysVec,
                   val throttle: Double,
                   val stages: List[Stage],
                   override val blackBox: BlackBox[StagedRocket])
  extends Rocket[StagedRocket]
  with Steppable[StagedRocket] {

  override lazy val mass = Stage.mass(stages)

  lazy val currentStage = stages.head

  lazy val atm = planetoid.atm(pos)

  override lazy val thrust: Double = currentStage.thrust(atm, throttle)

  override lazy val massFlow: Double = currentStage.massFlow(atm, throttle)

  override lazy val isFuelStarved: Boolean = stages.forall(_.isEmpty)

  override lazy val deltaV = Stage.deltaV(stages)

  override def physStep(deltaT: Double): Option[StagedRocket] =
    Option(new StagedRocket(
      state.step(deltaT, acceleration),
      attitude,
      throttle,
      steppedStages(deltaT),
      blackBox
    ))

  override def analyzeSteps[U >: StagedRocket <: Steppable[U]](steps: (U, U)): Option[U] = steps match {
    case (step: StagedRocket, nextStep: StagedRocket) => {
      if (step.blackBox.isNewlyTerminated) None
      else {
        val eventLog = new StagedRocketAnalyzer(step, nextStep).analyze
        if (eventLog.isEmpty) Option(nextStep)
        else Option(nextStep ++ eventLog)
      }
    }
    case _ => None
  }

  def ++(eventLog: BlackBox.EventLog[StagedRocket]): StagedRocket =
    new StagedRocket(
      state,
      attitude,
      throttle,
      stages,
      blackBox ++ eventLog
    )

  protected def steppedStages(deltaT: Double): List[Stage] =
    if (stages.isEmpty) Nil
    else {
      val nextStages = if (currentStage.isEmpty && stages.length > 1) stages.tail else stages
      nextStages.head.step(deltaT, atm, throttle) :: nextStages.tail
    }
}