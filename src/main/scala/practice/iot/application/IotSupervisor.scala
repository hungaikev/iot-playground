package practice.iot.application



import akka.actor._

/**
  * Created by hungai on 6/3/17.
  */

object IotSupervisor {

  def props() : Props = Props(new IotSupervisor)

}

class IotSupervisor  extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("Iot Application started")

  override def postStop(): Unit = log.info("Iot Application started")

  // No need to handle ay messages

  override def receive: Receive = Actor.emptyBehavior

}
