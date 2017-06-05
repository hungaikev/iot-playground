package practice.iot.lightbend

import akka.actor._

/**
  * Created by rodney on 6/5/17.
  */

object Calculator {

  def props(): Props = Props(new Calculator)


  case class Add(rhs: Int, lhs: Int)
  case class Subtract(rhs: Int, lhs: Int)
  case class Multiply(rhs: Int, lhs: Int)
  case class Value(v: Int)


}



class Calculator extends Actor with ActorLogging {

  import Calculator._

  override def preStart(): Unit = log.info("Starting our Calculator")

  override def postStop(): Unit = log.info("Stopping our calculator")

  override def receive: Receive = {

    case msg: Multiply =>
     val multiplier =  context.actorOf(MultiplyActor.props(),"multiplier")
      log.info("Sending {} to actor {}", msg, multiplier)
      multiplier !  msg

    case msg: Value =>
      log.info("This is the ans to the multi question {}", msg.v)

  }

  def add(r: Int, l: Int) =
    r + l



}
