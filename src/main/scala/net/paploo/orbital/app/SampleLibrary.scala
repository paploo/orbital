package net.paploo.orbital.app

import net.paploo.orbital.rocket._

import net.paploo.orbital.phys.{ State, PhysVec }
import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.rocket.blackbox.BlackBox

object SampleLibrary {

  /**
   *  Basic orbit
   *
   *  Do one 70km apoapsis, 77.14km periapsis orbit around Kerbin.
   */
  def basicOrbit: UnpoweredRocket = {
    val orbitRadius = 700000.0
    val circularOrbitSpeed = math.sqrt(Planetoid.kerbin.mu / orbitRadius)
    val initialState = State(0.0, PhysVec(orbitRadius, 0.0), PhysVec(0.0, 2300.0), Planetoid.kerbin)

    val initialRocket = new UnpoweredRocket(initialState, 10.0, BlackBox.empty)
    val rocket = initialRocket ++ BlackBox.EventLog(event.control.StartOfSimulationEvent(initialRocket))

    rocket.runWhile(_.t <= rocket.period * 1.1)(0.0001)
  }

  def stagedVerticalClimb: StagedRocket = {
    val stages = SampleLibrary.Gemini1Stages(false)
    val r = PhysVec(Planetoid.kerbin.radius + 77.594, 0.0)
    val initialState = State(
      0.0,
      r,
      Planetoid.kerbin.linearVelocity(r),
      Planetoid.kerbin
    )

    val initialRocket = new StagedRocket(initialState, PhysVec(1.0, 0.0), 1.0, stages, BlackBox.empty)
    val rocket = initialRocket ++ BlackBox.EventLog(event.control.StartOfSimulationEvent(initialRocket))

    rocket.runWhile(_.t < 600.0)(0.001)
  }

  /**
   * Returns a simple 3-stage rocket that can achieve orbit.
   *
   *  If the 3rd stage is not lit, the 0.21 patch shows this rocket as
   *  getting an orbit of 101,3 km apoapsis, -598.3 km periapsis, taking
   *  5m 32.8s to obtain apoapsis.  Additionally, if the 3rd stage is ejected
   *  immediately when encountered, the ground is hit at 8m 57s. Lastly, it starts
   *  at an altitude of 77.594 meters on the pad.
   */
  def Gemini1Stages(shouldLightThirdStage: Boolean = true): List[Stage] = {
    /* A 1m command module with small parachute */
    val commandModule = Stage(0.9, 0.9, 0.0, 0.0, 0.0)

    /* 1m decoupler, 1m ASAS, 1m medium tank, 1m poodle engine */
    val thirdStageThrust = if (shouldLightThirdStage) 50 else 0.0
    val thirdStage = Stage(2.9, 0.9, thirdStageThrust, 300, 390)

    /* 1m decoupler, 1m tall tank, 1m gimbaled engine */
    val secondStage = Stage(6.05, 2.05, 200, 320, 370)

    /* 1m decoupler, 1m tall tank, 1m tall tank, 1m gimbaled engine */
    val firstStage = Stage(10.55, 2.55, 200, 320, 370)

    List(firstStage, secondStage, thirdStage, commandModule)
  }

}