package practice.iot.lightbend


import akka.actor.{Actor,ActorLogging,Props}

/**
  * Created by rodney on 6/5/17.
  */

object MultiplyActor {

  def props(): Props = Props(new MultiplyActor)

}
class MultiplyActor extends Actor with ActorLogging {

  import Calculator._

  override def preStart(): Unit = log.info("About to do some multiplication")

  override def postStop(): Unit = log.info("Stopping multiplication")

  override def receive: Receive = {
    case msg: Multiply =>
      log.info("Received this {} ", msg)
      val calc = mult(msg.rhs,msg.lhs)
      log.info("Sending this calculation back {}", calc)
      sender() ! Value(calc)
  }

  def mult(r: Int,l: Int): Int = r * l

}
