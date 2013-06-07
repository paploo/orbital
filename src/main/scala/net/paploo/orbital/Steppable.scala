package net.paploo.orbital

import scala.annotation.tailrec

/**
 * Steppable introduces the basic functionality of stepping through the simulation
 * dependent on the time
 */
trait Steppable[+T <: Steppable[T]] {
  this: T =>

  /** The base step method signature; this must be overriden */
  def step(deltaT: Double): T

  /** Produces a stream of steps. */
  def steps(deltaT: Double): Stream[T] =
    this #:: step(deltaT).steps(deltaT)

  /**
   * Produces a stream of steps until the condition evaluates false.
   *
   * The last element is the last element for which the condition was true.
   *
   *  Calling stepsWhile(cond)(deltaT).last runs the heap out quickly, so it
   *  is better to use runWhile if you don't need to work with intermediate
   *  values.
   *
   */
  def stepsWhile(cond: T => Boolean)(deltaT: Double): Stream[T] = this #:: {
    val next = step(deltaT)
    if (cond(next)) next.stepsWhile(cond)(deltaT) else Stream.empty
  }

  /**
   * Runs while the condition is true, returning the last evaluation for which
   * it is true.
   */
  def runWhile(cond: T => Boolean)(deltaT: Double): T = {
    var frame = this
    var nextFrame = frame.step(deltaT)
    while (cond(nextFrame)) {
      frame = nextFrame
      nextFrame = frame.step(deltaT)
    }
    frame
  }
}