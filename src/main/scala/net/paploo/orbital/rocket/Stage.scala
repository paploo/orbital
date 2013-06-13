package net.paploo.orbital.rocket

/**
 * Stage for staged rockets.
 */
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