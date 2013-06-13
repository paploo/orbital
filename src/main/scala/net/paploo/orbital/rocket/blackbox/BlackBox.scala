package net.paploo.orbital.rocket.blackbox

import net.paploo.orbital.rocket.Rocket
import event.Event
import BlackBox.EventLog

object BlackBox {
  type EventLog[+T <: Rocket[T]] = Vector[Event[T]]

  def newBox[T <: Rocket[T]](rocket: T) = new BlackBox[T](Vector.empty, Vector(event.SimulationStartEvent(rocket)))
}

case class BlackBox[+T <: Rocket[T]](events: EventLog[T], newEvents: EventLog[T]) {

  def hasNewEvents: Boolean = !(newEvents.isEmpty)

  def step[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U] =
    new BlackBox[U](events ++ newEvents, newEvents)

}

trait BlackBoxAnalyzer[+T <: Rocket[T]] {
  type AnalyzerRocketType <: Rocket[T]

  def blackBox: BlackBox[T]

  def analyzeStep(rocket: AnalyzerRocketType, nextRocket: AnalyzerRocketType): BlackBox[T]

}