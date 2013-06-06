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
	
	
	val last = rocket.stepsWhile(_.state.t < rocket.period)(0.01).last
                                                  //> last  : net.paploo.orbital.Rocket = Rocket(State(2109.8399999996323,PhysVec(
                                                  //| 700126.5417299438,-699.4898838692047,0.0),PhysVec(2.191310137269498,2299.797
                                                  //| 955544316,0.0)), 10.0, Kerbin)
	last.state.t                              //> res2: Double = 2109.8399999996323
	last.state                                //> res3: net.paploo.orbital.State = State(2109.8399999996323,PhysVec(700126.541
                                                  //| 7299438,-699.4898838692047,0.0),PhysVec(2.191310137269498,2299.797955544316,
                                                  //| 0.0))
	last.apses                                //> res4: (Double, Double) = (700126.8749720006,771562.7551663277)
	last.period                               //> res5: Double = 2110.442214842026
}