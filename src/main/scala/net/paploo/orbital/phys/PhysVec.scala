package net.paploo.orbital.phys

object PhysVec {
  val zero = PhysVec(0.0, 0.0, 0.0)

  /** Build a new 2D vector from rectangular coordinates.*/
  def RectVec(x: Double, y: Double): PhysVec = PhysVec(x, y, 0.0)

  /** Build a new 3D vector from rectangular coordinates. */
  def RectVec(x: Double, y: Double, z: Double): PhysVec = PhysVec(x, y, z)

  /** Build a new 2D vector from polar coordinates. */
  def PolarVec(r: Double, phi: Double): PhysVec =
    PhysVec(r * math.cos(phi), r * math.sin(phi), 0.0)

  /** Build a new 3D vector from spherical coordinates */
  def SphericalVec(r: Double, phi: Double, th: Double) = PhysVec(
    r * math.sin(th) * math.cos(phi),
    r * math.sin(th) * math.sin(phi),
    r * math.cos(th)
  )

  /** Calculates the angle between the two vectors.  Is always in range [0,180] */
  def vectorAngle(u: PhysVec, v: PhysVec) =
    math.acos((u dot v) / (u.r * v.r))

  /** Implicit converter of Doubles to a PhysVec aware wrapper. */
  implicit class VecDouble(a: Double) {
    def *(v: PhysVec): PhysVec = v * a
    def /(v: PhysVec): PhysVec = v / a
  }
}

/**
 * A class representing a 3D vector.
 *
 *  One normally instantiates using the helper RectVect, PolarVec and
 *  SphericalVec methods.
 */
case class PhysVec(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) {

  /** radius */
  lazy val r: Double = math.sqrt(x * x + y * y + z * z)

  /** x-y plane variable (theta in 2-D, but confusingly phi in 3-D in physics) */
  lazy val phi: Double = math.atan2(y, z)

  /** Angle down from the z-axis. */
  lazy val th: Double = if (r == 0.0) 0.0 else math.acos(z / r)

  /** Produce a tuple in Rectangular Coordinates, (x,y,z) */
  lazy val toRect: (Double, Double, Double) = (x, y, z)

  /** Produce a tuple in Spherical Coordinates, (r, phi, th) */
  lazy val toSpherical: (Double, Double, Double) = (r, phi, th)

  lazy val unary_- : PhysVec = PhysVec(-x, -y, -z)
  lazy val unary_+ : PhysVec = this
  def +(v: PhysVec): PhysVec = PhysVec(x + v.x, y + v.y, z + v.z)
  def -(v: PhysVec): PhysVec = PhysVec(x - v.x, y - v.y, z - v.z)
  def *(a: Double): PhysVec = PhysVec(x * a, y * a, z * a)
  def /(a: Double): PhysVec = PhysVec(x / a, y / a, z / a)
  def dot(v: PhysVec): Double = x * v.x + y * v.y + z * v.z
  def cross(v: PhysVec): PhysVec = PhysVec(
    y * v.z - z * v.y,
    z * v.x - x * v.z,
    x * v.y - y * v.x
  )
  lazy val sq: Double = this dot this
  lazy val unit: PhysVec =
    if (this.r != 0.0) this / this.r
    else PhysVec.zero
}