package practice.iot.lightbend

import akka.actor._

/**
  * Created by rodney on 6/5/17.
  */

object Greeter {

  def props: Props = Props(new Greeter)

  case object Greet
  case object  Done
  case class GreetByName(name: String)

}

class Greeter extends Actor with ActorLogging {

  import Greeter._

  override def preStart(): Unit = log.info( "Starting the Greeter actor")

  override def postStop(): Unit = log.info( "Actor has been stopped")

  override def receive: Receive = {
    case Greet =>
      log.info(" Hello world")
      sender() ! Done
    case GreetByName(name) =>
      log.info("Hi {}", name)
    case _ =>
      log.info("This is an error, Please register")

  }

}
