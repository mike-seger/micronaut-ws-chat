package loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class WebSocketTest extends Simulation {
  val users = sys.props.getOrElse("users", "300").toInt
  val repetitions = sys.props.getOrElse("repetitions", "5").toInt
  println(users+"")
  val httpProtocol: HttpProtocolBuilder = http
      .baseUrl("http://localhost:8881")
//      .wsBaseUrl("wss://echo.websocket.org")
      .wsBaseUrl("ws://localhost:8881/chat/room1")

//root is HTTP protocol and webSocket url is being app  ended to it

  val scene = scenario("testWebSocket")
    .exec(http("firstRequest")
    .get("/"))
    .exec(session => session.set("id", "user" + session.userId))
    .exec(ws("openSocket").connect("/${id}")
      .onConnected(
        repeat(repetitions, "i") {
          exec(ws("sendMessage").sendText("{\"content\":\"chat text ${id}\",\"contentType\":\"text\"}")
            .await(5)(ws.checkTextMessage("check1").check(regex(".*(Connected|Disconnected|chat text user).*")
//              .await(20)(ws.checkTextMessage("check1").check(regex(".*chat text ${id}-${i}.*")
//              .await(20)(ws.checkTextMessage("check1").check(regex(".*(Connected|Disconnected|chat text).*")
            )))
        }
      ))
// created custom checks for checking my response
   
//.exec(session => session{
//      println(session("myMessage").as[String])
//      session
//    })
//created the session for printing the response and type-casted it to String

// fails for some reason -> analyze
//    .exec(ws("closeConnection").close)
//terminating the current websocket connection

  setUp(scene.inject(atOnceUsers(users)).protocols(httpProtocol))
}