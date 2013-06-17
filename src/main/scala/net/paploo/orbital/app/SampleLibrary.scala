package net.paploo.orbital.app

import net.paploo.orbital.rocket._

import net.paploo.orbital.phys.{ State, PhysVec }
import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.rocket.blackbox.BlackBox

object SampleLibrary {

  /**
   *  Basic orbit
   *
   *  Do one 70km x 77.14km orbit around Kerbin.
   */
  def basicOrbit: UnpoweredRocket = {
    val initialState = State(0.0, PhysVec(700000.0, 0.0), PhysVec(0.0, 2300.0), Planetoid.kerbin)

    val initialRocket = new UnpoweredRocket(initialState, 10.0, BlackBox.empty)
    val rocket = initialRocket ++ BlackBox.EventLog(event.control.StartOfSimulationEvent(initialRocket))

    rocket.runWhile(_.t <= rocket.period)(0.0001)
  }

}