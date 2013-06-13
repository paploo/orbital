package net.paploo.orbital.rocket

import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.phys.{ PhysVec, State, OrbitalParameters }
import net.paploo.orbital.phys.PhysVec.{ SphericalVec, VecDouble }

import net.paploo.orbital.Steppable

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

  lazy val force: PhysVec = gravForce + dragForce + thrustForce

  lazy val acceleration: PhysVec = force / mass

  lazy val gravForce: PhysVec =
    -((planetoid.mu * mass) / (pos.sq)) * pos.unit

  lazy val dragForce: PhysVec = {
    val magnitude = 0.5 * planetoid.density(pos) * vel.sq * mass * Rocket.standardDragCoefficient * Rocket.crossSectionalArea
    -magnitude * vel.unit
  }

  lazy val thrustForce: PhysVec = SphericalVec(thrust, attitude.phi, attitude.th)

  override def toString = s"${getClass.getSimpleName}($state)"
}

/** An concrete unpowered Rocket superclass. */
class UnpoweredRocket(override val state: State,
                      override val mass: Double)
  extends Rocket[UnpoweredRocket] {

  /* Overriding this to remove the thrust component reduces computation time by 25% */
  override lazy val force = gravForce + dragForce

  override val thrust = 0.0

  override val massFlow = 0.0

  override val attitude = PhysVec.zero

  override def step(deltaT: Double): UnpoweredRocket =
    new UnpoweredRocket(state.step(deltaT, acceleration), mass)
}

/**
 * StagedRockets are PoweredRockets with a list of Stages to burn through.
 *
 * The simplest powered rocket is merely a staged rocket with one stage.
 */
class StagedRocket(override val state: State,
                   override val attitude: PhysVec,
                   val throttle: Double,
                   val stages: List[Stage])
  extends Rocket[StagedRocket]
  with Steppable[StagedRocket] {

  lazy val mass = stages.foldLeft(0.0)(_ + _.mass)

  lazy val currentStage = stages.head

  lazy val atm = planetoid.atm(pos)

  lazy val thrust: Double = currentStage.thrust(atm, throttle)

  lazy val massFlow: Double = currentStage.massFlow(atm, throttle)

  override def step(deltaT: Double): StagedRocket =
    new StagedRocket(
      state.step(deltaT, acceleration),
      attitude,
      throttle,
      steppedStages(deltaT)
    )

  protected def steppedStages(deltaT: Double): List[Stage] = {
    val stagesList = if (currentStage.isEmpty && !stages.isEmpty) stages.tail else stages
    stagesList.head.step(deltaT, atm, throttle) :: stagesList.tail
  }
}