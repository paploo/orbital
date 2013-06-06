package net.paploo.orbital

import scala.annotation.tailrec

trait Steppable[T <: Steppable[T]] {
  this: T =>

  def step(deltaT: Double): T

  def steps(deltaT: Double): Stream[T] =
    this #:: step(deltaT).steps(deltaT)

  def stepsWhile(cond: T => Boolean)(deltaT: Double): Stream[T] = this #:: {
    val next = step(deltaT)
    if (cond(next)) next.stepsWhile(cond)(deltaT) else Stream.empty
  }
}