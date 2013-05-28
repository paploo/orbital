package net.paploo.orbital

import PhysVec.VecDouble

object State {
  //TODO: Add methods for converting state between reference frames?
}

/** The state variables (time, position, and velocity) grouped together */
case class State(t: Double, pos: PhysVec, vel: PhysVec) {
  lazy val specificKineticEnergy: Double = 0.5 * vel.sq
  lazy val specificMomentum: PhysVec = vel
  lazy val specificAngularMoment: PhysVec = pos cross vel

  def step(deltaT: Double, acceleration: PhysVec): State = {
    val nextVel = vel + acceleration * deltaT
    val nextPos = pos + vel * deltaT + 0.5 * acceleration * deltaT * deltaT
    State(t + deltaT, nextPos, nextVel)
  }
}