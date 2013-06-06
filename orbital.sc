import net.paploo.orbital._
import PhysVec._

object orbital {

	val initialState = State(0.0, PhysVec(700000.0, 0.0), PhysVec(0.0, 2300.0))
                                                  //> initialState  : net.paploo.orbital.State = State(0.0,PhysVec(700000.0,0.0,0.
                                                  //| 0),PhysVec(0.0,2300.0,0.0))
	val rocket = new Rocket(initialState, 10.0, Planetoid.kerbin)
                                                  //> rocket  : net.paploo.orbital.Rocket = Rocket(State(0.0,PhysVec(700000.0,0.0,
                                                  //| 0.0),PhysVec(0.0,2300.0,0.0)), 10.0, Kerbin)

	rocket.apses                              //> res0: (Double, Double) = (700000.0,771412.4159276232)
	rocket.period                             //> res1: Double = 2109.8459440561614
	
	rocket.stepsWhile(_.state.t < rocket.period)(0.01).take(10).toList
                                                  //> res2: List[net.paploo.orbital.Rocket] = List(Rocket(State(0.0,PhysVec(700000
                                                  //| .0,0.0,0.0),PhysVec(0.0,2300.0,0.0)), 10.0, Kerbin), Rocket(State(0.01,PhysV
                                                  //| ec(699999.9999639633,23.0,0.0),PhysVec(-0.0072073469387755095,2300.0,0.0)), 
                                                  //| 10.0, Kerbin), Rocket(State(0.02,PhysVec(699999.9998558532,45.99999999881594
                                                  //| ,0.0),PhysVec(-0.014414693866621614,2299.999999763187,0.0)), 10.0, Kerbin), 
                                                  //| Rocket(State(0.03,PhysVec(699999.9996756695,68.99999999407969,0.0),PhysVec(-
                                                  //| 0.0216220407616795,2299.9999992895614,0.0)), 10.0, Kerbin), Rocket(State(0.0
                                                  //| 4,PhysVec(699999.9994234124,91.99999998342311,0.0),PhysVec(-0.02882938760209
                                                  //| 0358,2299.999998579123,0.0)), 10.0, Kerbin), Rocket(State(0.05,PhysVec(69999
                                                  //| 9.9990990817,114.99999996447809,0.0),PhysVec(-0.036036734365995374,2299.9999
                                                  //| 976318713,0.0)), 10.0, Kerbin), Rocket(State(0.060000000000000005,PhysVec(69
                                                  //| 9999.9987026777,137.99999993487648,0.0),PhysVec(-0.04324408103153574,2299.99
                                                  //| 99964478075,0.0)), 10.0,
                                                  //| Output exceeds cutoff limit.
	
	val last = rocket.stepsWhile(_.state.t < rocket.period)(0.01).last
                                                  //> last  : net.paploo.orbital.Rocket = Rocket(State(2109.8399999996323,PhysVec(
                                                  //| 293815.063690854,4543249.095480515,0.0),PhysVec(-218.90019383236972,2094.802
                                                  //| 50699842,0.0)), 10.0, Kerbin)
	last.state.t                              //> res3: Double = 2109.8399999996323
	last.state                                //> res4: net.paploo.orbital.State = State(2109.8399999996323,PhysVec(293815.063
                                                  //| 690854,4543249.095480515,0.0),PhysVec(-218.90019383236972,2094.80250699842,0
                                                  //| .0))
	last.apses                                //> res5: (Double, Double) = (324090.42097674054,-2772596.8220999087)
	last.period                               //> res6: Double = NaN
}