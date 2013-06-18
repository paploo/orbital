package net.paploo.orbital.rocket

object Stage {

  def deltaV(stages: List[Stage]): Double =
    massAndDeltaV(stages)._2

  def mass(stages: List[Stage]): Double =
    stages.foldLeft(0.0)(_ + _.mass)

  /**
   * Calculates both mass and deltaV
   *
   * Calculating deltaV requires total mass calculations at various stages. If
   * delegating the mass calculation out, then we repetitively calculate mass
   * eating O(n^2) time doing it.  This gives an O(n) solution to calculate
   * deltaV because it only hits each mass once.
   */
  def massAndDeltaV(stages: List[Stage]): (Double, Double) =
    if (stages.isEmpty) (0.0, 0.0)
    else {
      val (payloadMass, payloadDeltaV) = massAndDeltaV(stages.tail)
      (stages.head.mass + payloadMass, stages.head.deltaV(payloadMass) + payloadDeltaV)
    }

}

/**
 * Stage for staged rockets.
 */
case class Stage(
  mass: Double,
  emptyMass: Double,
  maxThrust: Double,
  atmIsp: Double,
  vacIsp: Double) {

  lazy val isEmpty: Boolean = mass <= emptyMass || maxThrust == 0.0

  def isp(atm: Double) = atm * atmIsp + (1.0 - atm) * vacIsp

  def thrust(atm: Double, throttle: Double) =
    if (isEmpty) 0.0
    else throttle * maxThrust

  def massFlow(atm: Double, throttle: Double) =
    thrust(atm, throttle) / (isp(atm) * Rocket.ispSurfaceGravity)

  def effectiveExhaustVelocity(atm: Double): Double = isp(atm) * Rocket.ispSurfaceGravity

  def deltaV(payloadMass: Double) =
    effectiveExhaustVelocity(0.0) * math.log((mass + payloadMass) / (emptyMass + payloadMass))

  def step(deltaT: Double, atm: Double, throttle: Double): Stage = {
    if (isEmpty) this
    else new Stage(deltaMass(deltaT, atm, throttle), emptyMass, maxThrust, atmIsp, vacIsp)
  }

  protected def deltaMass(deltaT: Double, atm: Double, throttle: Double) =
    mass - massFlow(atm, throttle) * deltaT
}