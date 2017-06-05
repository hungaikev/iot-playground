package practice.iot.application

import akka.actor._
import akka.testkit._
import org.scalatest._
import scala.concurrent.duration._

/**
  * Created by rodney on 6/4/17.
  */
class DeviceManagerActorSpec extends TestKit(ActorSystem("iot-system"))
  with FlatSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
