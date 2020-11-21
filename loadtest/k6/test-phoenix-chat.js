import ws from 'k6/ws';
import { check } from 'k6';

var i;

export default function () {
  const userId = `user+${__VU}`;
  const url = 'ws://localhost:4000/socket/websocket?user_id='+userId;
  const params = { tags: { tag : 'micronaut-ws-chat' } };
  i=2;
  const res = ws.connect(url, params, function (socket) {

//{"topic":"phoenix","event":"heartbeat","payload":{},"ref":"4"}
    socket.on('open', function open() {
        socket.send({"topic":"rooms:lobby","event":"phx_join","payload":{},"ref":"1"});
        socket.setInterval(function timeout() {
            socket.send({"topic":"rooms:lobby","event":"new:msg",
              "payload":{"user": userId,"body":"message from user: "+userId},"ref":""+i});
            i=i+1;
        }, 1000);
    });
    

//    socket.on('message', (data) => console.log('Message received: ', data));
    socket.on('close', () => console.log('disconnected'));
  });

  check(res, { 'status is 101': (r) => r && r.status === 101 });
}
