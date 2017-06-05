package practice.iot.application


import akka.actor._
import scala.concurrent.duration._
/**
  * Created by rodney on 6/5/17.
  */

object DeviceQueryGroup {

  def props(actorToDeviceId: Map[ActorRef,String],
            requestId: Long,
            requester: ActorRef,
            timeout: FiniteDuration): Props = {
    Props(new DeviceQueryGroup(actorToDeviceId,requestId,requester,timeout))
  }

  case object CollectionTimeout



}

class DeviceQueryGroup(actorToDeviceId: Map[ActorRef,String],
                       requestId: Long,
                       requester: ActorRef,
                       timeout: FiniteDuration
                      ) extends Actor with ActorLogging {
  import DeviceQueryGroup._
  import context.dispatcher

  val queryTimeoutTime = context.system.scheduler.scheduleOnce(timeout,self,CollectionTimeout)

  override def preStart(): Unit = {
    log.info("Starting the DeviceQuery Group actor and asking all devices for temperatures")
    actorToDeviceId.keysIterator.foreach { deviceActor =>
      context.watch(deviceActor)
      deviceActor ! Device.ReadTemperature(0)
    }
  }

  override def postStop(): Unit = {
    queryTimeoutTime.cancel()
  }

  override def receive: Receive = waitingForReplies(Map.empty,actorToDeviceId.keySet)

  def receivedResponse(deviceActor: ActorRef,
                       reading: DeviceGroup.TemperatureReading,
                       stillWaiting: Set[ActorRef],
                       repliesSoFar: Map[String, DeviceGroup.TemperatureReading]): Unit = {
    context.unwatch(deviceActor)

    val deviceId = actorToDeviceId(deviceActor)
    val newStillWaiting = stillWaiting - deviceActor

    val newRepliesSoFar = repliesSoFar + (deviceId -> reading)
     if (newStillWaiting.isEmpty) {
       requester ! DeviceGroup.RespondAllTemperatures(requestId, newRepliesSoFar)
       context.stop(self)
     } else {
       context.become(waitingForReplies(newRepliesSoFar, newStillWaiting))
     }
  }

  def waitingForReplies(repliesSoFar: Map[String,DeviceGroup.TemperatureReading],
                        stillWaiting: Set[ActorRef]): Receive = {
    case Device.RespondTemperature(0,valueOption) =>
      val deviceActor = sender()
      val reading = valueOption match {
        case Some(value) => DeviceGroup.Temperature(value)
        case None  => DeviceGroup.TemperatureNotAvailable
      }

      receivedResponse(deviceActor,reading,stillWaiting,repliesSoFar)

    case Terminated(deviceActor) =>
      receivedResponse(deviceActor, DeviceGroup.DeviceNotAvailable, stillWaiting, repliesSoFar)

    case CollectionTimeout =>
      val timedOutReplies =
        stillWaiting.map { deviceActor =>
          val deviceId = actorToDeviceId(deviceActor)
          deviceId -> DeviceGroup.DeviceTimeOut

        }
      requester ! DeviceGroup.RespondAllTemperatures(requestId,repliesSoFar ++ timedOutReplies)

      context.stop(self)

  }

}
