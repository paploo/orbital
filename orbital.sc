import net.paploo.orbital._
import PhysVec._

object orbital {

	val initialState = State(0.0, PhysVec(700000.0, 0.0), PhysVec(0.0, 2300.0))
                                                  //> initialState  : net.paploo.orbital.State = State(0.0,PhysVec(700000.0,0.0,0.
                                                  //| 0),PhysVec(0.0,2300.0,0.0))
	val rocket = new UnpoweredRocket(initialState, Planetoid.kerbin, 10.0)
                                                  //> rocket  : net.paploo.orbital.UnpoweredRocket = Rocket(State(0.0,PhysVec(7000
                                                  //| 00.0,0.0,0.0),PhysVec(0.0,2300.0,0.0)), 10.0, Kerbin)
	rocket.apses                              //> res0: (Double, Double) = (700000.0,771412.4159276232)
	rocket.period                             //> res1: Double = 2109.8459440561614
	
	val start = System.currentTimeMillis      //> start  : Long = 1371012628978
	val last = rocket.runWhile(_.state.t < rocket.period)(0.0001)
                                                  //> last  : net.paploo.orbital.UnpoweredRocket = Rocket(State(2109.8458996826425
                                                  //| ,PhysVec(700001.268745336,-6.959514635671901,0.0),PhysVec(0.0218084750091530
                                                  //| 7,2299.9979904408033,0.0)), 10.0, Kerbin)
	val stop = System.currentTimeMillis       //> stop  : Long = 1371012634415
	stop - start                              //> res2: Long = 5437
	last.state.t                              //> res3: Double = 2109.8458996826425
	last.state                                //> res4: net.paploo.orbital.State = State(2109.8458996826425,PhysVec(700001.268
                                                  //| 745336,-6.959514635671901,0.0),PhysVec(0.02180847500915307,2299.997990440803
                                                  //| 3,0.0))
	last.apses                                //> res5: (Double, Double) = (700001.2687783306,771413.919850391)
	last.period                               //> res6: Double = 2109.851907688307
}