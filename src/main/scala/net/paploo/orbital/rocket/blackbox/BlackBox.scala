package net.paploo.orbital.rocket.blackbox

import net.paploo.orbital.rocket.Rocket
import event.Event
import BlackBox.EventLog

object BlackBox {

  /** The type alias for event lists on the BlackBox. */
  type EventLog[+T <: Rocket[T]] = Vector[Event[T]]

  /**
   * Returns a new immutable black box with a start event already placed on it.
   *
   *  This is a common operation when creating a new rocket.
   */
  def newBox[T <: Rocket[T]](rocket: T) = new immutable.BlackBox[T](Vector.empty, Vector(event.SimulationStartEvent(rocket)))
}

/**
 * The BlackBox acts as a flight recorder, storing a log of events for the flight.
 */
trait BlackBox[+T <: Rocket[T]] {

  /** The events for the flight; this includes the current events */
  def events: EventLog[T]

  /** The events added during the current step, if any */
  def currentEvents: EventLog[T]

  /** Returns true if there are current events */
  def hasNewEvents: Boolean = !(currentEvents.isEmpty)

  /** Appends new events to the black box, making them the current events, and returning a copy */
  def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U]
}

package immutable {

  /** An immutable implementation of the BlackBox trait */
  class BlackBox[+T <: Rocket[T]](override val events: EventLog[T], override val currentEvents: EventLog[T]) extends net.paploo.orbital.rocket.blackbox.BlackBox[T] {

    override def hasNewEvents: Boolean = !(currentEvents.isEmpty)

    override def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U] =
      new BlackBox[U](events ++ newEvents, newEvents)

  }

}

package mutable {

  /** A mutable implementation of the BlackBox trait. */
  class BlackBox[T <: Rocket[T]] extends net.paploo.orbital.rocket.blackbox.BlackBox[T] {

    var events: EventLog[T] = Vector.empty

    var currentEvents: EventLog[T] = Vector.empty

    /** Returns a copy of the BlackBox with new events added to the BlackBox, making them the current events */
    override def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U] = {
      val box = new BlackBox[U]
      box.events = events ++ newEvents
      box.currentEvents = newEvents
      box
    }

    /** Adds events to the BlackBox, making them current.  This mutates the BlackBox in place. */
    def +=(newEvents: EventLog[T]): BlackBox[T] = {
      events = events ++ newEvents
      currentEvents = newEvents
      this
    }

  }

}