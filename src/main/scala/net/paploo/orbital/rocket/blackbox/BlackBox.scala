package net.paploo.orbital.rocket.blackbox

import net.paploo.orbital.rocket.Rocket
import event.Event
import BlackBox.EventLog

trait BlackBoxAnalyzer[+T <: Rocket[T]] {
  type AnalyzerRocketType <: Rocket[T]

  def analyzeStep(rocket: AnalyzerRocketType, nextRocket: AnalyzerRocketType): BlackBox[T]
}

object BlackBox {
  type EventLog[+T <: Rocket[T]] = Vector[Event[T]]

  def newBox[T <: Rocket[T]](rocket: T) = new immutable.BlackBox[T](Vector.empty, Vector(event.SimulationStartEvent(rocket)))
}

trait BlackBox[+T <: Rocket[T]] {

  def events: EventLog[T]

  def currentEvents: EventLog[T]

  def hasNewEvents: Boolean = !(currentEvents.isEmpty)

  def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U]
}

package immutable {

  class BlackBox[+T <: Rocket[T]](override val events: EventLog[T], override val currentEvents: EventLog[T]) extends net.paploo.orbital.rocket.blackbox.BlackBox[T] {

    override def hasNewEvents: Boolean = !(currentEvents.isEmpty)

    override def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U] =
      new BlackBox[U](events ++ newEvents, newEvents)

  }

}

package mutable {

  class BlackBox[T <: Rocket[T]] extends net.paploo.orbital.rocket.blackbox.BlackBox[T] {

    var events: EventLog[T] = Vector.empty

    var currentEvents: EventLog[T] = Vector.empty

    override def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U] = {
      val box = new BlackBox[U]
      box.events = events ++ newEvents
      box.currentEvents = newEvents
      box
    }

    def +=(newEvents: EventLog[T]): BlackBox[T] = {
      events = events ++ newEvents
      currentEvents = newEvents
      this
    }

  }

}