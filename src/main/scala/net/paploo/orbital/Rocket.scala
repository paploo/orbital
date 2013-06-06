package net.paploo.orbital

import PhysVec.{ SphericalVec, VecDouble }

object Rocket {
  val ispSurfaceGravity = 9.8072
}

class Rocket(val state: State, val mass: Double, val planetoid: Planetoid) extends Steppable[Rocket] with OrbitalParameters {
  lazy val force: PhysVec = gravForce + dragForce

  lazy val acceleration: PhysVec = force / mass

  override def step(deltaT: Double): Rocket =
    new Rocket(steppedState(deltaT), mass, planetoid)

  lazy val gravForce: PhysVec =
    -(planetoid.mu / (pos.sq)) * pos.unit

  lazy val dragForce: PhysVec = PhysVec.zero // TODO: Implement against planetoid.

  override def toString = s"Rocket($state, $mass, ${planetoid.name})"

  protected def steppedState(deltaT: Double): State = state.step(deltaT, acceleration)
}

/**
 * Abstract superclass of powered rockets.
 *
 *  In additional to the base class properties, it also takes a throttle setting,
 *  between 0.0 and 1.0, and an attitude vector (relative to the inertial frame).
 */
abstract class PoweredRocket(state: State, mass: Double, planetoid: Planetoid,
                             val throttle: Double, val attitude: PhysVec)
  extends Rocket(state, mass, planetoid) {

  override lazy val force: PhysVec = gravForce + dragForce + thrustForce

  def thrustForce: PhysVec = SphericalVec(thrust, attitude.phi, attitude.th)

  def thrust: Double

  def massFlow: Double
}

class StagedRocket(state: State, planetoid: Planetoid,
                   throttle: Double, attitude: PhysVec, val stages: List[Stage])
  extends PoweredRocket(state, stages.foldLeft(0.0)(_ + _.mass), planetoid, throttle, attitude) {

  lazy val currentStage = stages.head

  lazy val atm = planetoid.atm(pos)

  lazy val thrust: Double = currentStage.thrust(atm, throttle)

  lazy val massFlow: Double = currentStage.massFlow(atm, throttle)

  override def step(deltaT: Double): Rocket =
    new StagedRocket(steppedState(deltaT), planetoid, throttle, attitude, steppedStages(deltaT))

  protected def steppedStages(deltaT: Double): List[Stage] = {
    val stagesList = if (currentStage.isEmpty && !stages.isEmpty) stages.tail else stages
    stagesList.head.step(deltaT, atm, throttle) :: stagesList.tail
  }
}

case class Stage(
  mass: Double,
  emptyMass: Double,
  maxThrust: Double,
  atmIsp: Double,
  vacIsp: Double,
  dragCoeff: Double) {

  def isEmpty: Boolean = mass <= emptyMass

  def isp(atm: Double) = atm * atmIsp + (1.0 - atm) * vacIsp

  def thrust(atm: Double, throttle: Double) =
    if (isEmpty) 0.0
    else throttle * maxThrust

  def massFlow(atm: Double, throttle: Double) =
    thrust(atm, throttle) / (isp(atm) * Rocket.ispSurfaceGravity)

  def step(deltaT: Double, atm: Double, throttle: Double): Stage = {
    if (isEmpty) this
    else new Stage(deltaMass(deltaT, atm, throttle), emptyMass, maxThrust, atmIsp, vacIsp, dragCoeff)
  }

  protected def deltaMass(deltaT: Double, atm: Double, throttle: Double) =
    mass - massFlow(atm, throttle) * deltaT
}