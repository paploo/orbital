package net.paploo.orbital.phys

/**
 * Steppable introduces the basic functionality of stepping through the simulation
 * dependent on the time.
 *
 * TODO: Stepping should happen in two steps:
 * 1. Call step to get intermediate step.
 * 2. Pass current and next steps to an analysis function.
 * 3. Return value from analysis function (it can replace/mutate or pass-through).
 * Stepping should return an Option[Steppable] value, so that termination can
 * be met when no value is produced.
 */
trait Steppable[+T <: Steppable[T]] {
  this: T =>

  /**
   * The base method of steppable; calculates the next step of the simulation.
   *
   *  Calculation is broken into two steps:
   *  1. The physical step increment, via the physStep method, calculates the
   *     next step using the current step only.
   *  2. The current nad next step are fed into the analyzeStep method, which
   *     has the option to react and override the nextStep in response to
   *     changes, such as the computation and addition of events to the flight
   *     recorder.
   *
   *  Returns the new step as an Option, with None if the simulation should
   *  be terminated.
   */
  def step(deltaT: Double): Option[T] = {
    physStep(deltaT) match {
      case None => None
      case Some(nextStep) => analyzeStep((this, nextStep))
    }
  }

  /**
   * Calculates the next step given the current step, or returns None if the
   *  simulation should be terminated.
   *
   *  This is normally used for calculating motion equations and other time
   *  dependent information.
   */
  def physStep(deltaT: Double): Option[T]

  /**
   * Analyzes the current step and next step and returns the next step.
   *
   *  The default implemetnation is to return the next step unaltered, as is
   *  usually the desired case.  However more complex functionality, such
   *  as flight recorders, need to geenerate events based on comparisons between
   *  steps.  This allows those comparisons to be made and their results to be
   *  inserted into the result of the step.
   *
   *  Given the variance of this method, it is usually necessary to match on
   *  the type T to work with its parameters directly.
   */
  def analyzeStep[U >: T <: Steppable[U]](steps: (U, U)): Option[U] = Some(steps._2)

  /**
   * Gives a stream of steps.
   *
   *  This method is highly non-performant as Scala seems bad at garbage
   *  collecting the steps when they are no longer being used.
   */
  def steps(deltaT: Double): Stream[T] = this #:: (step(deltaT) match {
    case Some(nextStep) => nextStep.steps(deltaT)
    case None => Stream.empty
  })

  /**
   * Runs while the given predicate is true, and returns the last step
   *  for which it is true.
   *
   *  Note that the loop may have exited because a step returned None rather
   *  than the predicate being false.
   */
  def runWhile(p: T => Boolean)(deltaT: Double): T =
    runWhileCallback((_, nextFrame) => p(nextFrame))(deltaT)

  /**
   * Runs while the given callback--which is given the current step and
   *  next step--is true.
   *
   *  The callback may have side effects in the application, such as generating
   *  events to update a user interface.
   *
   *  runWhile is a subset of this method, and can be achieved by using the predicate
   *  {{{
   *  (_, nextFrame) => predicate(nextFrame)
   *  }}}
   *
   *  As another example, one could instead return the first step for which the
   *  condition is false rather than the last step for which it is true like
   *  above, with
   *  {{{
   *  (currentFrame, _) => predicate(currentFrame)
   *  }}}
   */
  def runWhileCallback(callback: (T, T) => Boolean)(deltaT: Double): T = {
    var currentStep: Option[T] = Some(this)
    var nextStep: Option[T] = currentStep.get.step(deltaT)
    while (!(nextStep.isEmpty) && callback(currentStep.get, nextStep.get)) {
      currentStep = nextStep
      nextStep = currentStep.get.step(deltaT)
    }
    currentStep.get
  }
}