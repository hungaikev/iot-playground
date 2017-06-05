package practice.iot.lightbend

import akka.actor._

/**
  * Created by rodney on 6/5/17.
  */
object OurApp extends App {

  import Calculator._

  case class Stupid(n: String = "Just print")

  val system = ActorSystem("innovation-centre")

  // val greeter = system.actorOf(Greeter.props, "greeter-actor")

  val calculator = system.actorOf(Calculator.props(),"calculator")

  calculator ! Multiply(2,5)
  calculator ! Multiply(10,10)



  system.terminate()



}
