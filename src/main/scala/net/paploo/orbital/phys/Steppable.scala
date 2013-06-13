package net.paploo.orbital.phys

/**
 * Steppable introduces the basic functionality of stepping through the simulation
 * dependent on the time
 */
trait Steppable[+T <: Steppable[T]] {
  this: T =>

  /** Type for the AnalyzerHook function. **/
  type StepHook = (T, T) => Boolean

  /** The base step method signature; this must be overridden */
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
   *  values, or runAnalysisWhile if you need to work with intermediate values.
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
  def runWhile(cond: T => Boolean)(deltaT: Double): T =
    runWhileHook((_, nextFrame) => cond(nextFrame))(deltaT) //Speed trials say this is as fast as an explicit implementation.

  /**
   * Runs calling the hook at each step, giving both the current and next frame,
   * and stopping when the hook returns false. Returns the last frame for
   * which the hook returned true.
   *
   * While simple hook implementations give basic runWhile and runUntil
   * functionality, the hook function given to runWithHook is meant to be able
   * to do more complex tasks, such as detection of events.  For example,
   * if the radial velocity between two consecutive frames switches signs, then
   * an apsis was found.
   *
   * Implementation of runWhile can be achieved by passing
   * {{{
   * (_, nextFrame) => cond(nextFrame)
   * }}}
   * as the hook; this will return the last step for which the condition was true.
   *
   * If, for example, you wanted the return to be the first frame for which it is
   * not true, you could instead give
   * {{{
   * (frame, _) => cond(frame)
   * }}}
   *
   * The hook can be an instance method on an object. This allows
   * for simple things (e.g. writing to a log), or more complex
   * things (e.g. event detection and notification, or on a mutable object,
   * collection of statistics.
   */
  def runWhileHook(hook: StepHook)(deltaT: Double): T = {
    var step = this
    var nextStep = step.step(deltaT)
    while (hook(step, nextStep)) {
      step = nextStep
      nextStep = step.step(deltaT)
    }
    step
  }
}