package loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class MicronautChatTest extends Simulation {
  val users = sys.props.getOrElse("users", "300").toInt
  val toUsers = sys.props.getOrElse("tousers", "1000").toInt
  val repetitions = sys.props.getOrElse("repetitions", "5").toInt
  val wsUrl = sys.props.getOrElse("wsurl", "ws://localhost:8881/chat/room1")
  
  println(s"${users} / ${repetitions} / ${wsUrl}")
  val httpProtocol: HttpProtocolBuilder = http
      .wsBaseUrl(wsUrl)

  val scene = scenario("testWebSocket")
    .pause(1)
    .exec(session => session.set("id", "user"/* + session.userId*/))
    .exec(ws("openSocket").connect("/${id}")
      .onConnected(
        repeat(repetitions, "i") {
          exec(
            ws("sendMessage")
              .sendText("""
                {
                  "content": "chat text ${id}"
                  ,"contentType":"text"
                }
              """)
 //             .check(wsListen.within(30. seconds).until(1).regex(".*(Connected|Disconnected|chat text user).*").saveAs("name"))

          /*.exec(ws("check")*/
 //           .check(wsListen.within(10).until(1).jsonPath("$.content").saveAs("clientID")))
//            .check(wsListen.within(10 seconds)
//              .accumulate.jsonPath("$.content")
//              .regex(".*(Connected|Disconnected|chat text user).*")
//            )

            .await(10)(
//              wsListen.within(10).until(1).jsonPath("$.[2]").saveAs("clientID"))
              ws.checkTextMessage("check1")
                .check(regex(".*(Connected|Disconnected|chat text ${id}).*"))
   //             .check(regex(".*(chat text ${id}).*"))
            )
//            )
//          .check(wsListen.within(10 seconds).until(1).regex(".*").is("HOHO")))
          )
          .pause(1)
        }
      ))

  setUp(scene.inject(
    nothingFor(4)
    , atOnceUsers(users)
//    , constantUsersPerSec(users) during (20)
//    , rampUsersPerSec(1) to toUsers during (600)
  ).protocols(httpProtocol))
}
