import net.paploo.orbital.rocket._
import net.paploo.orbital.phys.{State, PhysVec}
import net.paploo.orbital.phys.PhysVec._
import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.rocket.blackbox.BlackBox

object orbital {
	val initialState = State(0.0, PhysVec(700000.0, 0.0), PhysVec(0.0, 2300.0), Planetoid.kerbin)
                                                  //> initialState  : net.paploo.orbital.phys.State = State(0.0,PhysVec(700000.0,0
                                                  //| .0,0.0),PhysVec(0.0,2300.0,0.0),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,
                                                  //| 5000.0))
	val initialRocket = new UnpoweredRocket(initialState, 10.0, BlackBox.empty)
                                                  //> initialRocket  : net.paploo.orbital.rocket.UnpoweredRocket = UnpoweredRocket
                                                  //| (State(0.0,PhysVec(700000.0,0.0,0.0),PhysVec(0.0,2300.0,0.0),Planetoid(Kerbi
                                                  //| n,3.5316E12,600000.0,21600.0,5000.0)))
	val rocket = initialRocket ++ BlackBox.EventLog(event.control.StartOfSimulationEvent(initialRocket))
                                                  //> rocket  : net.paploo.orbital.rocket.UnpoweredRocket = UnpoweredRocket(State(
                                                  //| 0.0,PhysVec(700000.0,0.0,0.0),PhysVec(0.0,2300.0,0.0),Planetoid(Kerbin,3.531
                                                  //| 6E12,600000.0,21600.0,5000.0)))
	//val rocket = initialRocket
	rocket.apses                              //> res0: (Double, Double) = (700000.0,771412.4159276232)
	rocket.period                             //> res1: Double = 2109.8459440561614
	
	val start = System.currentTimeMillis      //> start  : Long = 1371348224782
	val last = rocket.runWhile(_.state.t < rocket.period)(0.0001)
                                                  //> last  : net.paploo.orbital.rocket.UnpoweredRocket = UnpoweredRocket(State(21
                                                  //| 09.8458996826425,PhysVec(700001.268745336,-6.959514635671901,0.0),PhysVec(0.
                                                  //| 02180847500915307,2299.9979904408033,0.0),Planetoid(Kerbin,3.5316E12,600000.
                                                  //| 0,21600.0,5000.0)))
	val stop = System.currentTimeMillis       //> stop  : Long = 1371348233126
	
	stop - start                              //> res2: Long = 8344
	
	last.state.t                              //> res3: Double = 2109.8458996826425
	last.state                                //> res4: net.paploo.orbital.phys.State = State(2109.8458996826425,PhysVec(70000
                                                  //| 1.268745336,-6.959514635671901,0.0),PhysVec(0.02180847500915307,2299.9979904
                                                  //| 408033,0.0),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0))
	last.apses                                //> res5: (Double, Double) = (700001.2687783306,771413.919850391)
	last.period                               //> res6: Double = 2109.851907688307
	
	last.blackBox                             //> res7: net.paploo.orbital.rocket.blackbox.BlackBox[net.paploo.orbital.rocket.
                                                  //| UnpoweredRocket] = BlackBox(2 events:
                                                  //| StartOfSimulationEvent(UnpoweredRocket(State(0.0,PhysVec(700000.0,0.0,0.0),P
                                                  //| hysVec(0.0,2300.0,0.0),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0)))
                                                  //| )
                                                  //| ApoapsisEvent(UnpoweredRocket(State(1054.9279998068275,PhysVec(-771413.16783
                                                  //| 36244,-9.706352838163031,0.0),PhysVec(0.026287700627993654,-2087.07969045544
                                                  //| 77,0.0),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0)))))
}