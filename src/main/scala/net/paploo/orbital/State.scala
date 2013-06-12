package net.paploo.orbital

import PhysVec.{ VecDouble, RectVec }

object State {
}

/** The state variables (time, position, and velocity) grouped together */
case class State(t: Double, pos: PhysVec, vel: PhysVec, planetoid: Planetoid) {

  def step(deltaT: Double, acceleration: PhysVec): State = {
    val nextVel = vel + acceleration * deltaT
    val nextPos = pos + vel * deltaT + 0.5 * acceleration * deltaT * deltaT
    State(t + deltaT, nextPos, nextVel, planetoid)
  }
}