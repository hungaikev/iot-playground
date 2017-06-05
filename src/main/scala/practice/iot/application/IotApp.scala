package practice.iot.application

import akka.actor._
import scala.io.StdIn
/**
  * Created by hungai on 6/3/17.
  */
object IotApp  extends App {

  val system = ActorSystem("iot-system")

  try {
    // Create top level supervisor
    val supervisor = system.actorOf(IotSupervisor.props(),"iot-supervisor")

    //Exit the system after ENTER is pressed.
    println("Press ENTER/RETURN to Exit the Application")

    StdIn.readLine()

  } finally {

    system.terminate()
  }

}
