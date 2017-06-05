package practice.iot.application

import akka.actor._
import scala.concurrent.duration._

/**
  * Created by rodney on 6/4/17.
  */
object DeviceGroup {

  def props (groupId: String):Props = Props(new DeviceGroup(groupId))

  final case class RequestDeviceList(requestId: Long)
  final case class ReplyDeviceList(requestId: Long, ids: Set[String])


  sealed trait TemperatureReading
  final case class Temperature(value: Double) extends TemperatureReading
  case object TemperatureNotAvailable extends TemperatureReading
  case object DeviceNotAvailable extends TemperatureReading
  case object DeviceTimeOut extends TemperatureReading

  final case class RequestAllTemperatures(requestId: Long)
  final case class RespondAllTemperatures(requestId: Long, temperatures: Map[String,TemperatureReading])

}

class DeviceGroup(groupId:String) extends Actor with ActorLogging {
  import DeviceGroup._
  import DeviceManager._

  // To be able to look up child actors by their device IDs we will use a Map[String, ActorRef] .

  var deviceIdToActor = Map.empty[String,ActorRef]
  var actorToDeviceId = Map.empty[ActorRef,String]

  var nextCollectionId = 0L

  override def preStart(): Unit = log.info("DeviceGroup {} started", groupId)

  override def postStop(): Unit = log.info("DeviceGroup {} stopped", groupId)

  override def receive: Receive = {
    // A group has more work to do when it comes to registrations.
    // It must either forward the request to an existing child, or it should create one.

    case trackMsg @ RequestTrackDevice(`groupId`,_) =>
      deviceIdToActor.get(trackMsg.deviceId) match {
        case Some(deviceActor) =>
          deviceActor forward trackMsg
        case None =>
          log.info("Creating device actor for {}", trackMsg.deviceId)
          val deviceActor = context.actorOf(Device.props(groupId,trackMsg.deviceId), s"device-${trackMsg.deviceId}")
          context.watch(deviceActor)
          actorToDeviceId += deviceActor -> trackMsg.deviceId
          deviceIdToActor += trackMsg.deviceId -> deviceActor
          deviceActor forward trackMsg
      }

    case RequestDeviceList(requestId) =>
      sender() ! ReplyDeviceList(requestId, deviceIdToActor.keySet)

    case Terminated(deviceActor) =>
      val deviceId = actorToDeviceId(deviceActor)
      log.info("Device actor for {} has been terminated", deviceId)
      actorToDeviceId -= deviceActor
      deviceIdToActor -= deviceId

    case RequestAllTemperatures(requestId) =>
      context.actorOf(DeviceQueryGroup.props(
        actorToDeviceId = actorToDeviceId,
        requestId = requestId,
        requester = sender(),
        3.seconds
      ))

    case RequestTrackDevice(groupId,deviceId) =>
      log.warning("Ignoring TrackDevice request for {}. This actor is responsible for {}.", groupId,this.groupId)
  }

}
