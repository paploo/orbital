package net.paploo.orbital.rocket.blackbox

import scala.collection.immutable.VectorBuilder
import net.paploo.orbital.rocket.Rocket
import event.Event
import BlackBox.EventLog

object BlackBox {

  /** The type alias for event lists on the BlackBox. */
  type EventLog[+T <: Rocket[T]] = Vector[Event[T]]

  /** Type for a mutable type for building EventLogs efficiently. */
  type EventLogBuilder[T <: Rocket[T]] = VectorBuilder[Event[T]]

  def empty[T <: Rocket[T]] = new immutable.BlackBox[T](Vector.empty, Vector.empty)

  /**
   * Returns a new immutable black box with a start event already placed on it.
   *
   *  This is a common operation when creating a new rocket.
   */
  def newBox[T <: Rocket[T]](rocket: T) = new immutable.BlackBox[T](Vector.empty, Vector(event.control.StartOfSimulationEvent(rocket)))
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

  /**
   *  Returns true if any of the new events are termination events.
   *
   *  This is more performant for checking for termination as it only
   *  checks new events.
   */
  def isNewlyTerminated: Boolean = currentEvents.exists(_.isTerminationEvent)

  /**
   * Returns true if the events log contains a termination event.
   *
   * If you know the termination event must be newly created (is in the currentEvents)
   * then it is more performant to check isNewlyTerminated.
   */
  def isTerminated: Boolean = events.exists(_.isTerminationEvent)

  /** Appends new events to the black box, making them the current events, and returning a copy */
  def ++[U >: T <: Rocket[U]](newEvents: EventLog[U]): BlackBox[U]

  override def toString = s"${getClass.getSimpleName}(${events.length} events:\n${events.mkString("\n")})"
}

package immutable {

  /** An immutable implementation of the BlackBox trait */
  class BlackBox[+T <: Rocket[T]](override val events: EventLog[T], override val currentEvents: EventLog[T]) extends net.paploo.orbital.rocket.blackbox.BlackBox[T] {

    override lazy val hasNewEvents: Boolean = super.hasNewEvents

    override lazy val isNewlyTerminated: Boolean = super.isNewlyTerminated

    override lazy val isTerminated: Boolean = super.isTerminated

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