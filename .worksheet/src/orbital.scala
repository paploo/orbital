import net.paploo.orbital._
import PhysVec._

object orbital {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(140); 

	val initialState = State(0.0, PhysVec(700000.0, 0.0), PhysVec(0.0, 2300.0));System.out.println("""initialState  : net.paploo.orbital.State = """ + $show(initialState ));$skip(63); 
	val rocket = new Rocket(initialState, 10.0, Planetoid.kerbin);System.out.println("""rocket  : net.paploo.orbital.Rocket = """ + $show(rocket ));$skip(15); val res$0 = 

	rocket.apses;System.out.println("""res0: (Double, Double) = """ + $show(res$0));$skip(15); val res$1 = 
	rocket.period;System.out.println("""res1: Double = """ + $show(res$1));$skip(70); val res$2 = 
	
	rocket.stepsWhile(_.state.t < rocket.period)(0.01).take(10).toList;System.out.println("""res2: List[net.paploo.orbital.Rocket] = """ + $show(res$2));$skip(70); 
	
	val last = rocket.stepsWhile(_.state.t < rocket.period)(0.01).last;System.out.println("""last  : net.paploo.orbital.Rocket = """ + $show(last ));$skip(14); val res$3 = 
	last.state.t;System.out.println("""res3: Double = """ + $show(res$3));$skip(12); val res$4 = 
	last.state;System.out.println("""res4: net.paploo.orbital.State = """ + $show(res$4));$skip(12); val res$5 = 
	last.apses;System.out.println("""res5: (Double, Double) = """ + $show(res$5));$skip(13); val res$6 = 
	last.period;System.out.println("""res6: Double = """ + $show(res$6))}
}
