package net.paploo.orbital

trait OrbitalParameters {
  val state: State
  val planetoid: Planetoid

  val pos: PhysVec = state.pos
  val vel: PhysVec = state.vel

  lazy val specificMomentum: PhysVec = vel
  lazy val specificAngularMoment: PhysVec = pos cross vel

  lazy val specificKineticEnergy: Double = 0.5 * vel.sq
  lazy val specificPotentialEnergy: Double = -planetoid.mu / pos.r
  lazy val specificEnergy: Double = specificKineticEnergy + specificPotentialEnergy

  lazy val eccentricityVector: PhysVec = ((vel cross specificAngularMoment) / planetoid.mu) - (pos.unit)
  lazy val eccentricity: Double = eccentricityVector.r

  lazy val trueAnomaly: Double =
    if ((pos dot vel) >= 0) PhysVec.vectorAngle(pos, vel)
    else 2.0 * math.Pi - PhysVec.vectorAngle(pos, vel)

  lazy val semimajorAxis: Double =
    if (specificEnergy == 0.0) Double.NaN
    else -(planetoid.mu) / (2.0 * specificEnergy)

  //TODO: Apses doesn't always return the smallest number as the periapsis.
  lazy val apses: (Double, Double) =
    // When parabolic we hit a special case where the semimajor axis is undefined.
    if (specificEnergy == 0.0) {
      val periapsis = specificAngularMoment.sq / (2.0 * planetoid.mu)
      val apoapsis = Double.PositiveInfinity
      (periapsis, apoapsis)
    } else {
      val periapsis = semimajorAxis * (1.0 - eccentricity)
      val apoapsis = semimajorAxis * (1.0 + eccentricity)
      (periapsis, apoapsis)
    }

  lazy val apsisVectors: (PhysVec, PhysVec) = {
    val eccentricityUnitVector = eccentricityVector.unit
    val (periapsis, apoapsis) = apses
    (periapsis * eccentricityUnitVector, apoapsis * eccentricityUnitVector)
  }

  lazy val period: Double =
    if (specificEnergy < 0.0)
      (math.Pi * planetoid.mu) / math.sqrt(-2.0 * specificEnergy * specificEnergy * specificEnergy)
    else
      Double.NaN
}