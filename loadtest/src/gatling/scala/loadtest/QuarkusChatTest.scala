package loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

class QuarkusChatTest extends Simulation {
  val host = sys.props.getOrElse("host", "localhost")
  val port = sys.props.getOrElse("port", "8883").toInt
  val users = sys.props.getOrElse("users", "300").toInt
  val toUsers = sys.props.getOrElse("tousers", "1000").toInt
  val repetitions = sys.props.getOrElse("repetitions", "5").toInt
  val wsUrl = sys.props.getOrElse("wsurl", s"ws://${host}:${port}/chat/user")
  
  println(s"${users} / ${repetitions} / ${wsUrl}")
  val httpProtocol: HttpProtocolBuilder = http.wsBaseUrl(wsUrl)

  val scene = scenario("testWebSocket")
    .pause(1)
    .exec(session => session.set("id", session.userId))
    .exec(ws("openSocket").connect("${id}")
      .onConnected(
        repeat(repetitions, "i") {
          exec(
            ws("sendMessage")
              .sendText("User sent a text")
            .await(10)(
              ws.checkTextMessage("check1")
                .check(regex(".*"))
            )
          )
          .pause(100.milliseconds)
        }
      ))
//    .exec(ws("closeSocket").close)  

  setUp(scene.inject(
    atOnceUsers(users)
  ).protocols(httpProtocol))
  .maxDuration(1 minutes)
}
