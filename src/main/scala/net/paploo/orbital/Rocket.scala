package net.paploo.orbital

import PhysVec.{ SphericalVec, VecDouble }

object Rocket {
  val ispSurfaceGravity = 9.8072
}

class Rocket(val state: State, val mass: Double, val planetoid: Planetoid) {
  lazy val pos = state.pos
  lazy val vel = state.vel

  lazy val force: PhysVec = gravForce + dragForce

  lazy val acceleration: PhysVec = force / mass

  lazy val specificMomentum = state.specificMomentum
  lazy val specificAngularMoment = state.specificAngularMoment
  lazy val specificKineticEnergy = state.specificKineticEnergy
  lazy val specificPotentialEnergy = -planetoid.mu / pos.r
  lazy val specificEnergy = specificKineticEnergy + specificPotentialEnergy

  lazy val eccentricity: Double = {
    val num = 2.0 * specificAngularMoment.sq * specificEnergy
    val den = planetoid.mu * planetoid.mu
    val radicand = 1.0 + num / den
    // Sometimes rounding errors on near circular orbits make the radicand negative, so make zero when that happens.
    if (radicand < 0.0) 0.0
    else math.sqrt(radicand)
  }

  lazy val semimajorAxis =
    if (specificEnergy == 0.0) Double.NaN
    else -(planetoid.mu) / (2.0 * specificEnergy)

  lazy val apses: (Double, Double) =
    // When parabolic we hit a special case where the semimajor axis is undefined.
    if (specificEnergy == 0.0) {
      val periapsis = specificAngularMoment.sq / (2 * planetoid.mu)
      val apoapsis = Double.PositiveInfinity
      (periapsis, apoapsis)
    } else {
      val periapsis = semimajorAxis * (1.0 + eccentricity)
      val apoapsis = semimajorAxis * (1.0 - eccentricity)
      (periapsis, apoapsis)
    }

  lazy val period: Double =
    if (specificEnergy < 0.0)
      (math.Pi * planetoid.mu) / math.sqrt(-2.0 * specificEnergy * specificEnergy * specificEnergy)
    else
      Double.NaN

  def step(deltaT: Double): Rocket =
    new Rocket(steppedState(deltaT), mass, planetoid)

  lazy val gravForce: PhysVec =
    -(planetoid.mu / (pos.sq)) * pos.unit

  lazy val dragForce: PhysVec = PhysVec.zero // TODO: Implement against planetoid.

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