package practice.iot.lightbend


import akka.actor._
/**
  * Created by rodney on 6/3/17.
  */
object SecondApp extends App {

  val system = ActorSystem("second-app")

  class StartStopActor1 extends Actor {

    override def preStart(): Unit = {
      println("first started")

    }

    override def postStop(): Unit = println("first stopped")

    override def receive: Receive = {
      case "stop" =>
        val second =  context.actorOf(Props[StartStopActor2],"second")
        second ! "stop"
        context.stop(self)
    }

  }

  class StartStopActor2 extends Actor {
    override def preStart(): Unit = println("second started")

    override def postStop(): Unit = println("second stopped")

    //Actor.emptyBehaviour is a useful placeholder when we dont want to handle any messages in the actor

    override def receive: Receive = {
      case "stop" =>
        val third = context.actorOf(Props[StartStopActor3],"third")
        context.stop(self)
    }
  }



  class StartStopActor3 extends Actor {
    override def preStart(): Unit = println("third started")

    override def postStop(): Unit = println("third stopped")

    //Actor.emptyBehaviour is a useful placeholder when we dont want to handle any messages in the actor

    override def receive: Receive = Actor.emptyBehavior
  }

  val first = system.actorOf(Props[StartStopActor1],"first")

  first ! "stop"

  system.terminate()

}
