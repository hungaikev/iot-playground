package practice.iot.lightbend

import akka.actor._
/**
  * Created by rodney on 6/3/17.
  */
object ThirdApp extends App {

  val system = ActorSystem("third-app")


class SupervisedActor extends Actor {

  override def preStart(): Unit = println("supervised-actor started")

  override def postStop(): Unit = println("supervised actor stopped")

  override def receive: Receive = {
    case "fail" => println("supervised actor fails now")
      throw new Exception("I failed!")
  }
}


  class SupervisingActor extends Actor {
    val child = context.actorOf(Props[SupervisedActor],"supervised-actor")

    override def receive: Receive = {
      case "failChild" => child ! "fail"
    }
  }

  val supervisingActor = system.actorOf(Props[SupervisedActor],"supervising-actor")

  supervisingActor ! "failChild"

  system.terminate()

}
