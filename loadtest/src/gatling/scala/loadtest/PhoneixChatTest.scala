package loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

class PhoneixChatTest extends Simulation {
  val users = sys.props.getOrElse("users", "300").toInt
  val toUsers = sys.props.getOrElse("tousers", "1000").toInt
  val repetitions = sys.props.getOrElse("repetitions", "5").toInt
  val wsUrl = sys.props.getOrElse("wsurl", "ws://localhost:4000/socket")
  
  println(s"${users} / ${repetitions} / ${wsUrl}")
  val httpProtocol: HttpProtocolBuilder = http.wsBaseUrl(wsUrl)

  val scene = scenario("testWebSocket")
    .pause(1)
    .exec(session => session.set("id", session.userId))
    .exec(ws("openSocket").connect("/websocket?user_id=${id}")
      .onConnected(
        exec(ws("initialMessage")
          .sendText("""
            {"topic":"rooms:lobby","event":"phx_join","payload":{},"ref":"1"}
          """))
        .repeat(repetitions, "i") {
          exec(
            ws("sendMessage")
              .sendText("""
                {"topic":"rooms:lobby","event":"new:msg","payload":{"user":"","body":"message friom user: ${id}"},"ref":"2"}
              """)
            .await(10)(
              ws.checkTextMessage("check1")
//                .check(regex(".*(Connected|Disconnected|chat text).*"))
                .check(regex(".*"))
            )
          )
          .pause(100.milliseconds)
        }
      ))

  setUp(scene.inject(
    nothingFor(0)
    , atOnceUsers(users)
  ).protocols(httpProtocol))
}
