import net.paploo.orbital.rocket._
import net.paploo.orbital.phys.{State, PhysVec}
import net.paploo.orbital.phys.PhysVec._
import net.paploo.orbital.planetarysystem.Planetoid
import net.paploo.orbital.rocket.blackbox.BlackBox
import net.paploo.orbital.app.SampleLibrary

object orbital {
	// List for my "Gemini 1 Rocket"
	val stages = SampleLibrary.Gemini1Stages(false)
                                                  //> stages  : List[net.paploo.orbital.rocket.Stage] = List(Stage(10.55,2.55,200.
                                                  //| 0,320.0,370.0), Stage(6.05,2.05,200.0,320.0,370.0), Stage(2.9,0.9,0.0,300.0,
                                                  //| 390.0), Stage(0.9,0.9,0.0,0.0,0.0))
  Planetoid.kerbin.surfaceSpeed(0.0)              //> res0: Double = 174.53292519943295
	val initialState = State(
	0.0,
	PhysVec(Planetoid.kerbin.radius + 77.594, 0.0),
	PhysVec(0.0, 0.0),
	Planetoid.kerbin
	)                                         //> initialState  : net.paploo.orbital.phys.State = State(0.0,PhysVec(600077.594
                                                  //| ,0.0,0.0),PhysVec(0.0,0.0,0.0),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5
                                                  //| 000.0))
	val initialRocket = new StagedRocket(initialState, PhysVec(1.0, 0.0), 1.0, stages, BlackBox.empty)
                                                  //> initialRocket  : net.paploo.orbital.rocket.StagedRocket = StagedRocket(State
                                                  //| (0.0,PhysVec(600077.594,0.0,0.0),PhysVec(0.0,0.0,0.0),Planetoid(Kerbin,3.531
                                                  //| 6E12,600000.0,21600.0,5000.0)))
	val rocket = initialRocket ++ BlackBox.EventLog(event.control.StartOfSimulationEvent(initialRocket))
                                                  //> rocket  : net.paploo.orbital.rocket.StagedRocket = StagedRocket(State(0.0,Ph
                                                  //| ysVec(600077.594,0.0,0.0),PhysVec(0.0,0.0,0.0),Planetoid(Kerbin,3.5316E12,60
                                                  //| 0000.0,21600.0,5000.0)))
	
	rocket.thrustForce                        //> res1: net.paploo.orbital.phys.PhysVec = PhysVec(200.0,0.0,1.2246467991473532
                                                  //| E-14)
	rocket.force                              //> res2: net.paploo.orbital.phys.PhysVec = PhysVec(-0.07224863368514889,0.0,1.2
                                                  //| 246467991473532E-14)
  rocket.gravForce                                //> res3: net.paploo.orbital.phys.PhysVec = PhysVec(-200.07224863368515,-0.0,-0.
                                                  //| 0)
  rocket.dragForce                                //> res4: net.paploo.orbital.phys.PhysVec = PhysVec(-0.0,-0.0,-0.0)
	rocket.step(1.0).get.force                //> res5: net.paploo.orbital.phys.PhysVec = PhysVec(0.5542983249755764,0.0,1.224
                                                  //| 632526154844E-14)
                                                  
  rocket.deltaV                                   //> res6: Double = 6555.082060401877
                                                  
	//val rocket = initialRocket
	rocket.apses                              //> res7: (Double, Double) = (0.0,600077.594)
	rocket.period                             //> res8: Double = 549.4905621404731
	
	val start = System.currentTimeMillis      //> start  : Long = 1371534054089
	val last = rocket.runWhile(_.state.t < 600.0)(0.0001)
                                                  //> last  : net.paploo.orbital.rocket.StagedRocket = StagedRocket(State(599.999
                                                  //| 9999210237,PhysVec(566719.998328841,0.0,1.926664892680237E-12),PhysVec(-105
                                                  //| .91348607533588,0.0,-3.600716330543232E-16),Planetoid(Kerbin,3.5316E12,6000
                                                  //| 00.0,21600.0,5000.0)))
	val stop = System.currentTimeMillis       //> stop  : Long = 1371534060290
	
	stop - start                              //> res9: Long = 6201
	
	last.state.t                              //> res10: Double = 599.9999999210237
	last.state                                //> res11: net.paploo.orbital.phys.State = State(599.9999999210237,PhysVec(5667
                                                  //| 19.998328841,0.0,1.926664892680237E-12),PhysVec(-105.91348607533588,0.0,-3.
                                                  //| 600716330543232E-16),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0))
	last.apses                                //> res12: (Double, Double) = (0.0,567230.5376112844)
	last.period                               //> res13: Double = 504.9966838164734
	
	last.blackBox                             //> res14: net.paploo.orbital.rocket.blackbox.BlackBox[net.paploo.orbital.rocke
                                                  //| t.StagedRocket] = BlackBox(3 events:
                                                  //| StartOfSimulationEvent(StagedRocket(State(0.0,PhysVec(600077.594,0.0,0.0),P
                                                  //| hysVec(0.0,0.0,0.0),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0))))
                                                  //| PeriapsisEvent(StagedRocket(State(0.2306999999999909,PhysVec(600077.5939685
                                                  //| 642,0.0,1.5979029004783447E-17),PhysVec(1.168146083607087E-8,0.0,1.38543153
                                                  //| 56659312E-16),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0))))
                                                  //| ApoapsisEvent(StagedRocket(State(166.61380000440747,PhysVec(607089.87503741
                                                  //| 31,0.0,2.0357829155865607E-12),PhysVec(-5.463506712102928E-4,0.0,3.43981735
                                                  //| 21815795E-15),Planetoid(Kerbin,3.5316E12,600000.0,21600.0,5000.0)))))
}