package practice.iot.lightbend


import akka.actor._

/**
  * Created by rodney on 6/3/17.
  */
object MyApp extends App  {

  val system = ActorSystem("iot-system")

  class PrintMyActorRefActor extends Actor {
    override def receive: Receive = {
      case "printit" =>
        val secondActorRef: ActorRef = context.actorOf(Props.empty, "second-actor")
        println(s"Second: $secondActorRef")

    }
  }


  val firstRef: ActorRef = system.actorOf(Props[PrintMyActorRefActor],"first-actor")
  println(s"First: $firstRef")

  firstRef ! "printit"

  system.terminate()
}
